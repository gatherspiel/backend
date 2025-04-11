#!/bin/bash


sh ./startTestDatabase.sh
cd ..
mvn clean package -DskipTests
java -jar target/app.jar