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
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Name("allCatchmentMeasurementTypes")
@Description("NWFP rest API: List all measurement types of the catchments by their unique identifiers")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/allCatchmentMeasurementTypes.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/allCatchmentMeasurementTypes.owl#Output")
public class AllCatchmentMeasurementTypes extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(AllCatchmentMeasurementTypes.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("*** SADI Service ***");
        log.info("Invoking SADI service: allCatchmentMeasurementTypes");
        Model outputModel = output.getModel();

        try {
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getCatchmentMeasurementTypes";
            URL url = new URL(endPoint);
            long startTime = System.currentTimeMillis();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

                    //String catchmentIdVal = getNullAsEmptyString(element.get("catchment_ID"));
                    //String catchmentNameVal = getNullAsEmptyString(element.get("catchment_name"));
                    //String measurementTypeDisplayNameVal = getNullAsEmptyString(element.get("MeasTypeDisplayName"));
                    //String locationIdVal = getNullAsEmptyString(element.get("location_ID"));
                    //String locationNameVal = getNullAsEmptyString(element.get("location_Name"));
                    //String locationTypeIdVal = getNullAsEmptyString(element.get("location_type_id"));
                    //String locationTypeNameVal = getNullAsEmptyString(element.get("location_type_name"));
                    String typeIdVal = getNullAsEmptyString(element.get("type_id"));
                    //String concentrationNameAndUnitVal = getNullAsEmptyString(element.get("concName_and_unit"));


                    // Resource catchmentResource = outputModel.createResource();
                    // enabling Catchment rdf:type for the root node as instance of {Catchment} does not work on hydra gui
                    //catchment.addProperty(Vocab.type, Vocab.Catchment);


//                    Resource CatchmentIdResource = outputModel.createResource();
//                    CatchmentIdResource.addProperty(GetCatchmentMeasurementType.Vocab.type, GetCatchmentMeasurementType.Vocab.CatchmentId);
//                    CatchmentIdResource.addLiteral(GetCatchmentMeasurementType.Vocab.has_value, catchmentIdVal);
//                    catchmentResource.addProperty(GetCatchmentMeasurementType.Vocab.catchmentId, CatchmentIdResource);
//
//                    Resource CatchmentNameResource = outputModel.createResource();
//                    CatchmentNameResource.addProperty(GetCatchmentMeasurementType.Vocab.type, GetCatchmentMeasurementType.Vocab.CatchmentName);
//                    CatchmentNameResource.addLiteral(GetCatchmentMeasurementType.Vocab.has_value, catchmentNameVal);
//                    catchmentResource.addProperty(GetCatchmentMeasurementType.Vocab.catchmentName, CatchmentNameResource);
//
//                    Resource MeasurementTypeDisplayNameResource = outputModel.createResource();
//                    MeasurementTypeDisplayNameResource.addProperty(GetCatchmentMeasurementType.Vocab.type, GetCatchmentMeasurementType.Vocab.MeasurementTypeDisplayName);
//                    MeasurementTypeDisplayNameResource.addLiteral(GetCatchmentMeasurementType.Vocab.has_value, measurementTypeDisplayNameVal);
//                    catchmentResource.addProperty(GetCatchmentMeasurementType.Vocab.measurementTypeDisplayName, MeasurementTypeDisplayNameResource);
//
//                    Resource LocationIdResource = outputModel.createResource();
//                    LocationIdResource.addProperty(GetCatchmentMeasurementType.Vocab.type, GetCatchmentMeasurementType.Vocab.LocationId);
//                    LocationIdResource.addLiteral(GetCatchmentMeasurementType.Vocab.has_value, locationIdVal);
//                    catchmentResource.addProperty(GetCatchmentMeasurementType.Vocab.locationId, LocationIdResource);
//
//                    Resource LocationNameResource = outputModel.createResource();
//                    LocationNameResource.addProperty(GetCatchmentMeasurementType.Vocab.type, GetCatchmentMeasurementType.Vocab.LocationName);
//                    LocationNameResource.addLiteral(GetCatchmentMeasurementType.Vocab.has_value, locationNameVal);
//                    catchmentResource.addProperty(GetCatchmentMeasurementType.Vocab.locationName, LocationNameResource);
//
//                    Resource LocationTypeIdResource = outputModel.createResource();
//                    LocationTypeIdResource.addProperty(GetCatchmentMeasurementType.Vocab.type, GetCatchmentMeasurementType.Vocab.LocationTypeId);
//                    LocationTypeIdResource.addLiteral(GetCatchmentMeasurementType.Vocab.has_value, locationTypeIdVal);
//                    catchmentResource.addProperty(GetCatchmentMeasurementType.Vocab.locationTypeId, LocationTypeIdResource);
//
//                    Resource LocationTypeNameResource = outputModel.createResource();
//                    LocationTypeNameResource.addProperty(GetCatchmentMeasurementType.Vocab.type, GetCatchmentMeasurementType.Vocab.LocationTypeName);
//                    LocationTypeNameResource.addLiteral(GetCatchmentMeasurementType.Vocab.has_value, locationTypeNameVal);
//                    catchmentResource.addProperty(GetCatchmentMeasurementType.Vocab.locationTypeName, LocationTypeNameResource);

                    //Resource TypeIdResource = outputModel.createResource();
                    //TypeIdResource.addProperty(GetCatchmentMeasurementType.Vocab.type, GetCatchmentMeasurementType.Vocab.MeasurementTypeId);
                    //TypeIdResource.addLiteral(GetCatchmentMeasurementType.Vocab.has_value, typeIdVal);
                    //catchmentResource.addProperty(GetCatchmentMeasurementType.Vocab.measurementTypeId, TypeIdResource);
                    Resource measurement = outputModel.createResource();
                    measurement.addProperty(Vocab.has_CatchmentMeasurementTypeId, typeIdVal);
                    measurement.addProperty(Vocab.type,output);

//                    Resource ConcentrationNameAndUnitResource = outputModel.createResource();
//                    ConcentrationNameAndUnitResource.addProperty(GetCatchmentMeasurementType.Vocab.type, GetCatchmentMeasurementType.Vocab.ConcentrationNameAndUnit);
//                    ConcentrationNameAndUnitResource.addLiteral(GetCatchmentMeasurementType.Vocab.has_value, concentrationNameAndUnitVal);
//                    catchmentResource.addProperty(GetCatchmentMeasurementType.Vocab.concentrationNameAndUnit, ConcentrationNameAndUnitResource);

                    //catchmentResource.addProperty(GetCatchmentMeasurementType.Vocab.type, output);
                }
                log.info("allCatchmentMeasurementTypes service completed.");
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
        public static final Property has_CatchmentMeasurementTypeId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_CatchmentMeasurementTypeId");
        public static final Resource Measurement = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Measurement");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/allCatchmentMeasurementTypes.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/allCatchmentMeasurementTypes.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

