package com.thanlinardos.spring_enterprise_library.spring_cloud_security.aspect;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.api.service.PrivilegedResourceService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.PrivilegedResource;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils.AspectUtils;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils.AuthenticationUtils;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils.AspectUtils.getSimpleClassName;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Slf4j
public class AuthorizationAspectHelper {

    private static final String OWNER = "owner";

    private AuthorizationAspectHelper() {
    }

    public static Object authorizeControllerOperation(ProceedingJoinPoint proceedingJoinPoint, PrivilegedResourceService privilegedResourceService) throws Throwable {
        final String signature = proceedingJoinPoint.getSignature().toShortString();
        boolean isModifyingOperation = authorizeModifyingOperation(proceedingJoinPoint, signature, privilegedResourceService);
        Object result = proceedingJoinPoint.proceed();
        if (result instanceof ResponseEntity<?> responseEntity && responseEntity.getBody() != null) {
            ResponseEntity<List<PrivilegedResource>> authorizedResources = authorizePrivilegedResponse(responseEntity, signature, privilegedResourceService);
            if (authorizedResources != null) {
                return authorizedResources;
            }
            if (isModifyingOperation) {
                logAuthorizedResponse(signature, responseEntity.getBody(), responseEntity.getStatusCode(), getSimpleClassName(proceedingJoinPoint.getTarget().getClass()));
            }
        }

        return result;
    }

    @Nullable
    private static ResponseEntity<List<PrivilegedResource>> authorizePrivilegedResponse(ResponseEntity<?> responseEntity, String signature, PrivilegedResourceService privilegedResourceService) throws IllegalAccessException {
        switch (responseEntity.getBody()) {
            case PrivilegedResource resource when !privilegedResourceService.canCurrentPrincipalAccessResource(resource) ->
                    throwIllegalAccessToResource(signature, resource);
            case Optional<?> optional when optional.isPresent() && optional.get() instanceof PrivilegedResource resource && !privilegedResourceService.canCurrentPrincipalAccessResource(resource) ->
                    throwIllegalAccessToResource(signature, resource);
            case Collection<?> collection when collection.iterator().hasNext() && collection.iterator().next() instanceof PrivilegedResource -> {
                List<PrivilegedResource> authorizedResources = filterPrivilegedResources(collection, privilegedResourceService);
                log.info("[{}][{}] Authorization Filtering Collection of Resources: {} -> {}",
                        responseEntity.getStatusCode(),
                        signature,
                        collection.size(),
                        authorizedResources.size());
                log.debug("{} -> {}", getPrivilegedResourceNames(collection), getPrivilegedResourceNames(authorizedResources));
                return new ResponseEntity<>(authorizedResources, responseEntity.getHeaders(), responseEntity.getStatusCode());
            }
            case PrivilegedResource resource -> logAuthorizedResponse(signature, resource, responseEntity.getStatusCode(), getSimpleClassName(resource));
            case Optional<?> optional when optional.isPresent() && optional.get() instanceof PrivilegedResource resource -> logAuthorizedResponse(signature, resource, responseEntity.getStatusCode(), getSimpleClassName(resource));
            case null, default -> {
                // no need to authorize
            }
        }
        return null;
    }

    public static Object authorizeServiceMethod(ProceedingJoinPoint proceedingJoinPoint, PrivilegedResourceService privilegedResourceService) throws Throwable {
        final String signature = proceedingJoinPoint.getSignature().toShortString();
        if (isTransactional(proceedingJoinPoint)) {
            for (Object arg : proceedingJoinPoint.getArgs()) {
                if (arg instanceof PrivilegedResource resource && !privilegedResourceService.canCurrentPrincipalAccessResource(resource)) {
                    throwIllegalAccessToResource(signature, resource);
                }
            }
        }
        Object result = proceedingJoinPoint.proceed();
        switch (result) {
            case PrivilegedResource resource when !privilegedResourceService.canCurrentPrincipalAccessResource(resource) ->
                    throwIllegalAccessToResource(signature, resource);
            case Optional<?> optional when optional.isPresent() && optional.get() instanceof PrivilegedResource resource && !privilegedResourceService.canCurrentPrincipalAccessResource(resource) ->
                    throwIllegalAccessToResource(signature, resource);
            case Collection<?> collection when collection.iterator().hasNext() && collection.iterator().next() instanceof PrivilegedResource -> {
                List<PrivilegedResource> authorizedResources = filterPrivilegedResources(collection, privilegedResourceService);
                log.info("[{}] Authorization Filtering Collection of Resources: {} -> {}",
                        signature,
                        collection.size(),
                        authorizedResources.size());
                log.debug("[{}] {} -> {}", signature, getPrivilegedResourceNames(collection), getPrivilegedResourceNames(authorizedResources));
                return authorizedResources;
            }
            case PrivilegedResource resource -> logAuthorizedResource(signature, getSimpleClassName(resource), resource);
            case Optional<?> optional when optional.isPresent() && optional.get() instanceof PrivilegedResource resource -> logAuthorizedResource(signature, getSimpleClassName(resource), resource);
            case null, default -> {
                // no need to authorize
            }
        }

        return result;
    }

