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

@Name("getCatchmentMeasurementType")
@Description("Get measurement types from the catchments")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getCatchmentMeasurementType.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getCatchmentMeasurementType.owl#Output")
public class GetCatchmentMeasurementType extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(GetCatchmentMeasurementType.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("Invoking SADI service:  getCatchmentMeasurementTypes");
        Model outputModel = output.getModel();

        try {
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getCatchmentMeasurementTypes";
            URL url = new URL(endPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // set connection timeout to 2 seconds
            conn.setConnectTimeout(5000);
            // set content reading timeout to 5 seconds
            conn.setReadTimeout(5000);
            //conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            log.info("Request URL: " + url);

            int status = conn.getResponseCode();
            log.info("Response Code: " + status);

            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuffer response = new StringBuffer();
                log.info("Reading response...");
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                log.info("Done.");
                conn.disconnect();
                log.info("Connection closed.");
                //log.info("URL Content... \n" + response.toString());

                JsonArray jsonArray = new Gson().fromJson(response.toString(), JsonArray.class);

                Iterator<JsonElement> elementIterator = jsonArray.iterator();
                JsonObject element;

                while (elementIterator.hasNext()) {

                    element = elementIterator.next().getAsJsonObject();

                    String catchmentIdVal = getNullAsEmptyString(element.get("catchment_ID"));
                    String catchmentNameVal = getNullAsEmptyString(element.get("catchment_name"));
                    String measurementTypeDisplayNameVal = getNullAsEmptyString(element.get("MeasTypeDisplayName"));
                    String locationIdVal = getNullAsEmptyString(element.get("location_ID"));
                    String locationNameVal = getNullAsEmptyString(element.get("location_Name"));
                    String locationTypeIdVal = getNullAsEmptyString(element.get("location_type_id"));
                    String locationTypeNameVal = getNullAsEmptyString(element.get("location_type_name"));
                    String typeIdVal = getNullAsEmptyString(element.get("type_id"));
                    String concentrationNameAndUnitVal = getNullAsEmptyString(element.get("concName_and_unit"));


                    Resource Catchment = outputModel.createResource();
                    // enabling Catchment rdf:type for the root node as instance of {Catchment} does not work on hydra gui
                    //catchment.addProperty(Vocab.type, Vocab.Catchment);


                    Resource CatchmentIdResource = outputModel.createResource();
                    CatchmentIdResource.addProperty(Vocab.type, Vocab.CatchmentId);
                    CatchmentIdResource.addLiteral(Vocab.has_value, catchmentIdVal);
                    Catchment.addProperty(Vocab.catchmentId, CatchmentIdResource);

                    Resource CatchmentNameResource = outputModel.createResource();
                    CatchmentNameResource.addProperty(Vocab.type, Vocab.CatchmentName);
                    CatchmentNameResource.addLiteral(Vocab.has_value, catchmentNameVal);
                    Catchment.addProperty(Vocab.catchmentName, CatchmentNameResource);

                    Resource MeasurementTypeDisplayNameResource = outputModel.createResource();
                    MeasurementTypeDisplayNameResource.addProperty(Vocab.type, Vocab.MeasurementTypeDisplayName);
                    MeasurementTypeDisplayNameResource.addLiteral(Vocab.has_value, measurementTypeDisplayNameVal);
                    Catchment.addProperty(Vocab.measurementTypeDisplayName, MeasurementTypeDisplayNameResource);

                    Resource LocationIdResource = outputModel.createResource();
                    LocationIdResource.addProperty(Vocab.type, Vocab.LocationId);
                    LocationIdResource.addLiteral(Vocab.has_value, locationIdVal);
                    Catchment.addProperty(Vocab.locationId, LocationIdResource);

                    Resource LocationNameResource = outputModel.createResource();
                    LocationNameResource.addProperty(Vocab.type, Vocab.LocationName);
                    LocationNameResource.addLiteral(Vocab.has_value, locationNameVal);
                    Catchment.addProperty(Vocab.locationName, LocationNameResource);

                    Resource LocationTypeIdResource = outputModel.createResource();
                    LocationTypeIdResource.addProperty(Vocab.type, Vocab.LocationTypeId);
                    LocationTypeIdResource.addLiteral(Vocab.has_value, locationTypeIdVal);
                    Catchment.addProperty(Vocab.locationTypeId, LocationTypeIdResource);

                    Resource LocationTypeNameResource = outputModel.createResource();
                    LocationTypeNameResource.addProperty(Vocab.type, Vocab.LocationTypeName);
                    LocationTypeNameResource.addLiteral(Vocab.has_value, locationTypeNameVal);
                    Catchment.addProperty(Vocab.locationTypeName, LocationTypeNameResource);

                    Resource TypeIdResource = outputModel.createResource();
                    TypeIdResource.addProperty(Vocab.type, Vocab.TypeId);
                    TypeIdResource.addLiteral(Vocab.has_value, typeIdVal);
                    Catchment.addProperty(Vocab.typeId, TypeIdResource);

                    Resource ConcentrationNameAndUnitResource = outputModel.createResource();
                    ConcentrationNameAndUnitResource.addProperty(Vocab.type, Vocab.ConcentrationNameAndUnit);
                    ConcentrationNameAndUnitResource.addLiteral(Vocab.has_value, concentrationNameAndUnitVal);
                    Catchment.addProperty(Vocab.concentrationNameAndUnit, ConcentrationNameAndUnitResource);

                    Catchment.addProperty(Vocab.type, output);

                    log.info("Service successfully executed");
                }
            }
        } catch (Exception e) {
            log.error(e);
        }


    }

    public static final class Vocab {
        private static final Model m_model = ModelFactory.createDefaultModel();
        public static final Property type = m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        public static final Resource CatchmentId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentId");
        public static final Resource CatchmentName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentName");
        public static final Resource MeasurementTypeDisplayName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#MeasurementTypeDisplayName");
        public static final Resource LocationId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#LocationId");
        public static final Resource LocationName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#LocationName");
        public static final Resource LocationTypeId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#LocationTypeId");
        public static final Resource LocationTypeName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#LocationTypeName");
        public static final Resource TypeId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#TypeId");
        public static final Resource ConcentrationNameAndUnit = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ConcentrationNameAndUnit");


        public static final Property catchmentId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#catchmentId");
        public static final Property catchmentName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#catchmentName");
        public static final Property measurementTypeDisplayName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#measurementTypeDisplayName");
        public static final Property locationId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#locationId");
        public static final Property locationName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#locationName");
        public static final Property locationTypeId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#locationTypeId");
        public static final Property locationTypeName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#locationTypeName");
        public static final Property typeId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#typeId");
        public static final Property concentrationNameAndUnit = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#concentrationNameAndUnit");

        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getCatchmentMeasurementType.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getCatchmentMeasurementType.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

