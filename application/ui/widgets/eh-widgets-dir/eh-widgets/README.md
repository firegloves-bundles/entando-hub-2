# Entando Hub

## Getting started

To run the container:

1. `docker run -d -p 8081:8081 --name entando-hub-catalog germanogiudici/entando-hub-catalog-ms:0.0.1-SNAPSHOT`
2. `docker stop entando-hub-catalog`
3. `docker start entando-hub-catalog`

APIs url: http://127.0.0.1:8081/swagger-ui.html

H2 console url: http://localhost:8081/h2-console/

To access the H2 console use these credentials:

- JDBC URL: `jdbc:h2:./data/testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE`
- User Name: `sa`
- Password: `password`