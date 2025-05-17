# Error Detection for multiple Security Filter Chains

This repository directory contains a demo of the error detection for multiple security filter chains in Spring Security.

## Scenario

To demonstrate the error detection for multiple security filter chains, we have created a simple Spring Boot application
with two security filter chains (one for actuator and one for API).

In the project you find two security configurations:

* Valid one: `WebSecurityConfiguration` class
* Invalid one: `InvalidWebSecurityConfiguration` class

1. You may start the application with the valid configuration and see that everything works as expected.
2. You may start the application with the spring profile `invalid` to trigger the invalid configuration and see that an
   error is thrown during startup.

### Reference Documentation

For further reference, please consider the following sections:

* [Spring Boot Actuator](https://docs.spring.io/spring-boot/3.5.0-RC1/reference/actuator/index.html)
* [Spring Security](https://docs.spring.io/spring-boot/3.5.0-RC1/reference/web/spring-security.html)

