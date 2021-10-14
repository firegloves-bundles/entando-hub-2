# Deployment and installation

build docker image:
./prepareDockerImage.sh

actuator enabled by default:
http://<url>/actuator/health

swagger:
http://127.0.0.1:8081/swagger-ui.html

h2 console
http://localhost:8081/h2-console/
jdbc:h2:./data/testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE

TO RUN
docker run -d -p 8081:8081 --name entando-hub-catalog germanogiudici/entando-hub-catalog-ms:0.0.1-SNAPSHOT

docker stop entando-hub-catalog
docker start entando-hub-catalog


BUNDLE PUBLICATION
 create the bundle folder and copy all the bundle_src files in

ent prj init
ent prj build
ent prj pbs-init
ent prj fe-push (--force)

ent prj generate-cr | ent kubectl apply -n entando -f -

or ent deploy / ent install 


BE
https://<entando-url>/entando-hub-api/swagger-ui.html

# Development tips
## Roles
* Three roles are currently created for the Hub project - eh-author, eh-manager, eh-admin

## Local testing of the project
You can use the following commands from this folder to test the microservices 
* `ent prj keycloak start` - or stop to shutdown keycloak again.
* `ent prj be-test-run` - to run the microservice
* `ent prj fe-test-run` - to run the React frontend

### Local setup
* Access Keycloak at http://localhost:9080/auth/
* Access Swagger at http://localhost:8081/swagger-ui.html
* Access the React FE via http://localhost:3000/ 
* Use client web_app when authorizing the microservices
* As of 2021-10-14 this time you'll need to add at least one category via the API before you can create a bundle group via the FE

## Misc
* Four users are included in the keycloak realm config
  * admin/admin - eh-admin role plus has access to the realm to manage users
  * author/author - eh-author role
  * manager/manager - eh-manager role
  * user/user - regular user
* Removing the src/main/docker/keycloak-db directory will result in the realm from src/main/docker/realm-config being reloaded on the next restart.
