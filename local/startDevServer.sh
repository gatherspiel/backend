#!/bin/bash


sh ./startTestDatabase.sh
cd ..
export AUTH_URL='http://localhost:54321/auth/v1/'
mvn clean package -DskipTests
java -jar target/app.jar