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

## Replaces bundle ids

A `set-bundle-id.sh` utility script is available to populate/replace bundle ids.

You must provide the docker organization to use to get the new bundle ids to use, by using the `-o` option.
This script overrides the occurrences of 2 placeholders (__APPLICATION_BUNDLE_ID__ and __CONTENT_BUNDLE_ID__) with the corresponding bundle ids
By default:
* it searches only in the platform folder, but you can provide a custom location using the `-f` option
* it searches for __APPLICATION_BUNDLE_ID__ as value to override, but you can provide a custom value using the `-a` option
* it searches for __CONTENT_BUNDLE_ID__ as value to override, but you can provide a custom value using the `-c` option

a minimum example usage: 

```./set-bundle-id.sh -o mydockerorg```

a full example usage:

```./set-bundle-id.sh -o mydockerorg -f microfrontends -a MY_CUSTOM_PLACEHOLDER -c MY_CUSTOM_PLACEHOLDER_2```