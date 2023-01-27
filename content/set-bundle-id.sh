#!/bin/bash

# this script is meant to populate/replace bundle ids
# you must provide the new value using the -v option
# by default it searches only in the platform folder, but you can provide a custom location using the -f option
# by default it searches for e9233984 as value to override, but you can provide a custom value using the -t option

while getopts vft flag
do
  case "${flag}" in
      f) FOLDER_OVERRIDE=${OPTARG};;
      t) TARGET_OVERRIDE=${OPTARG};;
      v) NEW_VALUE=${OPTARG};;
      *) echo "usage: $0 [-f folder] [-t target to override] [-v new value]" >&2
         exit 1 ;;
  esac
done

if [[ -z "$NEW_VALUE" ]]
then
  echo "Please specify a new value with -v flag"
  exit 1
fi

FOLDER="${FOLDER_OVERRIDE:-platform}"
TARGET="${TARGET_OVERRIDE:-e9233984}"

echo "Folder: ${FOLDER}"
echo "Value to override: ${TARGET}"
echo "New value: ${NEW_VALUE}"


grep -rl "${TARGET}" "${FOLDER}" | xargs sed -i "" -e "s/${TARGET}/${NEW_VALUE}/g"
