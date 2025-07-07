This repo contains backend services for dmvboardgames.com. Data is stored in PostgreSQL using Supabase.

# Setup guide

## Prequisitcs
- Clone the [backend repo](https://github.com/gatherspiel/backend)
- Clone the [database repo](https://github.com/gatherspiel/database)
- Install the following dependencies
  - OpenJDK version 17.
  - [Maven 3.8.7](https://maven.apache.org/install.html) or a later version.
  - [Docker 27.5.1](https://docs.docker.com/engine/install/) or a later version. 

## Setup the database

- Navigate to the database repo and run the command `npx supabase start'

## Setup the backend
- Run src/test/java/app/database/utils/local/InitLocalDb.java. This will initialize a database with test data.

- Compile the API using `mvn clean package`. 
- Set the AUTH_URL environment variable to `http://localhost:54321/auth/v1/`
- Run `mvn clean package -DskipTests`


## Setting up auth for the UI.
- In the UI .env file and set the following values
    VITE_LOCAL_AUTH_KEY: `service_role_key` value that appears when starting the database.
    VITE_LOCAL_AUTH_URL:'http://localhost:54321/auth/v1/'
  
## Create an admin account for local development

- In the UI, register a user with the email test@gatherspiel.com and the password 123456.
- Navigate to the user table in the public schema. For the user with the email test@gatherspiel,com,
  set the user_role_level to site_admin and set the "IS_ACTIVE" flag to true.


# Contribution guidelines

Go to the following page to view general development guidelines for the project: https://github.com/Create-Third-Places

Also, follow the guidelines below:
- If you are updating the structure of an existing endpoint, or adding a new endpoint, make sure it is covered by a API test. The API tests are for verifying that the API is returning responses with the correct structure and response code. API tests should be added to the [api-testing repo](https://github.com/Create-Third-Places/api-testing)
- Logic should be covered by unit or integration tests. 


### Running the backend
- Run using `java -jar target/app.jar`. The API will be available at http://localhost:7070/
  
### Viewing automated emails related to authentication

Navigate to local mail server at http://localhost:54324


### Running integration tests

- Download and run a Postgres Docker container with `./startTestDatabase.sh`
- Run tests in the app.database folder.
  
### Code formatter

- Run `mvn prettier:write`.

### Production deployments

Production deployments will run with the latest changes from the release branch. Once a build image has been created, a deployment will then be manually triggered using the Digital Ocean app platform.

