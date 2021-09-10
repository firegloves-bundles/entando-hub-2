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

ent prj pbs-init

ent prj fe-push (--force)

ent prj generate-cr | ent kubectl apply -n entando -f -

or ent deploy / ent install 


BE
https://<entando-url>/entando-hub-api/swagger-ui.html

