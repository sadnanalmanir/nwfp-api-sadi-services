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
        String fieldEventId = input.getRequiredProperty(Vocab.has_FieldEventId).getString();
        Model outputModel = output.getModel();

        try {
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getFieldEvents";
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

                    Resource FieldEventIdRes = outputModel.createResource();

                    //Resource IdResource = outputModel.createResource();
                    //IdResource.addProperty(Vocab.type, Vocab.FieldEventId);
                    //IdResource.addLiteral(Vocab.has_value, idVal);
                    //FieldEventIdRes.addProperty(Vocab.fieldEventId, IdResource);

                    if (idVal.equals(fieldEventId)) {

                        Resource DatasetVersionIdResource = outputModel.createResource();
                        DatasetVersionIdResource.addProperty(Vocab.type, Vocab.DatasetVersionId);
                        DatasetVersionIdResource.addLiteral(Vocab.has_value, datasetVersionIdVal);
                        FieldEventIdRes.addProperty(Vocab.datasetVersionId, DatasetVersionIdResource);

                        Resource CatchmentNameResource = outputModel.createResource();
                        CatchmentNameResource.addProperty(Vocab.type, Vocab.CatchmentDisplayName);
                        CatchmentNameResource.addLiteral(Vocab.has_value, catchmentDisplayNameVal);
                        FieldEventIdRes.addProperty(Vocab.catchmentDisplayName, CatchmentNameResource);

                        Resource EventDateResource = outputModel.createResource();
                        EventDateResource.addProperty(Vocab.type, Vocab.EventDate);
                        EventDateResource.addLiteral(Vocab.has_value, eventDateVal);
                        FieldEventIdRes.addProperty(Vocab.eventDate, EventDateResource);

                        Resource TimeInResource = outputModel.createResource();
                        TimeInResource.addProperty(Vocab.type, Vocab.TimeIn);
                        TimeInResource.addLiteral(Vocab.has_value, timeInVal);
                        FieldEventIdRes.addProperty(Vocab.timeIn, TimeInResource);

                        Resource TimeOutResource = outputModel.createResource();
                        TimeOutResource.addProperty(Vocab.type, Vocab.TimeOut);
                        TimeOutResource.addLiteral(Vocab.has_value, timeOutVal);
                        FieldEventIdRes.addProperty(Vocab.timeOut, TimeOutResource);

                        Resource TimeInFieldResource = outputModel.createResource();
                        TimeInFieldResource.addProperty(Vocab.type, Vocab.TimeInField);
                        TimeInFieldResource.addLiteral(Vocab.has_value, timeInFieldVal);
                        FieldEventIdRes.addProperty(Vocab.timeInField, TimeInFieldResource);

                        Resource ApplicationTypeNameResource = outputModel.createResource();
                        ApplicationTypeNameResource.addProperty(Vocab.type, Vocab.ApplicationTypeName);
                        ApplicationTypeNameResource.addLiteral(Vocab.has_value, applicationTypeNameVal);
                        FieldEventIdRes.addProperty(Vocab.applicationTypeName, ApplicationTypeNameResource);

                        Resource FieldNameResource = outputModel.createResource();
                        FieldNameResource.addProperty(Vocab.type, Vocab.FieldName);
                        FieldNameResource.addLiteral(Vocab.has_value, fieldNameVal);
                        FieldEventIdRes.addProperty(Vocab.fieldName, FieldNameResource);

                        Resource TempField_NameResource = outputModel.createResource();
                        TempField_NameResource.addProperty(Vocab.type, Vocab.TempField_Name);
                        TempField_NameResource.addLiteral(Vocab.has_value, tempFieldNameVal);
                        FieldEventIdRes.addProperty(Vocab.tempFieldName, TempField_NameResource);

                        Resource TotalApplicationResource = outputModel.createResource();
                        TotalApplicationResource.addProperty(Vocab.type, Vocab.TotalApplication);
                        TotalApplicationResource.addLiteral(Vocab.has_value, totalApplicationVal);
                        FieldEventIdRes.addProperty(Vocab.totalApplication, TotalApplicationResource);

                        Resource ApplicationInfoResource = outputModel.createResource();
                        ApplicationInfoResource.addProperty(Vocab.type, Vocab.ApplicationInfo);
                        ApplicationInfoResource.addLiteral(Vocab.has_value, applicationInfoVal);
                        FieldEventIdRes.addProperty(Vocab.applicationInfo, ApplicationInfoResource);

                        Resource ApplicationRateResource = outputModel.createResource();
                        ApplicationRateResource.addProperty(Vocab.type, Vocab.ApplicationRate);
                        ApplicationRateResource.addLiteral(Vocab.has_value, applicationRateVal);
                        FieldEventIdRes.addProperty(Vocab.applicationRate, ApplicationRateResource);

                        Resource FieldIdResource = outputModel.createResource();
                        FieldIdResource.addProperty(Vocab.type, Vocab.FieldId);
                        FieldIdResource.addLiteral(Vocab.has_value, fieldIdVal);
                        FieldEventIdRes.addProperty(Vocab.fieldId, FieldIdResource);

                        Resource TractorIdResource = outputModel.createResource();
                        TractorIdResource.addProperty(Vocab.type, Vocab.TractorId);
                        TractorIdResource.addLiteral(Vocab.has_value, tractorIdVal);
                        FieldEventIdRes.addProperty(Vocab.tractorId, TractorIdResource);

                        Resource StartTractorHoursResource = outputModel.createResource();
                        StartTractorHoursResource.addProperty(Vocab.type, Vocab.StartTractorHours);
                        StartTractorHoursResource.addLiteral(Vocab.has_value, startTractorHoursVal);
                        FieldEventIdRes.addProperty(Vocab.startTractorHours, StartTractorHoursResource);

                        Resource EndTractorHoursResource = outputModel.createResource();
                        EndTractorHoursResource.addProperty(Vocab.type, Vocab.EndTractorHours);
                        EndTractorHoursResource.addLiteral(Vocab.has_value, endTractorHoursVal);
                        FieldEventIdRes.addProperty(Vocab.endTractorHours, EndTractorHoursResource);

                        Resource TotalTractorHoursResource = outputModel.createResource();
                        TotalTractorHoursResource.addProperty(Vocab.type, Vocab.TotalTractorHours);
                        TotalTractorHoursResource.addLiteral(Vocab.has_value, totalTractorHoursVal);
                        FieldEventIdRes.addProperty(Vocab.totalTractorHours, TotalTractorHoursResource);

                        Resource FieldOperationIdResource = outputModel.createResource();
                        FieldOperationIdResource.addProperty(Vocab.type, Vocab.FieldOperationId);
                        FieldOperationIdResource.addLiteral(Vocab.has_value, fieldOperationIdVal);
                        FieldEventIdRes.addProperty(Vocab.fieldOperationId, FieldOperationIdResource);

                        Resource FieldApplicationIdResource = outputModel.createResource();
                        FieldApplicationIdResource.addProperty(Vocab.type, Vocab.FieldApplicationId);
                        FieldApplicationIdResource.addLiteral(Vocab.has_value, fieldApplicationIdVal);
                        FieldEventIdRes.addProperty(Vocab.fieldApplicationId, FieldApplicationIdResource);

                        Resource ApplicationBatchNumberResource = outputModel.createResource();
                        ApplicationBatchNumberResource.addProperty(Vocab.type, Vocab.ApplicationBatchNumber);
                        ApplicationBatchNumberResource.addLiteral(Vocab.has_value, applicationBatchNumberVal);
                        FieldEventIdRes.addProperty(Vocab.applicationBatchNumber, ApplicationBatchNumberResource);

                        Resource ProductNameResource = outputModel.createResource();
                        ProductNameResource.addProperty(Vocab.type, Vocab.ProductName);
                        ProductNameResource.addLiteral(Vocab.has_value, productNameVal);
                        FieldEventIdRes.addProperty(Vocab.productName, ProductNameResource);

                        Resource ManufacturerResource = outputModel.createResource();
                        ManufacturerResource.addProperty(Vocab.type, Vocab.Manufacturer);
                        ManufacturerResource.addLiteral(Vocab.has_value, manufacturerVal);
                        FieldEventIdRes.addProperty(Vocab.manufacturer, ManufacturerResource);

                        Resource TemporaryFieldIdResource = outputModel.createResource();
                        TemporaryFieldIdResource.addProperty(Vocab.type, Vocab.TemporaryFieldId);
                        TemporaryFieldIdResource.addLiteral(Vocab.has_value, temporaryFieldIdVal);
                        FieldEventIdRes.addProperty(Vocab.temporaryFieldId, TemporaryFieldIdResource);

                        Resource CatchmentIdResource = outputModel.createResource();
                        CatchmentIdResource.addProperty(Vocab.type, Vocab.CatchmentName);
                        CatchmentIdResource.addLiteral(Vocab.has_value, catchmentNameVal);
                        FieldEventIdRes.addProperty(Vocab.catchmentName, CatchmentIdResource);

                        Resource Operation_nameResource = outputModel.createResource();
                        Operation_nameResource.addProperty(Vocab.type, Vocab.OperationName);
                        Operation_nameResource.addLiteral(Vocab.has_value, operationNameVal);
                        FieldEventIdRes.addProperty(Vocab.operation_name, Operation_nameResource);

                        Resource OperationGroupResource = outputModel.createResource();
                        OperationGroupResource.addProperty(Vocab.type, Vocab.OperationGroup);
                        OperationGroupResource.addLiteral(Vocab.has_value, operationGroupVal);
                        FieldEventIdRes.addProperty(Vocab.operationGroup, OperationGroupResource);

                        Resource ApplicationNameResource = outputModel.createResource();
                        ApplicationNameResource.addProperty(Vocab.type, Vocab.ApplicationName);
                        ApplicationNameResource.addLiteral(Vocab.has_value, applicationNameVal);
                        FieldEventIdRes.addProperty(Vocab.applicationName, ApplicationNameResource);

                        Resource ApplicationInfoAppsResource = outputModel.createResource();
                        ApplicationInfoAppsResource.addProperty(Vocab.type, Vocab.ApplicationInfoApps);
                        ApplicationInfoAppsResource.addLiteral(Vocab.has_value, applicationInfoAppsVal);
                        FieldEventIdRes.addProperty(Vocab.applicationInfoApps, ApplicationInfoAppsResource);

                        Resource FormatNameResource = outputModel.createResource();
                        FormatNameResource.addProperty(Vocab.type, Vocab.FormatName);
                        FormatNameResource.addLiteral(Vocab.has_value, formatNameVal);
                        FieldEventIdRes.addProperty(Vocab.formatName, FormatNameResource);

                        Resource RangeEndDateTimeResource = outputModel.createResource();
                        RangeEndDateTimeResource.addProperty(Vocab.type, Vocab.RangeEndDateTime);
                        RangeEndDateTimeResource.addLiteral(Vocab.has_value, rangeEndDateTimeVal);
                        FieldEventIdRes.addProperty(Vocab.rangeEndDateTime, RangeEndDateTimeResource);

                        Resource RangeStartDateTimeResource = outputModel.createResource();
                        RangeStartDateTimeResource.addProperty(Vocab.type, Vocab.RangeStartDateTime);
                        RangeStartDateTimeResource.addLiteral(Vocab.has_value, rangeStartDateTimeVal);
                        FieldEventIdRes.addProperty(Vocab.rangeStartDateTime, RangeStartDateTimeResource);

                        Resource TempFieldIdResource = outputModel.createResource();
                        TempFieldIdResource.addProperty(Vocab.type, Vocab.TempFieldId);
                        TempFieldIdResource.addLiteral(Vocab.has_value, tempFieldIdVal);
                        FieldEventIdRes.addProperty(Vocab.tempFieldId, TempFieldIdResource);

                        Resource FarmletNewResource = outputModel.createResource();
                        FarmletNewResource.addProperty(Vocab.type, Vocab.NewFarmlet);
                        FarmletNewResource.addLiteral(Vocab.has_value, farmletNewVal);
                        FieldEventIdRes.addProperty(Vocab.newFarmlet, FarmletNewResource);

                        Resource FarmletOldResource = outputModel.createResource();
                        FarmletOldResource.addProperty(Vocab.type, Vocab.OldFarmlet);
                        FarmletOldResource.addLiteral(Vocab.has_value, farmletOldVal);
                        FieldEventIdRes.addProperty(Vocab.oldFarmlet, FarmletOldResource);

                        FieldEventIdRes.addProperty(Vocab.type, output);
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
        public static final Property type = m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

        // properties
        public static final Property fieldEventId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#fieldEventId");
        public static final Property datasetVersionId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#datasetVersionId");
        public static final Property catchmentDisplayName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#catchmentDisplayName");
        public static final Property eventDate = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#eventDate");
        public static final Property timeIn = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#timeIn");
        public static final Property timeOut = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#timeOut");
        public static final Property timeInField = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#timeInField");
        public static final Property applicationTypeName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#applicationTypeName");
        public static final Property fieldName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#fieldName");
        public static final Property tempFieldName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#tempFieldName");
        public static final Property totalApplication = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#totalApplication");
        public static final Property applicationInfo = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#applicationInfo");
        public static final Property applicationRate = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#applicationRate");
        public static final Property fieldId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#fieldId");
        public static final Property tractorId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#tractorId");
        public static final Property startTractorHours = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#startTractorHours");
        public static final Property endTractorHours = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#endTractorHours");
        public static final Property totalTractorHours = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#totalTractorHours");
        public static final Property fieldOperationId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#fieldOperationId");
        public static final Property fieldApplicationId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#fieldApplicationId");
        public static final Property applicationBatchNumber = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#applicationBatchNumber");
        public static final Property productName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#productName");
        public static final Property manufacturer = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#manufacturer");
        public static final Property temporaryFieldId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#temporaryFieldId");
        public static final Property catchmentName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#catchmentName");
        public static final Property operation_name = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#operation_name");
        public static final Property operationGroup = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#operationGroup");
        public static final Property applicationName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#applicationName");
        public static final Property applicationInfoApps = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#applicationInfoApps");
        public static final Property formatName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#formatName");
        public static final Property rangeEndDateTime = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#rangeEndDateTime");
        public static final Property rangeStartDateTime = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#rangeStartDateTime");
        public static final Property tempFieldId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#tempFieldId");
        public static final Property newFarmlet = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#newFarmlet");
        public static final Property oldFarmlet = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#oldFarmlet");
        // data property
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        public static final Property has_FieldEventId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_FieldEventId");
        // resources
        public static final Resource FieldEventId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FieldEventId");
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

