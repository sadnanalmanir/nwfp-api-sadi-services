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

@Name("getFieldEvent")
@Description("NWFP rest API: Get information about the events in the fields based on the identifier")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getFieldEvent.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getFieldEvent.owl#Output")
public class GetFieldEvent extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(GetFieldEvent.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("*** SADI Service ***");
        log.info("Invoking SADI service:  getFieldEvent");

        // Extract the catchment id from the input RDF:
        int fieldEventId = input.getRequiredProperty(Vocab.has_fieldEventId).getResource().getRequiredProperty(Vocab.has_value).getInt();

        // create instance of the output model
        Model outputModel = output.getModel();

        try {
            // initiate GET request to the endpoint
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getFieldEvents";
            URL url = new URL(endPoint);
            long startTime = System.currentTimeMillis();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // set connection timeout to 5 seconds
            conn.setConnectTimeout(5000);
            // set content reading timeout to 20 seconds
            //conn.setReadTimeout(20000);
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

                while (elementIterator.hasNext()) {
                    element = elementIterator.next().getAsJsonObject();
                    // Read current unique identifier value
                    Literal idVal = outputModel.createTypedLiteral(element.get("Id").getAsInt());
                    // check if the current id matches the extracted id
                    if (idVal.getInt() == fieldEventId) {
                        String datasetVersionIdVal = getNullAsEmptyString(element.get("DatasetVersionId"));
                        // catchment display name
                        String catchmentDisplayNameVal = getNullAsEmptyString(element.get("catch_name"));
                        String eventDateVal = getNullAsEmptyString(element.get("EventDate"));
                        String timeInVal = getNullAsEmptyString(element.get("TimeIn"));
                        String timeOutVal = getNullAsEmptyString(element.get("TimeOut"));
                        String timeInFieldVal = getNullAsEmptyString(element.get("TimeInField"));
                        String applicationTypeNameVal = getNullAsEmptyString(element.get("ApplicationTypeName"));
                        String fieldNameVal = getNullAsEmptyString(element.get("Field_Name"));
                        String tempFieldNameVal = getNullAsEmptyString(element.get("TempField_Name"));
                        String totalApplicationVal = getNullAsEmptyString(element.get("TotalApplication"));
                        String applicationInfoVal = getNullAsEmptyString(element.get("ApplicationInfo"));
                        String applicationRateVal = getNullAsEmptyString(element.get("ApplicationRate"));
                        String fieldIdVal = getNullAsEmptyString(element.get("FieldId"));
                        String tractorIdVal = getNullAsEmptyString(element.get("TractorId"));
                        String startTractorHoursVal = getNullAsEmptyString(element.get("StartTractorHours"));
                        String endTractorHoursVal = getNullAsEmptyString(element.get("EndTractorHours"));
                        String totalTractorHoursVal = getNullAsEmptyString(element.get("TotalTractorHours"));
                        String fieldOperationIdVal = getNullAsEmptyString(element.get("FieldOperationId"));
                        String fieldApplicationIdVal = getNullAsEmptyString(element.get("FieldApplicationId"));
                        String applicationBatchNumberVal = getNullAsEmptyString(element.get("ApplicationBatchNumber"));
                        String productNameVal = getNullAsEmptyString(element.get("ProductName"));
                        String manufacturerVal = getNullAsEmptyString(element.get("Manufacturer"));
                        String temporaryFieldIdVal = getNullAsEmptyString(element.get("TemporaryFieldId"));
                        String catchmentNameVal = getNullAsEmptyString(element.get("Catchment_Name"));
                        String operationNameVal = getNullAsEmptyString(element.get("Operation_name"));
                        String operationGroupVal = getNullAsEmptyString(element.get("OperationGroup"));
                        String applicationNameVal = getNullAsEmptyString(element.get("ApplicationName"));
                        String applicationInfoAppsVal = getNullAsEmptyString(element.get("ApplicationInfoApps"));
                        String formatNameVal = getNullAsEmptyString(element.get("FormatName"));
                        String rangeEndDateTimeVal = getNullAsEmptyString(element.get("RangeEndDateTime"));
                        String rangeStartDateTimeVal = getNullAsEmptyString(element.get("RangeStartDateTime"));
                        // What is the difference between this id and the previous TemporaryFieldId?
                        String tempFieldIdVal = getNullAsEmptyString(element.get("TempFieldId"));
                        String farmletNewVal = getNullAsEmptyString(element.get("Farmlet_new"));
                        String farmletOldVal = getNullAsEmptyString(element.get("Farmlet_old"));

                        // populate the output model with instances and literal values
                        Resource DatasetVersionIdResource = outputModel.createResource();
                        DatasetVersionIdResource.addProperty(Vocab.type, Vocab.DatasetVersionId);
                        DatasetVersionIdResource.addLiteral(Vocab.has_value, datasetVersionIdVal);
                        output.addProperty(Vocab.has_datasetVersionId, DatasetVersionIdResource);

                        Resource CatchmentNameResource = outputModel.createResource();
                        CatchmentNameResource.addProperty(Vocab.type, Vocab.CatchmentDisplayName);
                        CatchmentNameResource.addLiteral(Vocab.has_value, catchmentDisplayNameVal);
                        output.addProperty(Vocab.has_catchmentDisplayName, CatchmentNameResource);

                        Resource EventDateResource = outputModel.createResource();
                        EventDateResource.addProperty(Vocab.type, Vocab.EventDate);
                        EventDateResource.addLiteral(Vocab.has_value, eventDateVal);
                        output.addProperty(Vocab.has_eventDate, EventDateResource);

                        Resource TimeInResource = outputModel.createResource();
                        TimeInResource.addProperty(Vocab.type, Vocab.TimeIn);
                        TimeInResource.addLiteral(Vocab.has_value, timeInVal);
                        output.addProperty(Vocab.has_timeIn, TimeInResource);

                        Resource TimeOutResource = outputModel.createResource();
                        TimeOutResource.addProperty(Vocab.type, Vocab.TimeOut);
                        TimeOutResource.addLiteral(Vocab.has_value, timeOutVal);
                        output.addProperty(Vocab.has_timeOut, TimeOutResource);

                        Resource TimeInFieldResource = outputModel.createResource();
                        TimeInFieldResource.addProperty(Vocab.type, Vocab.TimeInField);
                        TimeInFieldResource.addLiteral(Vocab.has_value, timeInFieldVal);
                        output.addProperty(Vocab.has_timeInField, TimeInFieldResource);

                        Resource ApplicationTypeNameResource = outputModel.createResource();
                        ApplicationTypeNameResource.addProperty(Vocab.type, Vocab.ApplicationTypeName);
                        ApplicationTypeNameResource.addLiteral(Vocab.has_value, applicationTypeNameVal);
                        output.addProperty(Vocab.has_applicationTypeName, ApplicationTypeNameResource);

                        Resource FieldNameResource = outputModel.createResource();
                        FieldNameResource.addProperty(Vocab.type, Vocab.FieldName);
                        FieldNameResource.addLiteral(Vocab.has_value, fieldNameVal);
                        output.addProperty(Vocab.has_fieldName, FieldNameResource);

                        Resource TempField_NameResource = outputModel.createResource();
                        TempField_NameResource.addProperty(Vocab.type, Vocab.TempField_Name);
                        TempField_NameResource.addLiteral(Vocab.has_value, tempFieldNameVal);
                        output.addProperty(Vocab.has_tempFieldName, TempField_NameResource);

                        Resource TotalApplicationResource = outputModel.createResource();
                        TotalApplicationResource.addProperty(Vocab.type, Vocab.TotalApplication);
                        TotalApplicationResource.addLiteral(Vocab.has_value, totalApplicationVal);
                        output.addProperty(Vocab.has_totalApplication, TotalApplicationResource);

                        Resource ApplicationInfoResource = outputModel.createResource();
                        ApplicationInfoResource.addProperty(Vocab.type, Vocab.ApplicationInfo);
                        ApplicationInfoResource.addLiteral(Vocab.has_value, applicationInfoVal);
                        output.addProperty(Vocab.has_applicationInfo, ApplicationInfoResource);

                        Resource ApplicationRateResource = outputModel.createResource();
                        ApplicationRateResource.addProperty(Vocab.type, Vocab.ApplicationRate);
                        ApplicationRateResource.addLiteral(Vocab.has_value, applicationRateVal);
                        output.addProperty(Vocab.has_applicationRate, ApplicationRateResource);

                        Resource FieldIdResource = outputModel.createResource();
                        FieldIdResource.addProperty(Vocab.type, Vocab.FieldId);
                        FieldIdResource.addLiteral(Vocab.has_value, fieldIdVal);
                        output.addProperty(Vocab.has_fieldId, FieldIdResource);

                        Resource TractorIdResource = outputModel.createResource();
                        TractorIdResource.addProperty(Vocab.type, Vocab.TractorId);
                        TractorIdResource.addLiteral(Vocab.has_value, tractorIdVal);
                        output.addProperty(Vocab.has_tractorId, TractorIdResource);

                        Resource StartTractorHoursResource = outputModel.createResource();
                        StartTractorHoursResource.addProperty(Vocab.type, Vocab.StartTractorHours);
                        StartTractorHoursResource.addLiteral(Vocab.has_value, startTractorHoursVal);
                        output.addProperty(Vocab.has_startTractorHours, StartTractorHoursResource);

                        Resource EndTractorHoursResource = outputModel.createResource();
                        EndTractorHoursResource.addProperty(Vocab.type, Vocab.EndTractorHours);
                        EndTractorHoursResource.addLiteral(Vocab.has_value, endTractorHoursVal);
                        output.addProperty(Vocab.has_endTractorHours, EndTractorHoursResource);

                        Resource TotalTractorHoursResource = outputModel.createResource();
                        TotalTractorHoursResource.addProperty(Vocab.type, Vocab.TotalTractorHours);
                        TotalTractorHoursResource.addLiteral(Vocab.has_value, totalTractorHoursVal);
                        output.addProperty(Vocab.has_totalTractorHours, TotalTractorHoursResource);

                        Resource FieldOperationIdResource = outputModel.createResource();
                        FieldOperationIdResource.addProperty(Vocab.type, Vocab.FieldOperationId);
                        FieldOperationIdResource.addLiteral(Vocab.has_value, fieldOperationIdVal);
                        output.addProperty(Vocab.has_fieldOperationId, FieldOperationIdResource);

                        Resource FieldApplicationIdResource = outputModel.createResource();
                        FieldApplicationIdResource.addProperty(Vocab.type, Vocab.FieldApplicationId);
                        FieldApplicationIdResource.addLiteral(Vocab.has_value, fieldApplicationIdVal);
                        output.addProperty(Vocab.has_fieldApplicationId, FieldApplicationIdResource);

                        Resource ApplicationBatchNumberResource = outputModel.createResource();
                        ApplicationBatchNumberResource.addProperty(Vocab.type, Vocab.ApplicationBatchNumber);
                        ApplicationBatchNumberResource.addLiteral(Vocab.has_value, applicationBatchNumberVal);
                        output.addProperty(Vocab.has_applicationBatchNumber, ApplicationBatchNumberResource);

                        Resource ProductNameResource = outputModel.createResource();
                        ProductNameResource.addProperty(Vocab.type, Vocab.ProductName);
                        ProductNameResource.addLiteral(Vocab.has_value, productNameVal);
                        output.addProperty(Vocab.has_productName, ProductNameResource);

                        Resource ManufacturerResource = outputModel.createResource();
                        ManufacturerResource.addProperty(Vocab.type, Vocab.Manufacturer);
                        ManufacturerResource.addLiteral(Vocab.has_value, manufacturerVal);
                        output.addProperty(Vocab.has_manufacturer, ManufacturerResource);

                        Resource TemporaryFieldIdResource = outputModel.createResource();
                        TemporaryFieldIdResource.addProperty(Vocab.type, Vocab.TemporaryFieldId);
                        TemporaryFieldIdResource.addLiteral(Vocab.has_value, temporaryFieldIdVal);
                        output.addProperty(Vocab.has_temporaryFieldId, TemporaryFieldIdResource);

                        Resource CatchmentIdResource = outputModel.createResource();
                        CatchmentIdResource.addProperty(Vocab.type, Vocab.CatchmentName);
                        CatchmentIdResource.addLiteral(Vocab.has_value, catchmentNameVal);
                        output.addProperty(Vocab.has_catchmentName, CatchmentIdResource);

                        Resource Operation_nameResource = outputModel.createResource();
                        Operation_nameResource.addProperty(Vocab.type, Vocab.OperationName);
                        Operation_nameResource.addLiteral(Vocab.has_value, operationNameVal);
                        output.addProperty(Vocab.has_operation_name, Operation_nameResource);

                        Resource OperationGroupResource = outputModel.createResource();
                        OperationGroupResource.addProperty(Vocab.type, Vocab.OperationGroup);
                        OperationGroupResource.addLiteral(Vocab.has_value, operationGroupVal);
                        output.addProperty(Vocab.has_operationGroup, OperationGroupResource);

                        Resource ApplicationNameResource = outputModel.createResource();
                        ApplicationNameResource.addProperty(Vocab.type, Vocab.ApplicationName);
                        ApplicationNameResource.addLiteral(Vocab.has_value, applicationNameVal);
                        output.addProperty(Vocab.has_applicationName, ApplicationNameResource);

                        Resource ApplicationInfoAppsResource = outputModel.createResource();
                        ApplicationInfoAppsResource.addProperty(Vocab.type, Vocab.ApplicationInfoApps);
                        ApplicationInfoAppsResource.addLiteral(Vocab.has_value, applicationInfoAppsVal);
                        output.addProperty(Vocab.has_applicationInfoApps, ApplicationInfoAppsResource);

                        Resource FormatNameResource = outputModel.createResource();
                        FormatNameResource.addProperty(Vocab.type, Vocab.FormatName);
                        FormatNameResource.addLiteral(Vocab.has_value, formatNameVal);
                        output.addProperty(Vocab.has_formatName, FormatNameResource);

                        Resource RangeEndDateTimeResource = outputModel.createResource();
                        RangeEndDateTimeResource.addProperty(Vocab.type, Vocab.RangeEndDateTime);
                        RangeEndDateTimeResource.addLiteral(Vocab.has_value, rangeEndDateTimeVal);
                        output.addProperty(Vocab.has_rangeEndDateTime, RangeEndDateTimeResource);

                        Resource RangeStartDateTimeResource = outputModel.createResource();
                        RangeStartDateTimeResource.addProperty(Vocab.type, Vocab.RangeStartDateTime);
                        RangeStartDateTimeResource.addLiteral(Vocab.has_value, rangeStartDateTimeVal);
                        output.addProperty(Vocab.has_rangeStartDateTime, RangeStartDateTimeResource);

                        Resource TempFieldIdResource = outputModel.createResource();
                        TempFieldIdResource.addProperty(Vocab.type, Vocab.TempFieldId);
                        TempFieldIdResource.addLiteral(Vocab.has_value, tempFieldIdVal);
                        output.addProperty(Vocab.has_tempFieldId, TempFieldIdResource);

                        Resource FarmletNewResource = outputModel.createResource();
                        FarmletNewResource.addProperty(Vocab.type, Vocab.NewFarmlet);
                        FarmletNewResource.addLiteral(Vocab.has_value, farmletNewVal);
                        output.addProperty(Vocab.has_newFarmlet, FarmletNewResource);

                        Resource FarmletOldResource = outputModel.createResource();
                        FarmletOldResource.addProperty(Vocab.type, Vocab.OldFarmlet);
                        FarmletOldResource.addLiteral(Vocab.has_value, farmletOldVal);
                        output.addProperty(Vocab.has_oldFarmlet, FarmletOldResource);
                    }
                }
                log.info("getFieldEvent service completed.");
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
        public static final Property has_fieldEventId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_fieldEventId");
        public static final Property has_datasetVersionId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_datasetVersionId");
        public static final Property has_catchmentDisplayName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_catchmentDisplayName");
        public static final Property has_eventDate = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_eventDate");
        public static final Property has_timeIn = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_timeIn");
        public static final Property has_timeOut = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_timeOut");
        public static final Property has_timeInField = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_timeInField");
        public static final Property has_applicationTypeName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_applicationTypeName");
        public static final Property has_fieldName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_fieldName");
        public static final Property has_tempFieldName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_tempFieldName");
        public static final Property has_totalApplication = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_totalApplication");
        public static final Property has_applicationInfo = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_applicationInfo");
        public static final Property has_applicationRate = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_applicationRate");
        public static final Property has_fieldId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_fieldId");
        public static final Property has_tractorId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_tractorId");
        public static final Property has_startTractorHours = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_startTractorHours");
        public static final Property has_endTractorHours = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_endTractorHours");
        public static final Property has_totalTractorHours = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_totalTractorHours");
        public static final Property has_fieldOperationId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_fieldOperationId");
        public static final Property has_fieldApplicationId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_fieldApplicationId");
        public static final Property has_applicationBatchNumber = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_applicationBatchNumber");
        public static final Property has_productName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_productName");
        public static final Property has_manufacturer = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_manufacturer");
        public static final Property has_temporaryFieldId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_temporaryFieldId");
        public static final Property has_catchmentName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_catchmentName");
        public static final Property has_operation_name = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#operation_name");
        public static final Property has_operationGroup = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_operationGroup");
        public static final Property has_applicationName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_applicationName");
        public static final Property has_applicationInfoApps = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_applicationInfoApps");
        public static final Property has_formatName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_formatName");
        public static final Property has_rangeEndDateTime = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_rangeEndDateTime");
        public static final Property has_rangeStartDateTime = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_rangeStartDateTime");
        public static final Property has_tempFieldId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_tempFieldId");
        public static final Property has_newFarmlet = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_newFarmlet");
        public static final Property has_oldFarmlet = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_oldFarmlet");
        // data property
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        // resources
        public static final Resource DatasetVersionId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DatasetVersionId");
        public static final Resource CatchmentDisplayName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentDisplayName");
        public static final Resource EventDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#EventDate");
        public static final Resource TimeIn = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#TimeIn");
        public static final Resource TimeOut = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#TimeOut");
        public static final Resource TimeInField = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#TimeInField");
        public static final Resource ApplicationTypeName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ApplicationTypeName");
        public static final Resource FieldName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FieldName");
        public static final Resource TempField_Name = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#TempField_Name");
        public static final Resource TotalApplication = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#TotalApplication");
        public static final Resource ApplicationInfo = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ApplicationInfo");
        public static final Resource ApplicationRate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ApplicationRate");
        public static final Resource FieldId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FieldId");
        public static final Resource TractorId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#TractorId");
        public static final Resource StartTractorHours = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#StartTractorHours");
        public static final Resource EndTractorHours = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#EndTractorHours");
        public static final Resource TotalTractorHours = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#TotalTractorHours");
        public static final Resource FieldOperationId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FieldOperationId");
        public static final Resource FieldApplicationId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FieldApplicationId");
        public static final Resource ApplicationBatchNumber = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ApplicationBatchNumber");
        public static final Resource ProductName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ProductName");
        public static final Resource Manufacturer = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Manufacturer");
        public static final Resource TemporaryFieldId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#TemporaryFieldId");
        public static final Resource CatchmentName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentName");
        public static final Resource OperationName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Operation_name");
        public static final Resource OperationGroup = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#OperationGroup");
        public static final Resource ApplicationName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ApplicationName");
        public static final Resource ApplicationInfoApps = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ApplicationInfoApps");
        public static final Resource FormatName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FormatName");
        public static final Resource RangeEndDateTime = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#RangeEndDateTime");
        public static final Resource RangeStartDateTime = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#RangeStartDateTime");
        public static final Resource TempFieldId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#TempFieldId");
        public static final Resource NewFarmlet = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#NewFarmlet");
        public static final Resource OldFarmlet = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#OldFarmlet");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getFieldEvent.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getFieldEvent.owl#Output");
    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

