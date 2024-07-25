# nwfp-api-sadi-services
SADI Services exposing RESTful API [endpoints](https://red-crescent-623716.postman.co/documentation/7453000-ae05790f-8bea-4c3c-8b9c-006ebab9e13e) of the [North Wyke Farm Platform](https://nwfp.rothamsted.ac.uk/) Data 

## SADI services
The following table shows the SADI services, their CRUD operations, the endpoints they expose

| SADI Service                  | Description                                                                                           | HTTP Method | API Endpoint                                                      |
|-------------------------------|-------------------------------------------------------------------------------------------------------|-------------|-------------------------------------------------------------------|
| allCatchments                 | List all catchments by their unique identifiers                                                       | GET         | https://nwfp.rothamsted.ac.uk:8443/getCatchments                  |
| getCatchment                  | Get information about the catchments based on the identifier                                          | GET         | https://nwfp.rothamsted.ac.uk:8443/getCatchments                  |
| allCatchmentMeasurementTypes  | List all measurement types of the catchments by their unique identifiers                              | GET         | https://nwfp.rothamsted.ac.uk:8443/getCatchmentMeasurementTypes   |
| getCatchmentMeasurementType   | Get information about the measurements of catchments based on the identifier                          | GET         | https://nwfp.rothamsted.ac.uk:8443/getCatchmentMeasurementTypes   |
| allFields                     | List all fields by their unique identifiers                                                           | GET         | https://nwfp.rothamsted.ac.uk:8443/getFields                      |
| getField                      | Get information about the fields based on the identifier                                              | GET         | https://nwfp.rothamsted.ac.uk:8443/getFields                      |
| allFieldEvents                | List all events in the fields by their identifiers                                                    | GET         | https://nwfp.rothamsted.ac.uk:8443/getFieldEvents                 |
| getFieldEvent                 | Get information about the events in the fields based on the identifier                                | GET         | https://nwfp.rothamsted.ac.uk:8443/getFieldEvents                 |
| allAnimalBasicData            | List all basic animal data by their unique identifiers                                                | GET         | https://nwfp.rothamsted.ac.uk:8443/getAnimalBasicData             |
| getAnimalBasicData            | Get information about the basic animal data based on the identifier                                   | GET         | https://nwfp.rothamsted.ac.uk:8443/getAnimalBasicData             |
| allDataQualities              | List all measures of the quality of data by their unique identifiers                                  | GET         | https://nwfp.rothamsted.ac.uk:8443/getDataQualities               |
| getDataQuality                | Get information about the measurements of quality of data based on the identifier                     | GET         | https://nwfp.rothamsted.ac.uk:8443/getDataQualities               |
| allMeasurementLocations       | List all locations of the measurements by their unique identifiers                                    | GET         | https://nwfp.rothamsted.ac.uk:8443/getMeasurementLocations        |
| getMeasurementLocation        | Get information about the locations of the measurements based on the identifier                       | GET         | https://nwfp.rothamsted.ac.uk:8443/getMeasurementLocations        |
| allMeasurementTypes           | List all types of the measurements by their unique identifiers                                        | GET         | https://nwfp.rothamsted.ac.uk:8443/getMeasurementTypes            |
| getMeasurementType            | Get information about the types of measurements based on the identifier                               | GET         | https://nwfp.rothamsted.ac.uk:8443/getMeasurementTypes            |
| allMeasurementTypesLong       | List all types of the measurements in the long-form by their non-unique identifiers                   | GET         | https://nwfp.rothamsted.ac.uk:8443/getMeasurementTypesLong        |
| getMeasurementTypeLong        | Get information about the measurements in the long-form based on the identifier                       | GET         | https://nwfp.rothamsted.ac.uk:8443/getMeasurementTypesLong        |
| getMeasurementByCatchmentName | Get information about the measurements based on the type, start date, end date, and name of catchment | POST        | https://nwfp.rothamsted.ac.uk:8443/getMeasurementsByCatchmentName |
| getMeasurementByDateRange     | Get information about the measurements based on the type, start and end date                          | POST        | https://nwfp.rothamsted.ac.uk:8443/getMeasurementsByDateRange     |
| getMeasurementByTypeId        | Get paginated information about the measurements based on type, page, and number of pages             | POST        | https://nwfp.rothamsted.ac.uk:8443/getMeasurementsByDateRange     |

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
  - http://localhost:8080/ontology/service-ontology/allCatchments.owl
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
$ curl http://localhost:8080/nwfp-api-sadi-services/allCatchments
```
```shell
$ curl -H 'Content-Type: text/rdf' -H 'Accept: text/rdf' --data @./src/test/inputdata/allCatchments/1.rdf http://localhost:8080/nwfp-api-sadi-services/allCatchments
```
```shell
$ curl -H 'Content-Type: text/rdf+n3' -H 'Accept: text/rdf+n3' --data @./src/test/inputdata/allCatchments/1.n3 http://localhost:8080/nwfp-api-sadi-services/allCatchments
```


### ServiceS failing to respond
#### getMeasurmentByTypeId
- The endpoint fails to return contents for the `queryResults` key
- Response from the API endpoint contains [{"totalPages":1883678},{"queryResults":[]}]
#### getFieldEvent
- Sometimes, the service fails to return results when RDF data is used sa input
- Error message is shown as `reactor.netty.http.client.PrematureCloseException: Connection prematurely closed DURING response`