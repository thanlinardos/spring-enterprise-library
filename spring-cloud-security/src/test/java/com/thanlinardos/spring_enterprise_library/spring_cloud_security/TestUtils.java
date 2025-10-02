package com.thanlinardos.spring_enterprise_library.spring_cloud_security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.aop.framework.ReflectiveMethodInvocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TestUtils {

    /**
     * Creates a dummy {@link JoinPoint} for the given method.
     *
     * @param method the method to create the join point for.
     * @return a dummy {@link JoinPoint} for the given method.
     */
    public static JoinPoint createDummyJoinPointForMethod(Method method) {
        MethodInvocationProceedingJoinPoint joinPoint = createDummyJoinPoint(method);
        setJoinPointSignature(joinPoint, getMethodSignature(method));
        return joinPoint;
    }

    private static MethodInvocationProceedingJoinPoint createDummyJoinPoint(Method method) {
        return new MethodInvocationProceedingJoinPoint(Objects.requireNonNull(TestUtils.createDummyReflectiveMethodInvocation(method)));
    }

    private static ReflectiveMethodInvocation createDummyReflectiveMethodInvocation(Method method) {
        try {
            Constructor<ReflectiveMethodInvocation> constructor = ReflectiveMethodInvocation.class
                    .getDeclaredConstructor(Object.class, Object.class, Method.class, Object[].class, Class.class, List.class);
            constructor.setAccessible(true);
            return constructor.newInstance(null, null, method, null, null, null);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setJoinPointSignature(MethodInvocationProceedingJoinPoint joinPoint, MethodSignature methodSignature) {
        Field signatureField = Arrays.stream(MethodInvocationProceedingJoinPoint.class.getDeclaredFields())
                .filter(field -> field.getName().equals("signature"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No field named 'signature' found in MethodInvocationProceedingJoinPoint class"));
        signatureField.setAccessible(true);
        try {
            signatureField.set(joinPoint, methodSignature);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodSignature getMethodSignature(Method method) {
        try {
            Class<?> clazz = Class.forName("org.aspectj.runtime.reflect.MethodSignatureImpl");
            Constructor<?> constructor = clazz.getDeclaredConstructor(int.class, String.class, Class.class, Class[].class, String[].class, Class[].class, Class.class);
            constructor.setAccessible(true);
            return (MethodSignature) constructor.newInstance(method.getModifiers(), method.getName(), method.getDeclaringClass(), method
                    .getParameterTypes(), new String[method.getParameterTypes().length], method.getExceptionTypes(), method
                    .getReturnType());
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
