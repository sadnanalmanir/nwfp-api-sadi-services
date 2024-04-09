package uk.ac.rothamsted.ide;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.sadiframework.service.annotations.*;
import org.sadiframework.service.simple.SimpleSynchronousServiceServlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.nio.charset.StandardCharsets;

@Name("getMeasurementLocation")
@Description("NWFP rest API: Get measurement locations")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getMeasurementLocation.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getMeasurementLocation.owl#Output")
public class GetMeasurementLocation extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(GetMeasurementLocation.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("*** SADI Service ***");
        log.info("Invoking SADI service:  getMeasurementLocation");
        Model outputModel = output.getModel();

        try {
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getMeasurementLocations";
            URL url = new URL(endPoint);
            long startTime = System.currentTimeMillis();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // set connection timeout to 2 seconds
            //conn.setConnectTimeout(5000);
            // set content reading timeout to 5 seconds
            //conn.setReadTimeout(20000);
            //conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            log.info("Request URL: " + url);

            int status = conn.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK) {
                log.info("'GET' Request is Successful. Http Status Code: " + status);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuilder response = new StringBuilder();
                log.info("Reading response...");
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                log.info("Done.");
                conn.disconnect();
                log.info("URL Connection closed.");
                long endTime = System.currentTimeMillis();
                log.info("Round trip response time = " + (endTime - startTime) + " ms");

                log.info("API Response Data: " + response);

                JsonArray jsonArray = new Gson().fromJson(response.toString(), JsonArray.class);


                Iterator<JsonElement> elementIterator = jsonArray.iterator();
                JsonObject element;

                while (elementIterator.hasNext()) {

                    element = elementIterator.next().getAsJsonObject();

                    String idVal = getNullAsEmptyString(element.get("Id"));
                    String measurementLocationNameVal = getNullAsEmptyString(element.get("measurementLocationName"));
                    String catchmentNameVal = getNullAsEmptyString(element.get("catchmentName"));
                    String catchmentDisplayNameVal = getNullAsEmptyString(element.get("catchmentDisplayName"));
                    String locationTypeNameVal = getNullAsEmptyString(element.get("locationTypeName"));
                    String locationXVal = getNullAsEmptyString(element.get("LocationX"));
                    String locationYVal = getNullAsEmptyString(element.get("LocationY"));
                    String farmletNameVal = getNullAsEmptyString(element.get("farmletName"));
                    String fieldNameVal = getNullAsEmptyString(element.get("fieldName"));
                    String catchmentIdVal = getNullAsEmptyString(element.get("Catchment_Id"));
                    String farmletIdVal = getNullAsEmptyString(element.get("Farmlet_Id"));
                    String fieldIdVal = getNullAsEmptyString(element.get("Field_Id"));
                    String locationTypeIdVal = getNullAsEmptyString(element.get("LocationType_Id"));
                    String heightVal = getNullAsEmptyString(element.get("Height"));
                    String validFromDateVal = getNullAsEmptyString(element.get("ValidFrom"));
                    String validUntilDateVal = getNullAsEmptyString(element.get("ValidUntil"));


                    Resource Measurement = outputModel.createResource();
                    // enabling Catchment rdf:type for the root node as instance of {Catchment} does not work on hydra gui
                    //catchment.addProperty(Vocab.type, Vocab.Catchment);


                    Resource IdResource = outputModel.createResource();
                    IdResource.addProperty(Vocab.type, Vocab.MeasurementLocationId);
                    IdResource.addLiteral(Vocab.has_value, idVal);
                    Measurement.addProperty(Vocab.measurementLocationId, IdResource);

                    Resource MeasurementLocationNameResource = outputModel.createResource();
                    MeasurementLocationNameResource.addProperty(Vocab.type, Vocab.MeasurementLocationName);
                    MeasurementLocationNameResource.addLiteral(Vocab.has_value, measurementLocationNameVal);
                    Measurement.addProperty(Vocab.measurementLocationName, MeasurementLocationNameResource);

                    Resource CatchmentNameResource = outputModel.createResource();
                    CatchmentNameResource.addProperty(Vocab.type, Vocab.CatchmentName);
                    CatchmentNameResource.addLiteral(Vocab.has_value, catchmentNameVal);
                    Measurement.addProperty(Vocab.catchmentName, CatchmentNameResource);

                    Resource CatchmentDisplayNameResource = outputModel.createResource();
                    CatchmentDisplayNameResource.addProperty(Vocab.type, Vocab.CatchmentDisplayName);
                    CatchmentDisplayNameResource.addLiteral(Vocab.has_value, catchmentDisplayNameVal);
                    Measurement.addProperty(Vocab.catchmentDisplayName, CatchmentDisplayNameResource);

                    Resource LocationTypeNameResource = outputModel.createResource();
                    LocationTypeNameResource.addProperty(Vocab.type, Vocab.LocationTypeName);
                    LocationTypeNameResource.addLiteral(Vocab.has_value, locationTypeNameVal);
                    Measurement.addProperty(Vocab.locationTypeName, LocationTypeNameResource);

                    Resource LocationXResource = outputModel.createResource();
                    LocationXResource.addProperty(Vocab.type, Vocab.LocationX);
                    LocationXResource.addLiteral(Vocab.has_value, locationXVal);
                    Measurement.addProperty(Vocab.locationX, LocationXResource);

                    Resource LocationYResource = outputModel.createResource();
                    LocationYResource.addProperty(Vocab.type, Vocab.LocationY);
                    LocationYResource.addLiteral(Vocab.has_value, locationYVal);
                    Measurement.addProperty(Vocab.locationY, LocationYResource);

                    Resource FarmletNameResource = outputModel.createResource();
                    FarmletNameResource.addProperty(Vocab.type, Vocab.FarmletName);
                    FarmletNameResource.addLiteral(Vocab.has_value, farmletNameVal);
                    Measurement.addProperty(Vocab.farmletName, FarmletNameResource);

                    Resource FieldNameResource = outputModel.createResource();
                    FieldNameResource.addProperty(Vocab.type, Vocab.FieldName);
                    FieldNameResource.addLiteral(Vocab.has_value, fieldNameVal);
                    Measurement.addProperty(Vocab.fieldName, FieldNameResource);

                    Resource CatchmentIdResource = outputModel.createResource();
                    CatchmentIdResource.addProperty(Vocab.type, Vocab.CatchmentId);
                    CatchmentIdResource.addLiteral(Vocab.has_value, catchmentIdVal);
                    Measurement.addProperty(Vocab.catchmentId, CatchmentIdResource);

                    Resource FarmletIdResource = outputModel.createResource();
                    FarmletIdResource.addProperty(Vocab.type, Vocab.FarmletId);
                    FarmletIdResource.addLiteral(Vocab.has_value, farmletIdVal);
                    Measurement.addProperty(Vocab.farmletId, FarmletIdResource);

                    Resource FieldIdResource = outputModel.createResource();
                    FieldIdResource.addProperty(Vocab.type, Vocab.FieldId);
                    FieldIdResource.addLiteral(Vocab.has_value, fieldIdVal);
                    Measurement.addProperty(Vocab.fieldId, FieldIdResource);

                    Resource LocationTypeIdResource = outputModel.createResource();
                    LocationTypeIdResource.addProperty(Vocab.type, Vocab.LocationTypeId);
                    LocationTypeIdResource.addLiteral(Vocab.has_value, locationTypeIdVal);
                    Measurement.addProperty(Vocab.locationTypeId, LocationTypeIdResource);

                    Resource HeightResource = outputModel.createResource();
                    HeightResource.addProperty(Vocab.type, Vocab.Height);
                    HeightResource.addLiteral(Vocab.has_value, heightVal);
                    Measurement.addProperty(Vocab.height, HeightResource);

                    Resource ValidFromDateResource = outputModel.createResource();
                    ValidFromDateResource.addProperty(Vocab.type, Vocab.ValidFromDate);
                    ValidFromDateResource.addLiteral(Vocab.has_value, validFromDateVal);
                    Measurement.addProperty(Vocab.validFromDate, ValidFromDateResource);

                    Resource ValidUntilDateResource = outputModel.createResource();
                    ValidUntilDateResource.addProperty(Vocab.type, Vocab.ValidUntilDate);
                    ValidUntilDateResource.addLiteral(Vocab.has_value, validUntilDateVal);
                    Measurement.addProperty(Vocab.validUntilDate, ValidUntilDateResource);

                    Measurement.addProperty(Vocab.type, output);

                }
                log.info("getMeasurementLocation service completed.");
            } else if (status > 299){
                log.info("Error executing the GET method at " + endPoint);
            }
        } catch (Exception e) {
            log.info(e);
        }


    }

    public static final class Vocab {
        private static final Model m_model = ModelFactory.createDefaultModel();
        public static final Property type = m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        public static final Property measurementLocationId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#measurementLocationId");
        public static final Property measurementLocationName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#measurementLocationName");
        public static final Property catchmentName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#catchmentName");
        public static final Property catchmentDisplayName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#catchmentDisplayName");
        public static final Property locationTypeName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#locationTypeName");
        public static final Property locationX = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#locationX");
        public static final Property locationY = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#locationY");
        public static final Property farmletName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#farmletName");
        public static final Property fieldName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#fieldName");
        public static final Property catchmentId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#catchmentId");
        public static final Property farmletId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#farmletId");
        public static final Property fieldId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#fieldId");
        public static final Property locationTypeId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#locationTypeId");
        public static final Property height = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#height");
        public static final Property validFromDate = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#validFromDate");
        public static final Property validUntilDate = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#validUntilDate");

        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");

        public static final Resource MeasurementLocationId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#MeasurementLocationId");
        public static final Resource MeasurementLocationName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#MeasurementLocationName");
        public static final Resource CatchmentName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentName");
        public static final Resource CatchmentDisplayName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentDisplayName");
        public static final Resource LocationTypeName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#LocationTypeName");
        public static final Resource LocationX = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#LocationX");
        public static final Resource LocationY = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#LocationY");
        public static final Resource FarmletName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FarmletName");
        public static final Resource FieldName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FieldName");
        public static final Resource CatchmentId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentId");
        public static final Resource FarmletId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FarmletId");
        public static final Resource FieldId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FieldId");
        public static final Resource LocationTypeId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#LocationTypeId");
        public static final Resource Height = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Height");
        public static final Resource ValidFromDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ValidFromDate");
        public static final Resource ValidUntilDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ValidUntilDate");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementLocation.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementLocation.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

