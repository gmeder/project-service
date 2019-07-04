# Run project locally

Go to mongo folder

    $ cd mongodb

 Create mongo container

    $ docker run --name mongodb-project \
    -e MONGO_INITDB_ROOT_USERNAME=mongo \
    -e MONGO_INITDB_ROOT_PASSWORD=mongo \
    -e MONGO_INITDB_DATABASE=projectdb \
    --mount type=bind,source="$(pwd)"/mongo-init.js,target=/docker-entrypoint-initdb.d/db-init.js \
    -p 27017:27017 -d mongo
    
 Launch project with maven (root folder)
 
	$ mvn vertx:run
	
# Test locally

getProducts

	$ curl http://localhost:8080/projects
	
getProductById

	$ curl http://localhost:8080/projects/11111

getProductsByStatus

	$ curl http://localhost:8080/projects/status/open
	$ curl http://localhost:8080/projects/status/cancelled
	
# Run project on Openshift

  Create Mongo database

	$ oc new-app --name mongodb-project-service \
	-e MONGO_INITDB_ROOT_USERNAME=mongo \
	-e MONGO_INITDB_ROOT_PASSWORD=mongo \
	-e MONGO_INITDB_DATABASE=projectdb \
	https://github.com/gmeder/mongodb-projects.git \
	--strategy=docker
	
Create ConfigMap

	$ cd project-service
	$ oc create configmap project-service --from-file=etc/app-config.yml
	$ oc policy add-role-to-user view -z default
	
Deploy project

	$ cd project-service
	$ mvn clean fabric8:deploy -Popenshift -DskipTests
	
