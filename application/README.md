# Entando Hub Application Bundle

## Overview


## Deployment and Installation
This project is ent-enabled so you can use the Entando CLI (ent) to perform the full build and deployment sequence.

1. Build the bundle: `ent bundle build --all` <br>
   You can build only the microservice: `ent bundle build --all-ms` <br>
   or only the microfrontends: `ent bundle build --all-mfe`
2. Pack the bundle: `ent bundle pack`
3. Publish the bundle in your container registry: `ent bundle publish`
4. Deploy the bundle in Entando: `ent bundle deploy`
5. Install the bundle with GUI or with command: `ent bundle install`


## Local Development

1. Start all the services (postgresql, keycloak): `ent bundle svc start --all`
2. Run the microservice: `ent bundle run entando-hub-catalog-ms`
3. Configure the parameters of microfrontends. You can use `.env.template` file like an example and put it in `.env` file
4. Run the microfrontends: `ent bundle run --all-mfe`

## Swagger UI
The swagger portal ui is provided by the microservice and is available by default.

You can reach it at addresses:

* for localhost

```
http://localhost:8081/swagger-ui.html
```

* for Entando cluster
```
http://{{YOUR-HOSTNAME}}/entando-hub-application-{{BUNDLE-CODE}}/entando-hub-catalog-ms/swagger-ui/index.html
```