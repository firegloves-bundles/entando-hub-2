# Entando Hub Content Bundle

## Overview
This bundle requires `entando-hub-application-bundle`. <br>
This is the content bundle for the Entando Hub. It contains the "non-code" parts of the Entando Hub including a basic page setup.


## Deployment and Installation
This project is ent-enabled so you can use the Entando CLI (ent) to perform the full build and deployment sequence.

1. Build the bundle: `ent bundle build --all` <br>
2. Pack the bundle: `ent bundle pack`
3. Publish the bundle in your container registry: `ent bundle publish`
4. Deploy the bundle in Entando: `ent bundle deploy`
5. Install the bundle with GUI or with command: `ent bundle install`