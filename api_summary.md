# API Endpoint Summary

Here is a summary of the API endpoints:

- **GET /users/{id}**: Retrieves user details for a given user ID.
- **POST /users**: Creates a new user.
- **PUT /users/{id}**: Updates user details for a given user ID.
- **DELETE /users/{id}**: Deletes a user with a given user ID.
- **GET /users**: Retrieves a list of users with pagination support.
- **GET /users/{id}/addresses**: Retrieves all addresses for a given user ID.
- **GET /users/{userId}/addresses/{addressId}**: Retrieves a specific address for a given user ID and address ID.
- **GET /users/email-verification?token={token}**: Verifies a user's email address using a token.
