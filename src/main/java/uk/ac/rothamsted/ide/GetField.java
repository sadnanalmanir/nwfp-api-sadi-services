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

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

@Name("getField")
@Description("Get all catchments and their properties")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getField.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getField.owl#Output")
public class GetField extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(GetField.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("Service invoked: getField");
        Model outputModel = output.getModel();

        try {

            URL url = new URL("https://nwfp.rothamsted.ac.uk:8443/getFields");

            InputStreamReader reader = new InputStreamReader(url.openStream());
            JsonArray jsonArray = new Gson().fromJson(reader, JsonArray.class);
            reader.close();

            Iterator<JsonElement> elementIterator = jsonArray.iterator();
            JsonObject element;

            while (elementIterator.hasNext()) {

                element = elementIterator.next().getAsJsonObject();

                String idVal = getNullAsEmptyString(element.get("Id"));
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


                Resource field = outputModel.createResource();
                // enabling Catchment rdf:type for the root node as instance of {Catchment} does not work on hydra gui
                //catchment.addProperty(Vocab.type, Vocab.Catchment);


                Resource IdResource = outputModel.createResource();
                IdResource.addProperty(Vocab.type, Vocab.Id);
                IdResource.addLiteral(Vocab.has_value, idVal);
                field.addProperty(Vocab.id, IdResource);

                Resource DisplayIdResource = outputModel.createResource();
                DisplayIdResource.addProperty(Vocab.type, Vocab.DisplayName);
                DisplayIdResource.addLiteral(Vocab.has_value, displayIdVal);
                field.addProperty(Vocab.displayId, DisplayIdResource);

                Resource NameResource = outputModel.createResource();
                NameResource.addProperty(Vocab.type, Vocab.Name);
                NameResource.addLiteral(Vocab.has_value, nameVal);
                field.addProperty(Vocab.name, NameResource);

                Resource ValidFromResource = outputModel.createResource();
                ValidFromResource.addProperty(Vocab.type, Vocab.ValidFromDate);
                ValidFromResource.addLiteral(Vocab.has_value, validFromVal);
                field.addProperty(Vocab.validFrom, ValidFromResource);

                Resource ValidUntilResource = outputModel.createResource();
                ValidUntilResource.addProperty(Vocab.type, Vocab.ValidUntilDate);
                ValidUntilResource.addLiteral(Vocab.has_value, validUntilVal);
                field.addProperty(Vocab.validUntil, ValidUntilResource);

                Resource CuttingAreaResource = outputModel.createResource();
                CuttingAreaResource.addProperty(Vocab.type, Vocab.CuttingArea);
                CuttingAreaResource.addLiteral(Vocab.has_value, cuttingAreaVal);
                field.addProperty(Vocab.cuttingArea, CuttingAreaResource);

                Resource FencedAreaResource = outputModel.createResource();
                FencedAreaResource.addProperty(Vocab.type, Vocab.FencedArea);
                FencedAreaResource.addLiteral(Vocab.has_value, fencedAreaVal);
                field.addProperty(Vocab.fencedArea, FencedAreaResource);

                Resource OrganicSpreadingAreaResource = outputModel.createResource();
                OrganicSpreadingAreaResource.addProperty(Vocab.type, Vocab.OrganicSpreadingArea);
                OrganicSpreadingAreaResource.addLiteral(Vocab.has_value, organicSpreadingAreaVal);
                field.addProperty(Vocab.organicSpreadingArea, OrganicSpreadingAreaResource);

                Resource InorganicSpreadingAreaResource = outputModel.createResource();
                InorganicSpreadingAreaResource.addProperty(Vocab.type, Vocab.InorganicSpreadingArea);
                InorganicSpreadingAreaResource.addLiteral(Vocab.has_value, inorganicSpreadingAreaVal);
                field.addProperty(Vocab.inorganicSpreadingArea, InorganicSpreadingAreaResource);

                Resource CatchmentIdResource = outputModel.createResource();
                CatchmentIdResource.addProperty(Vocab.type, Vocab.CatchmentId);
                CatchmentIdResource.addLiteral(Vocab.has_value, catchment_idVal);
                field.addProperty(Vocab.catchmentId, CatchmentIdResource);

                Resource HydrologicalAreaResource = outputModel.createResource();
                HydrologicalAreaResource.addProperty(Vocab.type, Vocab.HydrologicalArea);
                HydrologicalAreaResource.addLiteral(Vocab.has_value, hydrologicalAreaVal);
                field.addProperty(Vocab.hydrologicalArea, HydrologicalAreaResource);


                field.addProperty(Vocab.type, output);

            }


        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public static final class Vocab {
        private static final Model m_model = ModelFactory.createDefaultModel();
        public static final Property type = m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        public static final Property id = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#id");
        public static final Property displayId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#displayId");
        public static final Property name = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#name");
        public static final Property validFrom = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#validFrom");
        public static final Property validUntil = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#validUntil");
        public static final Property cuttingArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#cuttingArea");
        public static final Property fencedArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#fencedArea");
        public static final Property organicSpreadingArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#organicSpreadingArea");
        public static final Property inorganicSpreadingArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#inorganicSpreadingArea");
        public static final Property catchmentId = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#catchmentId");
        public static final Property hydrologicalArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#hydrologicalArea");


        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");

        public static final Resource Id = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Id");
        public static final Resource Name = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Name");
        public static final Resource DisplayName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DisplayName");
        public static final Resource ValidFromDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ValidFromDate");
        public static final Resource ValidUntilDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ValidUntilDate");
        public static final Resource Field = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Field");
        public static final Resource CuttingArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CuttingArea");
        public static final Resource FencedArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FencedArea");
        public static final Resource OrganicSpreadingArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#OrganicSpreadingArea");
        public static final Resource InorganicSpreadingArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#InorganicSpreadingArea");
        public static final Resource CatchmentId = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#CatchmentId");
        public static final Resource HydrologicalArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#HydrologicalArea");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/service-ontology/getField.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/service-ontology/getField.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

