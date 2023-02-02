# Entando Hub Bundle

## Overview
This is the parent repository for the Entando Hub project which currently contains two deployable sub-projects
* `application` which has the MFEs and microservices for the Entando Hub application
* `content` which has the non-compiled components such as pages, page templates, and fragments.

## Getting Started

### Deploy on Entando App

Move inside one of the two folders `application` and `content`, which contain the bundles.<br/>
You can build,deploy and install the bundles on Entando with commands of ent.

```
ent bundle pack
ent bundle publish
ent bundle deploy
ent bundle install
```

For more details check the readme of `application` and `content`.

## Configure Hub Registry in AppBuilder
This sections is useful to connect an existing Entando Hub.
From AppBuilder menu, you select `Hub`. At the top right, select `Select Registry` and `New Registry`.

You can choose Name and configure the url endpoint.

* example endpoint value for localhost:

```
http://localhost:8081/appbuilder/api
```

* example endpoint value for production: 

```
http://{{YOUR-HOSTNAME}}/entando-hub-application-{{BUNDLE-CODE}}/entando-hub-catalog-ms/appbuilder/api
```

You can use also Entando Hub from `entando.com`:
```
https://entando.com/entando-hub-api/appbuilder/api
```

## Use Entando Hub as a bundle from AppBuilder

You can deploy through ent using pre-built image from entando.

```
ent ecr deploy --repo=docker://registry.hub.docker.com/entando/entando-hub-application

ent ecr deploy --repo=docker://registry.hub.docker.com/entando/entando-hub-content
```

Install the bundle from AppBuilder GUI. <br><br>
Set up permissions to configure the service:

1. Login to your Keycloak instance as an admin.
2. Give at least one user the ability to manage the Hub by granting the `eh-admin` role. Assign the `eh-admin` role for the `pn-cee95efc-77ff566e-entandopsdh-entando-hub-catalog-ms-server` client.
3. Give the generated plugin client permission to manage users:
*  From the left sidebar, go to Clients and select client ID `pn-cee95efc-77ff566e-entandopsdh-entando-hub-catalog-ms-server`.
* Click the `Service Account` tab at the top of the page and select `realm-management` from the `Client Roles` field.
* Choose `realm-admin` from `Available Roles`. Click `Add selected`. It should appear as an `Assigned Role`.