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

@Name("getMeasurementType")
@Description("NWFP rest API: Get measurement types")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getMeasurementType.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getMeasurementType.owl#Output")
public class GetMeasurementType extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(GetMeasurementType.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("Invoking SADI service:  getMeasurementType");
        Model outputModel = output.getModel();

        try {
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getMeasurementTypes";
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
                log.info("URL Connection closed.");
                //log.info("URL Content... \n" + response.toString());

                JsonArray jsonArray = new Gson().fromJson(response.toString(), JsonArray.class);

                Iterator<JsonElement> elementIterator = jsonArray.iterator();
                JsonObject element;

                while (elementIterator.hasNext()) {

                    element = elementIterator.next().getAsJsonObject();

                    String idVal = getNullAsEmptyString(element.get("Id"));
                    String displayNameVal = getNullAsEmptyString(element.get("DisplayName"));
                    String nameVal = getNullAsEmptyString(element.get("Name"));
                    String unitVal = getNullAsEmptyString(element.get("Unit"));
                    String displayUnitVal = getNullAsEmptyString(element.get("DisplayUnit"));
                    String lLOVal = getNullAsEmptyString(element.get("LLO"));
                    String uLOVal = getNullAsEmptyString(element.get("ULO"));
                    String groupVal = getNullAsEmptyString(element.get("Group"));
                    String systemSetQualityVal = getNullAsEmptyString(element.get("SystemSetQuality"));

                    // TODO: create the resource only once, take it outside of the while loop
                    Resource Measurement = outputModel.createResource();
                    // enabling Catchment rdf:type for the root node as instance of {Catchment} does not work on hydra gui
                    //catchment.addProperty(Vocab.type, Vocab.Catchment);


                    Resource IdResource = outputModel.createResource();
                    IdResource.addProperty(Vocab.type, Vocab.Id);
                    IdResource.addLiteral(Vocab.has_value, idVal);
                    Measurement.addProperty(Vocab.id, IdResource);

                    Resource DisplayNameResource = outputModel.createResource();
                    DisplayNameResource.addProperty(Vocab.type, Vocab.DisplayName);
                    DisplayNameResource.addLiteral(Vocab.has_value, displayNameVal);
                    Measurement.addProperty(Vocab.displayName, DisplayNameResource);

                    Resource NameResource = outputModel.createResource();
                    NameResource.addProperty(Vocab.type, Vocab.Name);
                    NameResource.addLiteral(Vocab.has_value, nameVal);
                    Measurement.addProperty(Vocab.name, NameResource);

                    Resource UnitResource = outputModel.createResource();
                    UnitResource.addProperty(Vocab.type, Vocab.Unit);
                    UnitResource.addLiteral(Vocab.has_value, unitVal);
                    Measurement.addProperty(Vocab.unit, UnitResource);

                    Resource DisplayUnitResource = outputModel.createResource();
                    DisplayUnitResource.addProperty(Vocab.type, Vocab.DisplayUnit);
                    DisplayUnitResource.addLiteral(Vocab.has_value, displayUnitVal);
                    Measurement.addProperty(Vocab.displayUnit, DisplayUnitResource);

                    Resource LLOResource = outputModel.createResource();
                    LLOResource.addProperty(Vocab.type, Vocab.LLO);
                    LLOResource.addLiteral(Vocab.has_value, lLOVal);
                    Measurement.addProperty(Vocab.lLO, LLOResource);

                    Resource ULOResource = outputModel.createResource();
                    ULOResource.addProperty(Vocab.type, Vocab.ULO);
                    ULOResource.addLiteral(Vocab.has_value, uLOVal);
                    Measurement.addProperty(Vocab.uLO, ULOResource);

                    Resource GroupResource = outputModel.createResource();
                    GroupResource.addProperty(Vocab.type, Vocab.Group);
                    GroupResource.addLiteral(Vocab.has_value, groupVal);
                    Measurement.addProperty(Vocab.group, GroupResource);

                    Resource SystemSetQualityResource = outputModel.createResource();
                    SystemSetQualityResource.addProperty(Vocab.type, Vocab.SystemSetQuality);
                    SystemSetQualityResource.addLiteral(Vocab.has_value, systemSetQualityVal);
                    Measurement.addProperty(Vocab.systemSetQuality, SystemSetQualityResource);

                    Measurement.addProperty(Vocab.type, output);

                }
                log.info("Service successfully executed");
            } else {
                log.info("Connection to " + endPoint + " failed");
            }
        } catch (Exception e) {
            log.info(e);
        }


    }

    public static final class Vocab {
        private static final Model m_model = ModelFactory.createDefaultModel();
        public static final Property type = m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        public static final Property id = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#id");
        public static final Property displayName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#displayName");
        public static final Property name = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#name");
        public static final Property unit = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#unit");
        public static final Property displayUnit = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#displayUnit");
        public static final Property lLO = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#lLO");
        public static final Property uLO = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#uLO");
        public static final Property group = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#group");
        public static final Property systemSetQuality = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#systemSetQuality");

        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");

        public static final Resource Id = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Id");
        public static final Resource DisplayName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DisplayName");
        public static final Resource Name = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Name");
        public static final Resource Unit = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Unit");
        public static final Resource DisplayUnit = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DisplayUnit");
        public static final Resource LLO = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#LLO");
        public static final Resource ULO = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ULO");
        public static final Resource Group = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Group");
        public static final Resource SystemSetQuality = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#SystemSetQuality");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementType.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementType.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

