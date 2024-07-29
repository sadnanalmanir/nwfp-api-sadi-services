package uk.ac.rothamsted.ide;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.rdf.model.*;
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

@Name("getMeasurementLocationInfo")
@Description("NWFP rest API: Get information about the locations of the measurements based on the identifier")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getMeasurementLocationInfo.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getMeasurementLocationInfo.owl#Output")
public class GetMeasurementLocationInfo extends SimpleSynchronousServiceServlet {

    private static final Logger log = Logger.getLogger(GetMeasurementLocationInfo.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("*** SADI Service ***");
        log.info("Invoking SADI service:  getMeasurementLocationInfo");

        // Extract the measurementLocationId from the input RDF:
        int measurementLocationId = input.getRequiredProperty(Vocab.has_measurementLocationId).getResource().getRequiredProperty(Vocab.has_value).getInt();

        // create instance of the output model
        Model outputModel = output.getModel();

        try {
            // initiate GET request to the endpoint
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getMeasurementLocations";
            URL url = new URL(endPoint);
            long startTime = System.currentTimeMillis();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // set connection timeout to 5 seconds
            conn.setConnectTimeout(5000);
            // set content reading timeout to 20 seconds
            conn.setReadTimeout(20000);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            log.info("Request URL: " + url);

            // gather response from the request
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

                // Deserialize response data
                JsonArray jsonArray = new Gson().fromJson(response.toString(), JsonArray.class);
                Iterator<JsonElement> elementIterator = jsonArray.iterator();
                JsonObject element;

                // Read each unique identifier value
                while (elementIterator.hasNext()) {
                    element = elementIterator.next().getAsJsonObject();
                    // Read current unique identifier value
                    Literal idVal = outputModel.createTypedLiteral(element.get("Id").getAsInt());
                    // check if the current id matches the extracted id
                    if (idVal.getInt() == measurementLocationId) {
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

                        // populate the output model with instances and literal values
                        Resource MeasurementLocationNameResource = outputModel.createResource();
                        MeasurementLocationNameResource.addProperty(Vocab.type, Vocab.MeasurementLocationName);
                        MeasurementLocationNameResource.addLiteral(Vocab.has_value, measurementLocationNameVal);
                        output.addProperty(Vocab.has_measurementLocationName, MeasurementLocationNameResource);

                        Resource CatchmentNameResource = outputModel.createResource();
                        CatchmentNameResource.addProperty(Vocab.type, Vocab.CatchmentName);
                        CatchmentNameResource.addLiteral(Vocab.has_value, catchmentNameVal);
                        output.addProperty(Vocab.has_catchmentName, CatchmentNameResource);

                        Resource CatchmentDisplayNameResource = outputModel.createResource();
                        CatchmentDisplayNameResource.addProperty(Vocab.type, Vocab.CatchmentDisplayName);
                        CatchmentDisplayNameResource.addLiteral(Vocab.has_value, catchmentDisplayNameVal);
                        output.addProperty(Vocab.has_catchmentDisplayName, CatchmentDisplayNameResource);

                        Resource LocationTypeNameResource = outputModel.createResource();
                        LocationTypeNameResource.addProperty(Vocab.type, Vocab.LocationTypeName);
                        LocationTypeNameResource.addLiteral(Vocab.has_value, locationTypeNameVal);
                        output.addProperty(Vocab.has_locationTypeName, LocationTypeNameResource);

                        Resource LocationXResource = outputModel.createResource();
                        LocationXResource.addProperty(Vocab.type, Vocab.LocationX);
                        LocationXResource.addLiteral(Vocab.has_value, locationXVal);
                        output.addProperty(Vocab.has_locationX, LocationXResource);

                        Resource LocationYResource = outputModel.createResource();
                        LocationYResource.addProperty(Vocab.type, Vocab.LocationY);
                        LocationYResource.addLiteral(Vocab.has_value, locationYVal);
                        output.addProperty(Vocab.has_locationY, LocationYResource);

                        Resource FarmletNameResource = outputModel.createResource();
                        FarmletNameResource.addProperty(Vocab.type, Vocab.FarmletName);
                        FarmletNameResource.addLiteral(Vocab.has_value, farmletNameVal);
                        output.addProperty(Vocab.has_farmletName, FarmletNameResource);

                        Resource FieldNameResource = outputModel.createResource();
                        FieldNameResource.addProperty(Vocab.type, Vocab.FieldName);
                        FieldNameResource.addLiteral(Vocab.has_value, fieldNameVal);
                        output.addProperty(Vocab.has_fieldName, FieldNameResource);

                        Resource CatchmentIdResource = outputModel.createResource();
                        CatchmentIdResource.addProperty(Vocab.type, Vocab.CatchmentId);
                        CatchmentIdResource.addLiteral(Vocab.has_value, catchmentIdVal);
                        output.addProperty(Vocab.has_catchmentId, CatchmentIdResource);

                        Resource FarmletIdResource = outputModel.createResource();
                        FarmletIdResource.addProperty(Vocab.type, Vocab.FarmletId);
                        FarmletIdResource.addLiteral(Vocab.has_value, farmletIdVal);
                        output.addProperty(Vocab.has_farmletId, FarmletIdResource);

                        Resource FieldIdResource = outputModel.createResource();
                        FieldIdResource.addProperty(Vocab.type, Vocab.FieldId);
                        FieldIdResource.addLiteral(Vocab.has_value, fieldIdVal);
                        output.addProperty(Vocab.has_fieldId, FieldIdResource);

                        Resource LocationTypeIdResource = outputModel.createResource();
                        LocationTypeIdResource.addProperty(Vocab.type, Vocab.LocationTypeId);
                        LocationTypeIdResource.addLiteral(Vocab.has_value, locationTypeIdVal);
                        output.addProperty(Vocab.has_locationTypeId, LocationTypeIdResource);

                        Resource HeightResource = outputModel.createResource();
                        HeightResource.addProperty(Vocab.type, Vocab.Height);
                        HeightResource.addLiteral(Vocab.has_value, heightVal);
                        output.addProperty(Vocab.has_height, HeightResource);

                        Resource ValidFromDateResource = outputModel.createResource();
                        ValidFromDateResource.addProperty(Vocab.type, Vocab.ValidFromDate);
                        ValidFromDateResource.addLiteral(Vocab.has_value, validFromDateVal);
                        output.addProperty(Vocab.is_validFrom, ValidFromDateResource);

                        Resource ValidUntilDateResource = outputModel.createResource();
                        ValidUntilDateResource.addProperty(Vocab.type, Vocab.ValidUntilDate);
                        ValidUntilDateResource.addLiteral(Vocab.has_value, validUntilDateVal);
                        output.addProperty(Vocab.is_validUntil, ValidUntilDateResource);
                    }

                }
                log.info("getMeasurementLocationInfo service completed.");
            } else if (status > 299) {
                log.info("Error executing the GET method at " + endPoint);
            }
        } catch (Exception e) {
            log.info(e);
        }
    }

    public static final class Vocab {
        private static final Model m_model = ModelFactory.createDefaultModel();
        // Object properties
        public static final Property type = m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        public static final Property has_measurementLocationId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_measurementLocationId");
        public static final Property has_measurementLocationName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_measurementLocationName");
        public static final Property has_catchmentName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_catchmentName");
        public static final Property has_catchmentDisplayName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_catchmentDisplayName");
        public static final Property has_locationTypeName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_locationTypeName");
        public static final Property has_locationX = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_locationX");
        public static final Property has_locationY = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_locationY");
        public static final Property has_farmletName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_farmletName");
        public static final Property has_fieldName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_fieldName");
        public static final Property has_catchmentId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_catchmentId");
        public static final Property has_farmletId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_farmletId");
        public static final Property has_fieldId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_fieldId");
        public static final Property has_locationTypeId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_locationTypeId");
        public static final Property has_height = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_height");
        public static final Property is_validFrom = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#is_validFrom");
        public static final Property is_validUntil = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#is_validUntil");
        // Data property
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        // Resources
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
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementLocationInfo.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementLocationInfo.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

