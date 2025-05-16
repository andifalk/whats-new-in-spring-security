# Spring Authorization Server

This repository part contains a demo of the Spring Authorization Server using the latest features, i.e., MultiTenancy.

## Run the demo

1. Start the application using the following command:

   ```bash
   ./mvnw spring-boot:run
   ```
   This will start the Spring Authorization Server on port 9000.
   If you specify the profile `multi-tenancy`, the server will start with multitenancy (two different issuers).

2. Access the following URL to look up the OpenID configuration:

   ```bash
    http://localhost:9000/.well-known/openid-configuration
    ```
   This URL contains the issuer, authorization endpoint, token endpoint, and JWKs URI.

   The issuer is the unique identifier for the authorization server. The authorization endpoint is used to initiate the
   OAuth2 authorization flow. The token endpoint is used to get access tokens and refresh tokens.

   If you have enabled the MultiTenancy feature, you can access the OpenID configuration for the different issuers by
   appending `/issuer1` or `/issuer2` to the URL. This allows you to retrieve tokens for different tenants.

    ```bash
    http://localhost:9000/issuer1/.well-known/openid-configuration
    ```

3. Access the following URL to look up the JWKs:
   The JWKs URI is used to retrieve the public keys for verifying the JWT tokens.

   ```bash
    http://localhost:9000/jwks
    http://localhost:9000/issuer1/jwks
    http://localhost:9000/issuer2/jwks
    ```

### References

For further reference, please consider the following sections:

* [OAuth2 Authorization Server](https://docs.spring.io/spring-boot/docs/3.3.3/reference/htmlsingle/index.html#web.security.oauth2.authorization-server)
* [Spring Security](https://docs.spring.io/spring-boot/docs/3.3.3/reference/htmlsingle/index.html#web.security)
