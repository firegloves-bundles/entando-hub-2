#!/bin/sh

# this script is meant to populate/replace bundle ids
#
# Pre-requisites:
#    - ent cli installed https://developer.entando.com/next/docs/getting-started/entando-cli.html#command-list
#
# Overview:
# you must provide the docker org to use to derive the new bundle ids to use, by using the -o option.
# this script overrides the occurrences of 2 placeholders (__APPLICATION_BUNDLE_ID__ and __CONTENT_BUNDLE_ID__) with the corresponding bundle ids
# by default it searches only in the platform folder, but you can provide a custom location using the -f option
# by default it searches for __APPLICATION_BUNDLE_ID__ as value to override, but you can provide a custom value using the -a option
# by default it searches for __CONTENT_BUNDLE_ID__ as value to override, but you can provide a custom value using the -c option
# minimum example usage ./set-bundle-id.sh -o mydockerorg
# full example usage ./set-bundle-id.sh -o mydockerorg -f microfrontends -a MY_CUSTOM_PLACEHOLDER -c MY_CUSTOM_PLACEHOLDER_2

set -x

while [ "$#" -gt 0 ]; do
  case "$1" in
    "-f") FOLDER_OVERRIDE="$2";shift;;
    "-a") APPLICATION_TARGET_OVERRIDE="$2";shift;;
    "-c") CONTENT_TARGET_OVERRIDE="$2";shift;;
    "-o") DOCKER_ORG="$2";shift;;
    "-"*) echo "Undefined argument \"$1\"" 1>&2;exit 3;;
  esac
  shift
done


if [ -z "$DOCKER_ORG" ]
then
  echo "Please specify a docker org with -o flag"
  exit 1
fi

FOLDER="${FOLDER_OVERRIDE:-platform}"
APPLICATION_TARGET="${APPLICATION_TARGET_OVERRIDE:-__APPLICATION_BUNDLE_ID__}"
CONTENT_TARGET="${CONTENT_TARGET_OVERRIDE:-__CONTENT_BUNDLE_ID__}"
APPLICATION_BUNDLE_ID=$(ent ecr get-bundle-id "docker://registry.hub.docker.com/${DOCKER_ORG}/entando-hub-application")
CONTENT_BUNDLE_ID=$(ent ecr get-bundle-id "docker://registry.hub.docker.com/${DOCKER_ORG}/entando-hub-content")

echo "#########################"
echo "Folder: ${FOLDER}"
echo "Application placeholder to override: ${APPLICATION_TARGET}"
echo "Application Bundle ID: ${APPLICATION_BUNDLE_ID}"
echo "Content placeholder to override: ${CONTENT_TARGET}"
echo "Content Bundle ID: ${CONTENT_BUNDLE_ID}"
echo "#########################"
echo "Replacing..."

grep -rl "${APPLICATION_TARGET}" "${FOLDER}" | xargs sed -ie "s/${APPLICATION_TARGET}/${APPLICATION_BUNDLE_ID}/g"
grep -rl "${CONTENT_TARGET}" "${FOLDER}" | xargs sed -ie "s/${CONTENT_TARGET}/${CONTENT_BUNDLE_ID}/g"

echo "...DONE"
