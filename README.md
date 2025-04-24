This repo contains backend services for dmvboardgames.com. Data is stored in PostgreSQL using Supabase.

## Contribution guidelines

Go to the following page to view general development guidelines for the project: https://github.com/Create-Third-Places

Also, follow the guidelines below:
- If you are updating the structure of an existing endpoint, or adding a new endpoint, make sure it is covered by a API test. The API tests are for verifying that the API is returning responses with the correct structure and response code. API tests should be added to the [api-testing repo](https://github.com/Create-Third-Places/api-testing)
- Logic should be covered by unit or integration tests. 
  
### Running locally in Linux

- Compile the API using `mvn clean package`. 
- Make sure the database password is set in your `.bashrc` file
- Run using `java -jar target/app.jar`

The API will be available at http://localhost:7070/

To compile without running unit tests, run `mvn clean package -DskipTests`

### Running integration tests

- Download and run a Postgres Docker container with `./startTestDatabase.sh`
- Run tests in the app.database folder.
  
### Code formatter

- Run `mvn prettier:write`.

### Production deployments

Production deployments will run with the latest changes from the release branch. Once a build image has been created, a deployment will then be manually triggered using the Digital Ocean app platform.

