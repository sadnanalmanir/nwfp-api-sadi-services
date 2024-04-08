# nwfp-api-sadi-services
SADI Services exposing RESTful API [endpoints](https://red-crescent-623716.postman.co/documentation/7453000-ae05790f-8bea-4c3c-8b9c-006ebab9e13e) of the [North Wyke Farm Platform](https://nwfp.rothamsted.ac.uk/) Data 

## SADI services
The following table shows the SADI services, their CRUD operations, the endpoints they expose

| SADI Service                  | HTTP Method | Endpoint                                                          |
|-------------------------------|-------------|-------------------------------------------------------------------|
| getCatchment                  | GET         | https://nwfp.rothamsted.ac.uk:8443/getCatchments                  |
| getCatchmentMeasurementType   | GET         | https://nwfp.rothamsted.ac.uk:8443/getCatchmentMeasurementTypes   |
| getField                      | GET         | https://nwfp.rothamsted.ac.uk:8443/getFields                      |
| getFieldEvent                 | GET         | https://nwfp.rothamsted.ac.uk:8443/getFieldEvents                 |
| getAnimalBasicData            | GET         | https://nwfp.rothamsted.ac.uk:8443/getAnimalBasicData             |
| getDataQuality                | GET         | https://nwfp.rothamsted.ac.uk:8443/getDataQualities               |
| getMeasurementLocation        | GET         | https://nwfp.rothamsted.ac.uk:8443/getMeasurementLocations        |
| getMeasurementType            | GET         | https://nwfp.rothamsted.ac.uk:8443/getMeasurementTypes            |
| getMeasurementTypeLong        | GET         | https://nwfp.rothamsted.ac.uk:8443/getMeasurementTypesLong        |
| getMeasurementByCatchmentName | POST        | https://nwfp.rothamsted.ac.uk:8443/getMeasurementsByCatchmentName |
| getMeasurementByDateRange     | POST        | https://nwfp.rothamsted.ac.uk:8443/getMeasurementsByDateRange     |
| getMeasurementByTypeId        | POST        | https://nwfp.rothamsted.ac.uk:8443/getMeasurementsByDateRange     |


## How to deploy
You need to have [docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/) 
installed in your system. The following command uses `docker-compose.yaml` and `Dockerfile` to compile and package the source, 
deploy the ontologies, and the services on Tomcat server at http://localhost:8080.

### Clone the repository
```shell
$ git clone https://github.com/sadnanalmanir/nwfp-api-sadi-services.git 
```

### Move into the repository
```shell
$ cd nwfp-api-sadi-services
```
### Use docker compose command

```shell
$ docker compose up --build
```

After the deployment, open the browser to view:

- SADI services at http://localhost:8080/nwfp-api-sadi-services
- Domain ontology at http://localhost:8080/ontology/domain-ontology/nwf.owl
- Service ontologies at
  - http://localhost:8080/ontology/service-ontology/getCatchment.owl
  - http://localhost:8080/ontology/service-ontology/getCatchmentMeasurementType.owl
  - http://localhost:8080/ontology/service-ontology/getField.owl
  - http://localhost:8080/ontology/service-ontology/getFieldEvent.owl
  - http://localhost:8080/ontology/service-ontology/getAnimalBasicData.owl
  - http://localhost:8080/ontology/service-ontology/getDataQuality.owl
  - http://localhost:8080/ontology/service-ontology/getMeasurementLocation.owl
  - http://localhost:8080/ontology/service-ontology/getMeasurementType.owl
  - http://localhost:8080/ontology/service-ontology/getMeasurementTypeLong.owl
  - http://localhost:8080/ontology/service-ontology/getMeasurementByCatchmentName.owl
  - http://localhost:8080/ontology/service-ontology/getMeasurementByDateRange.owl
  - http://localhost:8080/ontology/service-ontology/getMeasurementByTypeId.owl

## Test the service
The `curl` command can be used to test each service
- Retrieve the service description
```shell
$ curl SERVICE_URL
```
- Test the service with input serialized in RDF
```shell
$ curl -H 'Content-Type: text/rdf' -H 'Accept: text/rdf' --data @PATH_TO_RDF_DATA SERVICE_URL
```
- Test the service with input serialized in n3
```shell
$ curl -H 'Content-Type: text/rdf+n3' -H 'Accept: text/rdf+n3' --data @PATH_TO_N3_DATA SERVICE_URL
```

For instance, use the following commands to test the getCatchment service
```shell
$ curl http://localhost:8080/nwfp-api-sadi-services/getCatchment
```
```shell
$ curl -H 'Content-Type: text/rdf' -H 'Accept: text/rdf' --data @./src/test/inputdata/getCatchment/1.rdf http://localhost:8080/nwfp-api-sadi-services/getCatchment
```
```shell
$ curl -H 'Content-Type: text/rdf+n3' -H 'Accept: text/rdf+n3' --data @./src/test/inputdata/getCatchment/1.n3 http://localhost:8080/nwfp-api-sadi-services/getCatchment
```