# Employees management

Fullstack employee management project made with Spring Boot & Angular.

## Authentication

Authentication is implemented using a java implementation of JSON Web Token (JWT) (RFC 7519) provided by Auth0.
There are 2 types of tokens - **access token**, that is valid for 7 hours and is refreshed when expired & **refresh token**, that is valid for 7 days and is used to refresh access token.

When access token is expired, it is automatically (authentication interceptor) updated by sending refresh token to the backend. When a refresh token expires - user is logged out and must login to get a new one.
