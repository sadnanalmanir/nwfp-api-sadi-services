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

@Name("getDataQuality")
@Description("NWFP rest API: Get information about data qualities")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getDataQuality.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getDataQuality.owl#Output")
public class GetDataQuality extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(GetDataQuality.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("*** SADI Service ***");
        log.info("Invoking SADI service:  getDataQuality");
        Model outputModel = output.getModel();

        try {
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getDataQualities";
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
                    String descriptionVal = getNullAsEmptyString(element.get("Description"));
                    String severityOrderVal = getNullAsEmptyString(element.get("Severity_Order"));

                    Resource dataQualityRes = outputModel.createResource();

                    Resource IdResource = outputModel.createResource();
                    IdResource.addProperty(Vocab.type, Vocab.DataQualityId);
                    IdResource.addLiteral(Vocab.has_value, idVal);
                    dataQualityRes.addProperty(Vocab.dataQualityId, IdResource);

                    Resource DescriptionResource = outputModel.createResource();
                    DescriptionResource.addProperty(Vocab.type, Vocab.Description);
                    DescriptionResource.addLiteral(Vocab.has_value, descriptionVal);
                    dataQualityRes.addProperty(Vocab.description, DescriptionResource);

                    Resource SeverityOrderResource = outputModel.createResource();
                    SeverityOrderResource.addProperty(Vocab.type, Vocab.SeverityOrder);
                    SeverityOrderResource.addLiteral(Vocab.has_value, severityOrderVal);
                    dataQualityRes.addProperty(Vocab.severityOrder, SeverityOrderResource);

                    dataQualityRes.addProperty(Vocab.type, output);
                }
                log.info("getDataQuality service completed.");
            } else if (status > 299){
                log.info("Error executing the GET method at " + endPoint);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    public static final class Vocab {
        private static final Model m_model = ModelFactory.createDefaultModel();
        // Object properties
        public static final Property type = m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        public static final Property dataQualityId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#dataQualityId");
        public static final Property description = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#description");
        public static final Property severityOrder = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#severityOrder");
        // Data property
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        // Resources
        public static final Resource Description = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Description");
        public static final Resource SeverityOrder = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#SeverityOrder");
        public static final Resource DataQualityId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DataQualityId");

        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getDataQuality.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getDataQuality.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