    private static void logAuthorizedResource(String signature, String className, Object resource) {
        log.info("[{}] Authorized Resource: {}", signature, className);
        log.debug("[{}] result: {}", signature, resource);
    }

    public static void refreshOwnerSecurityContext(JoinPoint jp, List<GrantedAuthority> allAuthorities) {
        if (getContext().getAuthentication() != null) {
            return;
        }
        log.info("RefreshOwnerSecurityContext for task: {} with schedule: {}", jp.getSignature().toShortString(), AspectUtils.getAnnotationValues(jp, Scheduled.class));
        Jwt ownerPrincipal = Jwt.withTokenValue(OWNER)
                .header("alg", "none")
                .claim("sub", OWNER)
                .claim("email", "owner@email.com")
                .claim("resource_access", Map.of("account", OWNER, "eazybankapi", "client"))
                .claim("client_id", "eazybankapi")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(ownerPrincipal, "pass", allAuthorities);
        getContext().setAuthentication(authentication);
    }

    private static boolean authorizeModifyingOperation(ProceedingJoinPoint proceedingJoinPoint, String signature, PrivilegedResourceService privilegedResourceService) throws IllegalAccessException {
        boolean isModifyingOperation = isModifyingOperation(proceedingJoinPoint);
        if (isModifyingOperation) {
            List<String> modifiedResourceTypes = new ArrayList<>();
            for (Object arg : proceedingJoinPoint.getArgs()) {
                if (arg instanceof PrivilegedResource resource) {
                    modifiedResourceTypes.add(resource.getClass().getSimpleName());
                    if (!privilegedResourceService.canCurrentPrincipalAccessResource(resource)) {
                        throwIllegalAccessToResource(signature, resource);
                    }
                }
            }
            logAuthorizedModifyingRequest(signature, proceedingJoinPoint.getArgs(),
                    getModifyingOperation(proceedingJoinPoint),
                    modifiedResourceTypes);
        }
        return isModifyingOperation;
    }

    private static void logAuthorizedResponse(String signature, Object body, HttpStatusCode statusCode, String className) {
        log.info("[{}][{}] Authorized Response: {}", statusCode, signature, className);
        log.debug("[{}][{}] body: {}", statusCode, signature, body);
    }

    private static void logAuthorizedModifyingRequest(String signature, Object[] args, HttpMethod method, List<String> classNames) {
        if (classNames.isEmpty()) {
            log.warn("[{}][{}] Skipped authorization for modifying request with args: {}", method, signature, args);
        } else {
            log.info("[{}][{}] Authorized Request: {}", method, signature, classNames);
            log.debug("[{}][{}] args: {}", method, signature, args);
        }
    }

    protected static void throwIllegalAccessToResource(String methodSignature, PrivilegedResource resource) throws IllegalAccessException {
        throw new IllegalAccessException("["
                + methodSignature
                + "] Unauthorized Access to Resource: "
                + resource.getPrincipalName()
                + " for user: "
                + AuthenticationUtils.getPrincipalNameFromAuthentication(getContext().getAuthentication()));
    }

    protected static List<PrivilegedResource> filterPrivilegedResources(Collection<?> collection, PrivilegedResourceService privilegedResourceService) {
        return collection.stream()
                .map(PrivilegedResource.class::cast)
                .filter(privilegedResourceService::canCurrentPrincipalAccessResource)
                .toList();
    }

    protected static boolean isTransactional(ProceedingJoinPoint proceedingJoinPoint) {
        return AspectUtils.hasAnnotation(proceedingJoinPoint, Transactional.class);
    }

    protected static boolean isModifyingOperation(ProceedingJoinPoint proceedingJoinPoint) {
        return AspectUtils.hasAnnotation(proceedingJoinPoint, PostMapping.class)
                || AspectUtils.hasAnnotation(proceedingJoinPoint, PutMapping.class)
                || AspectUtils.hasAnnotation(proceedingJoinPoint, DeleteMapping.class)
                || AspectUtils.hasAnnotation(proceedingJoinPoint, PatchMapping.class)
                || AspectUtils.hasAnnotationWithProperty(proceedingJoinPoint, RequestMapping.class, "method",
                List.of(RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH));
    }

    protected static HttpMethod getModifyingOperation(ProceedingJoinPoint proceedingJoinPoint) {
        if (AspectUtils.hasAnnotation(proceedingJoinPoint, PostMapping.class)) {
            return HttpMethod.POST;
        } else if (AspectUtils.hasAnnotation(proceedingJoinPoint, PutMapping.class)) {
            return HttpMethod.PUT;
        } else if (AspectUtils.hasAnnotation(proceedingJoinPoint, DeleteMapping.class)) {
            return HttpMethod.DELETE;
        } else if (AspectUtils.hasAnnotation(proceedingJoinPoint, PatchMapping.class)) {
            return HttpMethod.PATCH;
        } else {
            return (HttpMethod) (Objects.requireNonNull(AspectUtils.getAnnotationValue(proceedingJoinPoint, RequestMapping.class, "method")));
        }
    }

    protected static List<String> getPrivilegedResourceNames(Collection<?> collection) {
        return collection.stream()
                .map(PrivilegedResource.class::cast)
                .map(PrivilegedResource::getPrincipalName)
                .toList();
    }
}
