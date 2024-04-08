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

@Name("getMeasurementTypeLong")
@Description("NWFP rest API: Get measurement types")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getMeasurementTypeLong.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getMeasurementTypeLong.owl#Output")
public class GetMeasurementTypeLong extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(GetMeasurementTypeLong.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("*** SADI Service ***");
        log.info("Invoking SADI service:  getMeasurementTypeLong");
        Model outputModel = output.getModel();

        try {
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getMeasurementTypesLong";
            URL url = new URL(endPoint);
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
                //log.info("URL Content... \n" + response.toString());

                JsonObject jsonObject = new Gson().fromJson(response.toString(), JsonObject.class);
                JsonArray idsJsonArray= jsonObject.get("Ids").getAsJsonArray();
                JsonArray namesJsonArray= jsonObject.get("Names").getAsJsonArray();
                JsonArray displayNamesJsonArray= jsonObject.get("DisplayNames").getAsJsonArray();
                JsonArray unitsJsonArray= jsonObject.get("Units").getAsJsonArray();
                JsonArray displayUnitsJsonArray= jsonObject.get("DisplayUnits").getAsJsonArray();
                JsonArray systemSetQualityJsonArray= jsonObject.get("SystemSetQuality").getAsJsonArray();

                if (idsJsonArray.size() == namesJsonArray.size()
                        && idsJsonArray.size() == displayNamesJsonArray.size()
                        && idsJsonArray.size() == unitsJsonArray.size()
                        && idsJsonArray.size() == displayUnitsJsonArray.size()
                        && idsJsonArray.size() == systemSetQualityJsonArray.size()
                ) {
                    for (int i=0; i<idsJsonArray.size();i++) {
                        log.info("Ids: " + getNullAsEmptyString(idsJsonArray.get(i))
                                + " | Names: " + getNullAsEmptyString(namesJsonArray.get(i))
                                + " | DisplayNames: " + getNullAsEmptyString(displayNamesJsonArray.get(i))
                                + " | Units: " + getNullAsEmptyString(unitsJsonArray.get(i))
                                + " | DisplayUnits: " + getNullAsEmptyString(displayUnitsJsonArray.get(i))
                                + " | SystemSetQuality: " + getNullAsEmptyString(systemSetQualityJsonArray.get(i))
                        );

                        String idVal = getNullAsEmptyString(idsJsonArray.get(i));
                        String nameVal = getNullAsEmptyString(namesJsonArray.get(i));
                        String displayNameVal = getNullAsEmptyString(displayNamesJsonArray.get(i));
                        String unitVal = getNullAsEmptyString(unitsJsonArray.get(i));
                        String displayUnitVal = getNullAsEmptyString(displayUnitsJsonArray.get(i));
                        String systemSetQualityVal = getNullAsEmptyString(systemSetQualityJsonArray.get(i));


                        Resource Measurement = outputModel.createResource();
                        // enabling Catchment rdf:type for the root node as instance of {Catchment} does not work on hydra gui

                        Resource IdResource = outputModel.createResource();
                        IdResource.addProperty(Vocab.type, Vocab.Id);
                        IdResource.addLiteral(Vocab.has_value, idVal);
                        Measurement.addProperty(Vocab.id, IdResource);

                        Resource NameResource = outputModel.createResource();
                        NameResource.addProperty(Vocab.type, Vocab.Name);
                        NameResource.addLiteral(Vocab.has_value, nameVal);
                        Measurement.addProperty(Vocab.name, NameResource);

                        Resource DisplayNameResource = outputModel.createResource();
                        DisplayNameResource.addProperty(Vocab.type, Vocab.DisplayName);
                        DisplayNameResource.addLiteral(Vocab.has_value, displayNameVal);
                        Measurement.addProperty(Vocab.displayName, DisplayNameResource);

                        Resource UnitResource = outputModel.createResource();
                        UnitResource.addProperty(Vocab.type, Vocab.Unit);
                        UnitResource.addLiteral(Vocab.has_value, unitVal);
                        Measurement.addProperty(Vocab.unit, UnitResource);

                        Resource DisplayUnitResource = outputModel.createResource();
                        DisplayUnitResource.addProperty(Vocab.type, Vocab.DisplayUnit);
                        DisplayUnitResource.addLiteral(Vocab.has_value, displayUnitVal);
                        Measurement.addProperty(Vocab.displayUnit, DisplayUnitResource);

                        Resource SystemSetQualityResource = outputModel.createResource();
                        SystemSetQualityResource.addProperty(Vocab.type, Vocab.SystemSetQuality);
                        SystemSetQualityResource.addLiteral(Vocab.has_value, systemSetQualityVal);
                        Measurement.addProperty(Vocab.systemSetQuality, SystemSetQualityResource);

                        Measurement.addProperty(Vocab.type, output);
                    }
                    log.info("getMeasurementTypeLong service completed.");
                }
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
        public static final Property id = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#id");
        public static final Property displayName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#displayName");
        public static final Property name = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#name");
        public static final Property unit = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#unit");
        public static final Property displayUnit = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#displayUnit");
        public static final Property systemSetQuality = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#systemSetQuality");

        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");

        public static final Resource Id = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Id");
        public static final Resource Name = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Name");
        public static final Resource DisplayName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DisplayName");
        public static final Resource Unit = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Unit");
        public static final Resource DisplayUnit = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DisplayUnit");
        public static final Resource SystemSetQuality = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#SystemSetQuality");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementTypeLong.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementTypeLong.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

