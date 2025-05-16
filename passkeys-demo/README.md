# Passkeys Demo

## Setup

Using Passkeys with most providers requires secure HTTPS connections and does not work with the localhost domain.
Therefore, this sample is configured for https://server.local:8433 address.

To enable this local domain on your machine, you need to add the following entry to your `/etc/hosts` file:

```shell
127.0.0.1 server.local
```

To ensure a valid trusted HTTPS connection, you need a trusted X.509 certificate.
The existing server-keystore.p12 already contains a server certificate, but as you do not have a corresponding root
certificate installed, this is not trusted.

To replace the existing keystore with your own trusted certificates, you need the
tool [mkcert](https://github.com/FiloSottile/mkcert).
After installing the tool (see https://github.com/FiloSottile/mkcert?tab=readme-ov-file#installation) you can install
your local CA:

```shell
mkcert -install
```

After installing the local CA with the root certificate, you can overwrite the existing keystore.
Make sure you open the terminal in the subdirectory `src/main/resources` of the module `passkeys-demo` inside this
repository.

```shell
mkcert -p12-file server-keystore.p12 -pkcs12 127.0.0.1 localhost server.local
```

## Running the demo

Just start the application in your IDE or using the maven spring boot plugin.
Now navigate your browser to https://server.local:8443.
First, you need to log in with the user credentials `user/password`.
Now you should see a welcome page with the following links:

* Register: Click on this link to register your user for a Passkey (using Keychain on Mac, Browser, 1Password, etc.)
* Hello: This calls a testing API that welcomes you as a user
* Log Out: Force a logout, i.e., to test logging in using a passkey

### Reference Documentation

For further reference, please consider the following sections:

* [Spring Security](https://docs.spring.io/spring-boot/docs/3.3.3/reference/htmlsingle/index.html#web.security)
* [Spring Security Webauthn](https://github.com/rwinch/spring-security-webauthn)

