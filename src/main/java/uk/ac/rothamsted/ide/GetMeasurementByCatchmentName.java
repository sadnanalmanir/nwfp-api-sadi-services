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
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

@Name("getMeasurementByCatchmentName")
@Description("NWFP rest API: Get measurement types used in the catchments")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getMeasurementByCatchmentName.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getMeasurementByCatchmentName.owl#Output")
public class GetMeasurementByCatchmentName extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(GetMeasurementByCatchmentName.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("Invoking SADI service:  getMeasurementByCatchmentName");
        Model outputModel = output.getModel();
        String startDateValue = input.getPropertyResourceValue(Vocab.startDate).getRequiredProperty(Vocab.has_value).getString();
        String endDateValue = input.getPropertyResourceValue(Vocab.endDate).getRequiredProperty(Vocab.has_value).getString();
        String catchmentNameValue = input.getPropertyResourceValue(Vocab.catchmentName).getRequiredProperty(Vocab.has_value).getString();
        String typeIdValue = input.getPropertyResourceValue(Vocab.typeId).getRequiredProperty(Vocab.has_value).getString();
        String body = "{\n" +
                "    \"startDate\": \"" +startDateValue+ "\",\n" +
                "    \"endDate\": \""+endDateValue+"\",\n" +
                "    \"catchmentName\": \""+catchmentNameValue+"\",\n" +
                "    \"typeId\": "+typeIdValue+"\n" +
                "}";
        log.info("Retrieved from HYDRA inputs: " +body);


        try {

            URL url = new URL("https://nwfp.rothamsted.ac.uk:8443/getMeasurementsByCatchmentName");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            //connection.setConnectTimeout(3000);
            //connection.setReadTimeout(10000);

            try(DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream())) {
                dataOutputStream.writeBytes(body);
            }

            log.info("Response code: " + connection.getResponseCode());

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonArray jsonArray = new Gson().fromJson(reader, JsonArray.class);
            reader.close();

            Iterator<JsonElement> elementIterator = jsonArray.iterator();
            JsonObject element;

            while (elementIterator.hasNext()) {

                element = elementIterator.next().getAsJsonObject();

                String dateTimeVal = getNullAsEmptyString(element.get("DateTime"));
                float valueVal = element.get("Value").getAsFloat();
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

                log.info("Service successfully executed");
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
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementByCatchmentName.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getMeasurementByCatchmentName.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

