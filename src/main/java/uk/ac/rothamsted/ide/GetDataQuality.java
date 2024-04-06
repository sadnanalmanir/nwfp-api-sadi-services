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

@Name("getDataQuality")
@Description("Get all catchments and their properties")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getDataQuality.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getDataQuality.owl#Output")
public class GetDataQuality extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(GetDataQuality.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("Invoking SADI service:  getDataQuality");
        Model outputModel = output.getModel();

        try {
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getDataQualities";
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

                    String idVal = getNullAsEmptyString(element.get("Id"));
                    String descriptionVal = getNullAsEmptyString(element.get("Description"));
                    String severityOrderVal = getNullAsEmptyString(element.get("Severity_Order"));

                    Resource dataQuality = outputModel.createResource();

                    Resource IdResource = outputModel.createResource();
                    IdResource.addProperty(Vocab.type, Vocab.Id);
                    IdResource.addLiteral(Vocab.has_value, idVal);
                    dataQuality.addProperty(Vocab.id, IdResource);

                    Resource DescriptionResource = outputModel.createResource();
                    DescriptionResource.addProperty(Vocab.type, Vocab.Description);
                    DescriptionResource.addLiteral(Vocab.has_value, descriptionVal);
                    dataQuality.addProperty(Vocab.description, DescriptionResource);

                    Resource SeverityOrderResource = outputModel.createResource();
                    SeverityOrderResource.addProperty(Vocab.type, Vocab.SeverityOrder);
                    SeverityOrderResource.addLiteral(Vocab.has_value, severityOrderVal);
                    dataQuality.addProperty(Vocab.severityOrder, SeverityOrderResource);

                    dataQuality.addProperty(Vocab.type, output);

                    log.info("Service successfully executed");
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    public static final class Vocab {
        private static final Model m_model = ModelFactory.createDefaultModel();
        // Object properties
        public static final Property type = m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        public static final Property id = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#id");
        public static final Property description = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#description");
        public static final Property severityOrder = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#severityOrder");
        // Data property
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        // Resources
        public static final Resource Id = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Id");
        public static final Resource Description = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Description");
        public static final Resource SeverityOrder = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#SeverityOrder");
        public static final Resource DataQuality = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DataQuality");

        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getDataQuality.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getDataQuality.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

