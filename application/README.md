# Deployment and installation
This project is ent-enabled so you can use the ent cli (https://dev.entando.org/next/docs/reference/entando-cli.html) to perform the full build and deployment sequence.

### Setup the project directory.
1. Prepare the bundle directory: `cp -r bundle_src bundle`
2. Initialize the project: `ent prj init` (use entando-hub to match the bundle name in the integration environment)
3. Initialize publication: `ent prj pbs-init` (requires the git bundle repo url)

### Publish the bundle.
1. Build: `ent prj build` (build the frontend and backend) or `ent prj fe-build -a` (to just build the frontend, including changes from bundle_src)
2. Publish: `ent prj pub` or `ent prj fe-push` (publish all or just the frontend)
3. Deploy (after connecting to k8s): `ent prj deploy`
4. Install the bundle via 1) App Builder, 2) `ent prj install`, or 3) `ent prj install --conflict-strategy=OVERRIDE` on subsequent installs.
5. Iterate steps 1-4 to publish new versions.

# Development tips
## Roles
* Three roles are currently created for the Hub project - eh-author, eh-manager, eh-admin

## Local Frontend only
This style of development is for a developer who only wants to run the frontend code locally. See the README under `ui/widgets/cp-widgets-dir/cp-widgets/README.md` for the settings required to do this.

## Local Full Stack
This style of development is for a developer who wants to run keycloak, frontend, and backend all locally. Docker is required for this setup.

### Full stack setup
* Keycloak: http://localhost:9080/auth/
* OpenAPI/Swagger: http://localhost:8081/swagger-ui.html
* H2 console: http://localhost:8081/h2-console/
* JDBC connector for H2: `jdbc:h2:~/<YOUR_PROJECT_ROOT>/application/entando-data/testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE`
* React FE: http://localhost:3000/
* Use the client web_app when authorizing the microservices
* You'll need to update the `ui/widgets/eh-widgets-dir/eh-widgets/.env` settings to match your environment before starting the React app. See the `.env.template` file for a possible local configuration.
* As of 2021-10-14 you'll need to add at least one category in the database before you can create a bundle group via the FE

### Local testing
You can use the following commands from the application folder to run the local stack 
* `ent prj xk start` - or stop to shutdown keycloak again. If docker-compose is only available to a superuser, you may need to create the `keycloak-db/content` directory as your regular user (`mkdir -p src/main/docker/keycloak-db/content`) and then start keycloak via `sudo docker-compose -f src/main/docker/keycloak.yml up`. This will work around permission issues mounted the h2 data store. 
* `ent prj be-test-run` - to run the microservice
  persistent database (entando-data) with data preload (data.sql) The
  h2 console can be accessed here: http://localhost:8081/h2-console/
* `ent prj fe-test-run` - to run the React frontend

### Misc
* Four users are included in the keycloak realm config
  * admin/admin - eh-admin role plus has access to the realm to manage users
  * author/author - eh-author role
  * manager/manager - eh-manager role
  * user/user - regular user
* Removing the src/main/docker/keycloak-db directory will result in the realm from src/main/docker/realm-config being reloaded on the next restart.

### Set up environment variable in .yaml file
Please Add/Update the base url `HUB_GROUP_DETAIL_BASE_URL` in the .yaml files.
This will be used to open the detailed bundle group page on hub.
* `Example:`
* `HUB_GROUP_DETAIL_BASE_URL: http://hubdev.okd-entando.org/entando-de-app/en/test.page#/`
* `HUB_GROUP_DETAIL_BASE_URL: http://localhost:3000/#/`


## Local alternative - running microservice in docker
You can also choose to run the current microservice in a docker container. 
TODO: You'll need to provide environment variables to enable keycloak integration in this mode.
* `docker run -d -p 8081:8081 --name entando-hub-catalog entandopsdh/entando-hub-catalog-ms:0.0.1-SNAPSHOT`
* `docker stop entando-hub-catalog`
* `docker start entando-hub-catalog`
