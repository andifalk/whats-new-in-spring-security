# One-Time Token Demo

This application shows the One-Time Token Login of Spring Security with storing the Token in the database.

## Prerequisites

You need to have the following installed:

- Java 21 or later
- Docker (only for the Mailserver)

## Run the demo

1. Start the application using the following command:

   ```bash
   ./mvnw spring-boot:run
   ```
2. Request a token by accessing the following URL and use the email `test@example.com`:

   http://localhost:8080/login

3. Access the following URL to verify the email containing themagic link:

   http://localhost:1080

4. Copy the link in the email to the web browser to log in.
5. You will be redirected to the application (http://localhost/api/demo) with a success message.

## Important Note

You may look up the token in the database using the following SQL query by opening the H2 console
at http://localhost:8080/h2-console and use `jdbc:h2:mem:testdb` as the JDBC URL.

## Reference Documentation

For further reference, please consider the following sections:

* [Spring Security One-Time Token](https://docs.spring.io/spring-security/reference/6.5/servlet/authentication/onetimetoken.html)
* [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
