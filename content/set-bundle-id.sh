#!/bin/sh

# this script is meant to populate/replace bundle ids
#
# Pre-requisites:
#    - ent cli installed https://developer.entando.com/next/docs/getting-started/entando-cli.html#command-list
#
# Overview:
# you must provide the bundle url from which derive the new hash to use, by using the -u option
# by default it searches only in the platform folder, but you can provide a custom location using the -f option
# by default it searches for ++BUNDLE_ID++ as value to override, but you can provide a custom value using the -t option
# minimum example usage ./set-bundle-id.sh -u docker://registry.hub.docker.com/alepintus/Dentando-hub-application"
# full example usage ./set-bundle-id.sh -u docker://registry.hub.docker.com/alepintus/Dentando-hub-application -f microfrontends -t 1111cccc

while [ "$#" -gt 0 ]; do
  case "$1" in
    "-f") FOLDER_OVERRIDE="$2";shift;;
    "-t") TARGET_OVERRIDE="$2";shift;;
    "-u") BUNDLE_URL="$2";shift;;
    "-"*) echo "Undefined argument \"$1\"" 1>&2;exit 3;;
  esac
  shift
done


if [ -z "$BUNDLE_URL" ]
then
  echo "Please specify a bundle url with -u flag"
  exit 1
fi

FOLDER="${FOLDER_OVERRIDE:-platform}"
TARGET="${TARGET_OVERRIDE:-++BUNDLE_ID++}"
NEW_VALUE=$(ent ecr get-bundle-id "${BUNDLE_URL}")

echo "#########################"
echo "Folder: ${FOLDER}"
echo "Url: ${BUNDLE_URL}"
echo "Value to override: ${TARGET}"
echo "New value: ${NEW_VALUE}"
echo "#########################"
echo "Replacing..."

grep -rl "${TARGET}" "${FOLDER}" | xargs sed -i "" -e "s/${TARGET}/${NEW_VALUE}/g"

echo "...DONE"
