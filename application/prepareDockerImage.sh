#!/bin/sh

echo ""
echo "Building project and Docker image "
#todo export as global variable
#todo manage version
export MY_IMAGE="germanogiudici/entando-hub-catalog-ms:0.0.2-SNAPSHOT"

docker build -t ${MY_IMAGE}  .
echo "Built $MY_IMAGE"

echo ""
echo "Uploading $MY_IMAGE to dockerhub"
docker push $MY_IMAGE
