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

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

@Name("getMeasurementByDateRange")
@Description("NWFP rest API: Get measurement information based on the date ranges with single id support")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getMeasurementByDateRange.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getMeasurementByDateRange.owl#Output")
public class GetMeasurementByDateRange extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(GetMeasurementByDateRange.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("Invoking SADI service:  getMeasurementByDateRange");
        Model outputModel = output.getModel();

        String startDateValue = input.getPropertyResourceValue(GetMeasurementByDateRange.Vocab.startDate).getRequiredProperty(GetMeasurementByDateRange.Vocab.has_value).getString();
        if (startDateValue == null | startDateValue == ""){
            log.info("Failed to extract start date from: "
                    + input.getLocalName() + " -> " + GetMeasurementByDateRange.Vocab.startDate.getLocalName() + " -> " + GetMeasurementByDateRange.Vocab.has_value.getLocalName());
            throw new IllegalArgumentException("Failed to extract start date from: "
                    + input.getLocalName() + " -> " + GetMeasurementByDateRange.Vocab.startDate.getLocalName() + " -> " + GetMeasurementByDateRange.Vocab.has_value.getLocalName());
        }
        String endDateValue = input.getPropertyResourceValue(GetMeasurementByDateRange.Vocab.endDate).getRequiredProperty(GetMeasurementByDateRange.Vocab.has_value).getString();
        if (endDateValue == null){
            log.info("Failed to extract end date from: "
                    + input.getLocalName() + " -> " + GetMeasurementByDateRange.Vocab.endDate.getLocalName() + " -> " + GetMeasurementByDateRange.Vocab.has_value.getLocalName());
            throw new IllegalArgumentException("Failed to extract end date from: "
                    + input.getLocalName() + " -> " + GetMeasurementByDateRange.Vocab.endDate.getLocalName() + " -> " + GetMeasurementByDateRange.Vocab.has_value.getLocalName());
        }
        String typeIdValue = input.getPropertyResourceValue(GetMeasurementByDateRange.Vocab.typeId).getRequiredProperty(GetMeasurementByDateRange.Vocab.has_value).getString();
        if (typeIdValue == null){
            log.info("Failed to extract start type id from: "
                    + input.getLocalName() + " -> " + GetMeasurementByDateRange.Vocab.typeId.getLocalName() + " -> " + GetMeasurementByDateRange.Vocab.has_value.getLocalName());
            throw new IllegalArgumentException("Failed to extract start type id from: "
                    + input.getLocalName() + " -> " + GetMeasurementByDateRange.Vocab.typeId.getLocalName() + " -> " + GetMeasurementByDateRange.Vocab.has_value.getLocalName());
        }
        String body = "{\n" +
                "    \"startDate\": \"" +startDateValue+ "\",\n" +
                "    \"endDate\": \""+endDateValue+"\",\n" +
                "    \"typeId\": "+typeIdValue+"\n" +
                "}";
        log.info("Data to send via POST method: " +body);



        try {
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getMeasurementsByDateRange";
            URL url = new URL(endPoint);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setDoOutput(true);
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(5000);

            try(OutputStream os = conn.getOutputStream()) {
                byte[] inputToSend = body.getBytes("utf-8");
                os.write(inputToSend, 0, inputToSend.length);
            }

            int status = conn.getResponseCode();
            log.info("Response Code: " + status);

            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String responseLine;
                StringBuffer response = new StringBuffer();
                log.info("Reading response...");
                while ((responseLine = in.readLine()) != null) {
                    response.append(responseLine);
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

                    String dateTimeVal = getNullAsEmptyString(element.get("DateTime"));
                    String valueVal = getNullAsEmptyString(element.get("Value"));
                    String measurementTypeDisplayNameVal = getNullAsEmptyString(element.get("MeasTypeDisplayName"));
                    String locationNameVal = getNullAsEmptyString(element.get("LocationName"));
                    String catchmentDisplayNameVal = getNullAsEmptyString(element.get("CatchDisplayName"));
                    String dataQualityVal = getNullAsEmptyString(element.get("dataQuality"));

                    Resource DateTimeResource = outputModel.createResource();
                    DateTimeResource.addProperty(Vocab.type, Vocab.DateTime);
                    DateTimeResource.addLiteral(Vocab.has_value, dateTimeVal);
                    output.addProperty(Vocab.dateTime, DateTimeResource);

                    Resource ValueResource = outputModel.createResource();
                    ValueResource.addProperty(Vocab.type, Vocab.Value);
                    ValueResource.addLiteral(Vocab.has_value, valueVal);
                    output.addProperty(Vocab.value, ValueResource);

                    Resource MeasurementTypeDisplayNameResource = outputModel.createResource();
                    MeasurementTypeDisplayNameResource.addProperty(Vocab.type, Vocab.MeasurementTypeDisplayName);
                    MeasurementTypeDisplayNameResource.addLiteral(Vocab.has_value, measurementTypeDisplayNameVal);
                    output.addProperty(Vocab.measurementTypeDisplayName, MeasurementTypeDisplayNameResource);

                    Resource LocationNameResource = outputModel.createResource();
                    LocationNameResource.addProperty(Vocab.type, Vocab.LocationName);
                    LocationNameResource.addLiteral(Vocab.has_value, locationNameVal);
                    output.addProperty(Vocab.locationName, LocationNameResource);

                    Resource CatchmentDisplayNameResource = outputModel.createResource();
                    CatchmentDisplayNameResource.addProperty(Vocab.type, Vocab.CatchmentDisplayName);
                    CatchmentDisplayNameResource.addLiteral(Vocab.has_value, catchmentDisplayNameVal);
                    output.addProperty(Vocab.catchmentDisplayName, CatchmentDisplayNameResource);

                    Resource DataQualityResource = outputModel.createResource();
                    DataQualityResource.addProperty(Vocab.type, Vocab.DataQuality);
                    DataQualityResource.addLiteral(Vocab.has_value, dataQualityVal);
                    output.addProperty(Vocab.dataQuality, DataQualityResource);
                }


                log.info("Service successfully executed");
            }else if (status > 299){
                log.info("Error executing the POST method at " + endPoint);
            }
        } catch (Exception e) {
            log.info(e);
        }


    }

    public static final class Vocab {
        private static final Model m_model = ModelFactory.createDefaultModel();
        // object properties
        public static final Property type = m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        public static final Property startDate = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#startDate");
        public static final Property endDate = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#endDate");
        public static final Property typeId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#typeId");
        public static final Property catchmentName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#catchmentName");
        public static final Property dateTime = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#dateTime");
        public static final Property value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#value");
        public static final Property measurementTypeDisplayName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#measurementTypeDisplayName");
        public static final Property locationName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#locationName");
        public static final Property catchmentDisplayName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#catchmentDisplayName");
        public static final Property dataQuality = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#dataQuality");
        // data property
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        // resources
        public static final Resource StartDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#StartDate");
        public static final Resource EndDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#EndDate");
        public static final Resource TypeId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#TypeId");
        public static final Resource CatchmentName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentName");
        public static final Resource DateTime = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DateTime");
        public static final Resource Value = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Value");
        public static final Resource MeasurementTypeDisplayName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#MeasurementTypeDisplayName");
        public static final Resource LocationName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#LocationName");
        public static final Resource CatchmentDisplayName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentDisplayName");
        public static final Resource DataQuality = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DataQuality");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementByDateRange.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementByDateRange.owl#Output");
    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

