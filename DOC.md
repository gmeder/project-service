# Service documentation

## Service design

The project is divided in 3 layers: API, Model and Service

- API: Contains the Verticl definition as API (Routes, HTTP Status, Request/Response types)
- Model: Contains the models used in the database and as response of the service.
- Service: Business logic implementation. Database lookup

## Service definition

The service has its own Swagger file in the folder src/main/resources. The yaml has been modeled and generated from https://openapi.design/

This yaml is then used from swagger-codegen to generate a http client for the gateway service. The client project has its own repo and documentation

https://github.com/gmeder/project-api-client.git

The service has an extra method addProject to add a new project

## Data definition

The service has a corresponding database Dockerfile that creates the mongodb container and prepoluate it with some testing data

https://github.com/gmeder/mongodb-projects.git

## Run service

How to run and test the service is described in README (Openshift and local)

## Service info

- Service is running on port 8081
- Service connect to mongodb *localhost* on port *27017* with creds *mongo/mongo* and database *projectdb*
- The project has a route http://project.assignment-gmeder.apps.na311.openshift.opentlc.com/
