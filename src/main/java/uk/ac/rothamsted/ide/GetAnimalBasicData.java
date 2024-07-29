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

@Name("getAnimalBasicData")
@Description("NWFP rest API: Get information about the basic animal data based on the identifier")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getAnimalBasicData.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getAnimalBasicData.owl#Output")
public class GetAnimalBasicData extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(GetAnimalBasicData.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));
        log.info("*** SADI Service ***");
        log.info("Invoking SADI service: getAnimalBasicData");
        // Extract the animalBasicDataId from the input RDF:
        int animalBasicData_Id = input.getRequiredProperty(Vocab.has_AnimalBasicDataId).getInt();
        // create instance of the output model
        Model outputModel = output.getModel();

        try {
            // initiate GET request to the endpoint
            String endPoint = "https://nwfp.rothamsted.ac.uk:8443/getAnimalBasicData";
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
            if (status == HttpURLConnection.HTTP_OK){
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
                    if (idVal.getInt() == animalBasicData_Id) {
                        String officialTagVal = getNullAsEmptyString(element.get("OfficialTag"));
                        String managementTagVal = getNullAsEmptyString(element.get("ManagementTag"));
                        String breedVal = getNullAsEmptyString(element.get("Breed"));
                        String dateOfBirthVal = getNullAsEmptyString(element.get("DateOfBirth"));
                        String grazingYearVal = getNullAsEmptyString(element.get("GrazingYear"));
                        String endGrazingYearVal = getNullAsEmptyString(element.get("EndGrazingYear"));
                        String genderVal = getNullAsEmptyString(element.get("Gender"));
                        String farmletNameVal = getNullAsEmptyString(element.get("farmletName"));
                        String sireIdVal = getNullAsEmptyString(element.get("SireId"));
                        String birthDamIdVal = getNullAsEmptyString(element.get("BirthDamId"));
                        String rearingDamIdVal = getNullAsEmptyString(element.get("RearingDamId"));
                        String birthLitterSizeVal = getNullAsEmptyString(element.get("BirthLitterSize"));
                        String rearingLitterSizeVal = getNullAsEmptyString(element.get("RearingLitterSize"));
                        String animalIdVal = getNullAsEmptyString(element.get("AnimalId"));
                        String commentVal = getNullAsEmptyString(element.get("Comments"));
                        String rangeStartDateTimeVal = getNullAsEmptyString(element.get("RangeStartDateTime"));
                        String rangeEndDateTimeVal = getNullAsEmptyString(element.get("RangeEndDateTime"));
                        String animalCategoryNameVal = getNullAsEmptyString(element.get("AnimalCategoryName"));
                        String breedingAnimalVal = getNullAsEmptyString(element.get("BreedingAnimal"));

                        // populate the output model with instances and literal values
                        Resource OfficialTagResource = outputModel.createResource();
                        OfficialTagResource.addProperty(Vocab.type, Vocab.OfficialTag);
                        OfficialTagResource.addLiteral(Vocab.has_value, officialTagVal);
                        output.addProperty(Vocab.officialTag, OfficialTagResource);

                        Resource ManagementTagResource = outputModel.createResource();
                        ManagementTagResource.addProperty(Vocab.type, Vocab.ManagementTag);
                        ManagementTagResource.addLiteral(Vocab.has_value, managementTagVal);
                        output.addProperty(Vocab.managementTag, ManagementTagResource);

                        Resource BreedResource = outputModel.createResource();
                        BreedResource.addProperty(Vocab.type, Vocab.Breed);
                        BreedResource.addLiteral(Vocab.has_value, breedVal);
                        output.addProperty(Vocab.breed, BreedResource);

                        Resource DateOfBirthResource = outputModel.createResource();
                        DateOfBirthResource.addProperty(Vocab.type, Vocab.DateOfBirth);
                        DateOfBirthResource.addLiteral(Vocab.has_value, dateOfBirthVal);
                        output.addProperty(Vocab.dateOfBirth, DateOfBirthResource);

                        Resource GrazingYearResource = outputModel.createResource();
                        GrazingYearResource.addProperty(Vocab.type, Vocab.GrazingYear);
                        GrazingYearResource.addLiteral(Vocab.has_value, grazingYearVal);
                        output.addProperty(Vocab.grazingYear, GrazingYearResource);

                        Resource EndGrazingYearResource = outputModel.createResource();
                        EndGrazingYearResource.addProperty(Vocab.type, Vocab.EndGrazingYear);
                        EndGrazingYearResource.addLiteral(Vocab.has_value, endGrazingYearVal);
                        output.addProperty(Vocab.endGrazingYear, EndGrazingYearResource);

                        Resource GenderResource = outputModel.createResource();
                        GenderResource.addProperty(Vocab.type, Vocab.Gender);
                        GenderResource.addLiteral(Vocab.has_value, genderVal);
                        output.addProperty(Vocab.gender, GenderResource);

                        Resource FarmletNameResource = outputModel.createResource();
                        FarmletNameResource.addProperty(Vocab.type, Vocab.FarmletName);
                        FarmletNameResource.addLiteral(Vocab.has_value, farmletNameVal);
                        output.addProperty(Vocab.farmletName, FarmletNameResource);

                        Resource SireIdResource = outputModel.createResource();
                        SireIdResource.addProperty(Vocab.type, Vocab.SireId);
                        SireIdResource.addLiteral(Vocab.has_value, sireIdVal);
                        output.addProperty(Vocab.sireId, SireIdResource);

                        Resource BirthDamIdResource = outputModel.createResource();
                        BirthDamIdResource.addProperty(Vocab.type, Vocab.BirthDamId);
                        BirthDamIdResource.addLiteral(Vocab.has_value, birthDamIdVal);
                        output.addProperty(Vocab.birthDamId, BirthDamIdResource);

                        Resource RearingDamIdResource = outputModel.createResource();
                        RearingDamIdResource.addProperty(Vocab.type, Vocab.RearingDamId);
                        RearingDamIdResource.addLiteral(Vocab.has_value, rearingDamIdVal);
                        output.addProperty(Vocab.rearingDamId, RearingDamIdResource);

                        Resource BirthLitterSizeResource = outputModel.createResource();
                        BirthLitterSizeResource.addProperty(Vocab.type, Vocab.BirthLitterSize);
                        BirthLitterSizeResource.addLiteral(Vocab.has_value, birthLitterSizeVal);
                        output.addProperty(Vocab.birthLitterSize, BirthLitterSizeResource);

                        Resource RearingLitterSizeResource = outputModel.createResource();
                        RearingLitterSizeResource.addProperty(Vocab.type, Vocab.RearingLitterSize);
                        RearingLitterSizeResource.addLiteral(Vocab.has_value, rearingLitterSizeVal);
                        output.addProperty(Vocab.rearingLitterSize, RearingLitterSizeResource);

                        Resource AnimalIdResource = outputModel.createResource();
                        AnimalIdResource.addProperty(Vocab.type, Vocab.AnimalId);
                        AnimalIdResource.addLiteral(Vocab.has_value, animalIdVal);
                        output.addProperty(Vocab.animalId, AnimalIdResource);

                        Resource CommentResource = outputModel.createResource();
                        CommentResource.addProperty(Vocab.type, Vocab.Comment);
                        CommentResource.addLiteral(Vocab.has_value, commentVal);
                        output.addProperty(Vocab.comment, CommentResource);

                        Resource RangeStartDateTimeResource = outputModel.createResource();
                        RangeStartDateTimeResource.addProperty(Vocab.type, Vocab.RangeStartDateTime);
                        RangeStartDateTimeResource.addLiteral(Vocab.has_value, rangeStartDateTimeVal);
                        output.addProperty(Vocab.rangeStartDateTime, RangeStartDateTimeResource);

                        Resource RangeEndDateTimeResource = outputModel.createResource();
                        RangeEndDateTimeResource.addProperty(Vocab.type, Vocab.RangeEndDateTime);
                        RangeEndDateTimeResource.addLiteral(Vocab.has_value, rangeEndDateTimeVal);
                        output.addProperty(Vocab.rangeEndDateTime, RangeEndDateTimeResource);

                        Resource AnimalCategoryNameResource = outputModel.createResource();
                        AnimalCategoryNameResource.addProperty(Vocab.type, Vocab.AnimalCategoryName);
                        AnimalCategoryNameResource.addLiteral(Vocab.has_value, animalCategoryNameVal);
                        output.addProperty(Vocab.animalCategoryName, AnimalCategoryNameResource);

                        Resource BreedingAnimalResource = outputModel.createResource();
                        BreedingAnimalResource.addProperty(Vocab.type, Vocab.BreedingAnimal);
                        BreedingAnimalResource.addLiteral(Vocab.has_value, breedingAnimalVal);
                        output.addProperty(Vocab.breedingAnimal, BreedingAnimalResource);
                    }
                }
                log.info("getAnimalBasicData service completed.");
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
        public static final Property officialTag = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_officialTag");
        public static final Property managementTag = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_managementTag");
        public static final Property breed = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_breed");
        public static final Property dateOfBirth = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_dateOfBirth");
        public static final Property grazingYear = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_grazingYear");
        public static final Property endGrazingYear = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_endGrazingYear");
        public static final Property gender = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_gender");
        public static final Property farmletName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_farmletName");
        public static final Property sireId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_sireId");
        public static final Property birthDamId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_birthDamId");
        public static final Property rearingDamId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_rearingDamId");
        public static final Property birthLitterSize = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_birthLitterSize");
        public static final Property rearingLitterSize = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_rearingLitterSize");
        public static final Property animalId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_animalId");
        public static final Property comment = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_comment");
        public static final Property rangeStartDateTime = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_rangeStartDateTime");
        public static final Property rangeEndDateTime = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_rangeEndDateTime");
        public static final Property animalCategoryName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_animalCategoryName");
        public static final Property breedingAnimal = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_breedingAnimal");
        // data property
        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");
        public static final Property has_AnimalBasicDataId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_AnimalBasicDataId");
        // Resources
        public static final Resource OfficialTag = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#OfficialTag");
        public static final Resource ManagementTag = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ManagementTag");
        public static final Resource Breed = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Breed");
        public static final Resource DateOfBirth = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DateOfBirth");
        public static final Resource GrazingYear = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#GrazingYear");
        public static final Resource EndGrazingYear = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#EndGrazingYear");
        public static final Resource Gender = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Gender");
        public static final Resource FarmletName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FarmletName");
        public static final Resource SireId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#SireId");
        public static final Resource BirthDamId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#BirthDamId");
        public static final Resource RearingDamId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#RearingDamId");
        public static final Resource BirthLitterSize = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#BirthLitterSize");
        public static final Resource RearingLitterSize = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#RearingLitterSize");
        public static final Resource AnimalId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#AnimalId");
        public static final Resource Comment = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Comment");
        public static final Resource RangeStartDateTime = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#RangeStartDateTime");
        public static final Resource RangeEndDateTime = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#RangeEndDateTime");
        public static final Resource AnimalCategoryName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#AnimalCategoryName");
        public static final Resource BreedingAnimal = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#BreedingAnimal");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getAnimalBasicData.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getAnimalBasicData.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

