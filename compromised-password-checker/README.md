# Compromised Password Checker Demo

This is a simple Spring Boot application that demonstrates how to check if a password has been compromised using the
`HaveIBeenPwnedRestApiPasswordChecker`.

## Run the Demo

1. Start the application using the following command:

   ```bash
   ./mvnw spring-boot:run
   ```

   This will start the application on port 8080.

## API Endpoints

| Endpoint         | Description                                                 |
|------------------|-------------------------------------------------------------|
| /me              | Get infos for current authenticated user                    |
| /register        | Registers new user and checks password for required policy  |
| /change-password | Changes password for existing user (checks password policy) |
| /check-password  | Checks if given password is compromised                     |

## Access the following URL to check if a password has been compromised:

   ```http request
   POST http://localhost:8080/check-password
    Content-Type: application/json

   {
     "password": "password"
   }
   ```

This will check if the password `querty123456` has been compromised. If it has, the response will be:

    ```bash
    Given password is compromised!
    ```

## Call the register-user endpoint with a POST request

   ```http request
   POST http://localhost:8080/register
    Content-Type: application/json

   {
     "username": "myuser",
     "password": "querty123456",
     "email": "myuser@example.com"
   }
   ```

## Reference Documentation

For further reference, please consider the following sections:

* [Spring Security](https://docs.spring.io/spring-boot/3.5.0-RC1/reference/web/spring-security.html)

