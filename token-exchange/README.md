# OAuth2 Token Exchange Demo

This repository directory contains a demo of the OAuth2 Token Exchange feature in Spring Security and Spring
Authorization Server.

It includes three modules:

1. **token-exchange-client**: A Spring Boot application that acts as an OAuth2 client.
2. **token-exchange-resource-server**: A Spring Boot application that acts as an OAuth2 resource server performing the
   token exchange and then calls the final target resource server.
3. **target-resource-server**: A Spring Boot application that acts as a target resource server that is called with the
   exchanged token.

## Scenario

The scenario is as follows:

1. The client application (http://localhost:9093) requests an access token from the authorization
   server (http://localhost:9000).
2. The client application uses the access token to call the first resource server (http://localhost:9094).
3. The first resource server performs a token exchange with the authorization server (http://localhost:9000) to obtain a
   new access token for the
   target resource server (http://localhost:9095).

## Reference Documentation

For further reference, please consider the following sections:

* [OAuth2 Resource Server](https://docs.spring.io/spring-boot/3.5.0-RC1/reference/web/spring-security.html#web.security.oauth2.server)
* [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
