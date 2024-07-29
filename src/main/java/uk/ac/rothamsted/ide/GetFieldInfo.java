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

@Name("getFieldInfo")
@Description("NWFP rest API: Get information about the fields based on the identifier")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getFieldInfo.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getFieldInfo.owl#Output")
public class GetFieldInfo extends SimpleSynchronousServiceServlet {

    private static final Logger log = Logger.getLogger(GetFieldInfo.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("*** SADI Service ***");
        log.info("Invoking SADI service:  getFieldInfo");

        // Extract the field id from the input RDF:
        int fieldId = input.getRequiredProperty(Vocab.has_fieldId).getResource().getRequiredProperty(Vocab.has_value).getInt();

        // create instance of the output model
        Model outputModel = output.getModel();

        try {
            // initiate GET request to the endpoint
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getFields";
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
                    if (idVal.getInt() == fieldId) {
                        String displayIdVal = getNullAsEmptyString(element.get("DisplayId"));
                        String nameVal = getNullAsEmptyString(element.get("Name"));
                        String validFromVal = getNullAsEmptyString(element.get("ValidFrom"));
                        String validUntilVal = getNullAsEmptyString(element.get("ValidUntil"));
                        String cuttingAreaVal = getNullAsEmptyString(element.get("CuttingArea"));
                        String fencedAreaVal = getNullAsEmptyString(element.get("FencedArea"));
                        String organicSpreadingAreaVal = getNullAsEmptyString(element.get("OrganicSpreadingArea"));
                        String inorganicSpreadingAreaVal = getNullAsEmptyString(element.get("InorganicSpreadingArea"));
                        String catchment_idVal = getNullAsEmptyString(element.get("Catchment_Id"));
                        String hydrologicalAreaVal = getNullAsEmptyString(element.get("HydrologicalArea"));

                        // populate the output model with instances and literal values
                        Resource DisplayIdResource = outputModel.createResource();
                        DisplayIdResource.addProperty(Vocab.type, Vocab.DisplayId);
                        DisplayIdResource.addLiteral(Vocab.has_value, displayIdVal);
                        output.addProperty(Vocab.has_displayId, DisplayIdResource);

                        Resource NameResource = outputModel.createResource();
                        NameResource.addProperty(Vocab.type, Vocab.Name);
                        NameResource.addLiteral(Vocab.has_value, nameVal);
                        output.addProperty(Vocab.has_name, NameResource);

                        Resource ValidFromResource = outputModel.createResource();
                        ValidFromResource.addProperty(Vocab.type, Vocab.ValidFromDate);
                        ValidFromResource.addLiteral(Vocab.has_value, validFromVal);
                        output.addProperty(Vocab.is_validFrom, ValidFromResource);

                        Resource ValidUntilResource = outputModel.createResource();
                        ValidUntilResource.addProperty(Vocab.type, Vocab.ValidUntilDate);
                        ValidUntilResource.addLiteral(Vocab.has_value, validUntilVal);
                        output.addProperty(Vocab.is_validUntil, ValidUntilResource);

                        Resource CuttingAreaResource = outputModel.createResource();
                        CuttingAreaResource.addProperty(Vocab.type, Vocab.CuttingArea);
                        CuttingAreaResource.addLiteral(Vocab.has_value, cuttingAreaVal);
                        output.addProperty(Vocab.has_cuttingArea, CuttingAreaResource);

                        Resource FencedAreaResource = outputModel.createResource();
                        FencedAreaResource.addProperty(Vocab.type, Vocab.FencedArea);
                        FencedAreaResource.addLiteral(Vocab.has_value, fencedAreaVal);
                        output.addProperty(Vocab.has_fencedArea, FencedAreaResource);

                        Resource OrganicSpreadingAreaResource = outputModel.createResource();
                        OrganicSpreadingAreaResource.addProperty(Vocab.type, Vocab.OrganicSpreadingArea);
                        OrganicSpreadingAreaResource.addLiteral(Vocab.has_value, organicSpreadingAreaVal);
                        output.addProperty(Vocab.has_organicSpreadingArea, OrganicSpreadingAreaResource);

                        Resource InorganicSpreadingAreaResource = outputModel.createResource();
                        InorganicSpreadingAreaResource.addProperty(Vocab.type, Vocab.InorganicSpreadingArea);
                        InorganicSpreadingAreaResource.addLiteral(Vocab.has_value, inorganicSpreadingAreaVal);
                        output.addProperty(Vocab.has_inorganicSpreadingArea, InorganicSpreadingAreaResource);

                        Resource CatchmentIdResource = outputModel.createResource();
                        CatchmentIdResource.addProperty(Vocab.type, Vocab.CatchmentId);
                        CatchmentIdResource.addLiteral(Vocab.has_value, catchment_idVal);
                        output.addProperty(Vocab.has_catchmentId, CatchmentIdResource);

                        Resource HydrologicalAreaResource = outputModel.createResource();
                        HydrologicalAreaResource.addProperty(Vocab.type, Vocab.HydrologicalArea);
                        HydrologicalAreaResource.addLiteral(Vocab.has_value, hydrologicalAreaVal);
                        output.addProperty(Vocab.has_hydrologicalArea, HydrologicalAreaResource);
                    }
                }
                log.info("getFieldInfo service completed.");
            } else if (status > 299){
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
        public static final Property has_fieldId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_fieldId");
        public static final Property has_displayId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_displayId");
        public static final Property has_name = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_name");
        public static final Property is_validFrom = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#is_validFrom");
        public static final Property is_validUntil = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#is_validUntil");
        public static final Property has_cuttingArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_cuttingArea");
        public static final Property has_fencedArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_fencedArea");
        public static final Property has_organicSpreadingArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_organicSpreadingArea");
        public static final Property has_inorganicSpreadingArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_inorganicSpreadingArea");
        public static final Property has_catchmentId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_catchmentId");
        public static final Property has_hydrologicalArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_hydrologicalArea");
        // Data property
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        // Resources
        public static final Resource Name = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Name");
        public static final Resource DisplayId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DisplayId");
        public static final Resource ValidFromDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ValidFromDate");
        public static final Resource ValidUntilDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ValidUntilDate");
        public static final Resource CuttingArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CuttingArea");
        public static final Resource FencedArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FencedArea");
        public static final Resource OrganicSpreadingArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#OrganicSpreadingArea");
        public static final Resource InorganicSpreadingArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#InorganicSpreadingArea");
        public static final Resource CatchmentId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentId");
        public static final Resource HydrologicalArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#HydrologicalArea");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getFieldInfo.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getFieldInfo.owl#Output");
    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

