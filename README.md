This repo contains backend services for dmvboardgames.com. Data is stored in PostgreSQL using Supabase.

# Setup guide

## Prequisitcs
- Clone the [backend repo](https://github.com/gatherspiel/backend)
- Compile the API using `mvn clean package`. 
- Set the AUTH_URL environment variable to `http://localhost:54321/auth/v1/`

- Clone the [database repo](https://github.com/free-gather/database), setup the Supabase cli, and then run 
   `npx supabase start` to start the database.
- In the UI .env file and set the following values
    VITE_LOCAL_AUTH_KEY: `service_role_key` value that appears when starting the database.
    AUTH_URL:'http://localhost:54321/auth/v1/'
- Run src/test/java/app/database/utils/local/InitLocalDb.java. This will initialize a test database.
- Run using `java -jar target/app.jar`

The API will be available at http://localhost:7070/


# Contribution guidelines

Go to the following page to view general development guidelines for the project: https://github.com/Create-Third-Places

Also, follow the guidelines below:
- If you are updating the structure of an existing endpoint, or adding a new endpoint, make sure it is covered by a API test. The API tests are for verifying that the API is returning responses with the correct structure and response code. API tests should be added to the [api-testing repo](https://github.com/Create-Third-Places/api-testing)
- Logic should be covered by unit or integration tests. 


### Viewing automated emails related to authentication

Navigate to local mail server at http://localhost:54324
### Creating a new user

- Navigate to http://localhost:54323
- Go to the authentication dashboard and create a new user with the email `test@freegather.org` and the password `test`

### Running integration tests

- Download and run a Postgres Docker container with `./startTestDatabase.sh`
- Run tests in the app.database folder.
  
### Code formatter

- Run `mvn prettier:write`.

### Production deployments

Production deployments will run with the latest changes from the release branch. Once a build image has been created, a deployment will then be manually triggered using the Digital Ocean app platform.

