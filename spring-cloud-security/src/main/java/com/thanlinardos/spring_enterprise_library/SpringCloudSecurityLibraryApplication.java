package com.thanlinardos.spring_enterprise_library;

import org.springframework.boot.SpringApplication;

/**
 * Entry point for the Spring Cloud Security Library application.
 */
public class SpringCloudSecurityLibraryApplication {

    /**
     * Default constructor.
     */
    public SpringCloudSecurityLibraryApplication() {
        // Default constructor
    }

    /**
     * The main method to run the Spring Boot application.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(HttpsClientLibraryApplication.class, args);
    }
}
