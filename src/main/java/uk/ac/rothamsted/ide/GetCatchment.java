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

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

@Name("getCatchment")
@Description("Get all catchments and their properties")
@ContactEmail("sadnanalmanir@gmail.com")
@InputClass("http://localhost:8080/ontology/service-ontology/getCatchment.owl#Input")
@OutputClass("http://localhost:8080/ontology/service-ontology/getCatchment.owl#Output")
public class GetCatchment extends SimpleSynchronousServiceServlet {
    private static final Logger log = Logger.getLogger(GetCatchment.class);

    @Override
    public void processInput(Resource input, Resource output) {

        PropertyConfigurator.configure(log.getClass().getClassLoader().getResource("log4j.properties"));

        log.info("Service invoked: getCatchments");
        Model outputModel = output.getModel();

        try {

            URL url = new URL("https://nwfp.rothamsted.ac.uk:8443/getCatchments");

            InputStreamReader reader = new InputStreamReader(url.openStream());
            JsonArray jsonArray = new Gson().fromJson(reader, JsonArray.class);
            reader.close();

            Iterator<JsonElement> elementIterator = jsonArray.iterator();
            JsonObject element;

            while (elementIterator.hasNext()) {

                element = elementIterator.next().getAsJsonObject();

                String idVal = element.get("Id").getAsString();
                String nameVal = element.get("Name").getAsString();
                String displayNameVal = element.get("DisplayName").getAsString();
                String validFromVal = getNullAsEmptyString(element.get("ValidFrom"));
                String validUntilVal = getNullAsEmptyString(element.get("ValidUntil"));
                float hydrologicalCatchmentAreaVal = element.get("HydrologicalCatchmentArea").getAsFloat();
                float fencedCatchmentAreaVal = element.get("FencedCatchmentArea").getAsFloat();

                //log.info("id: "+ id);
                //log.info("name: "+ name);

                Resource catchment = outputModel.createResource();
                // enabling Catchment rdf:type for the root node as instance of {Catchment} does not work on hydra gui
                //catchment.addProperty(Vocab.type, Vocab.Catchment);


                Resource IdResource = outputModel.createResource();
                IdResource.addProperty(Vocab.type, Vocab.Id);
                IdResource.addLiteral(Vocab.has_value, idVal);
                catchment.addProperty(Vocab.id, IdResource);

                Resource NameResource = outputModel.createResource();
                NameResource.addProperty(Vocab.type, Vocab.Name);
                NameResource.addLiteral(Vocab.has_value, nameVal);
                catchment.addProperty(Vocab.name, NameResource);

                Resource DisplayNameResource = outputModel.createResource();
                DisplayNameResource.addProperty(Vocab.type, Vocab.DisplayName);
                DisplayNameResource.addLiteral(Vocab.has_value, displayNameVal);
                catchment.addProperty(Vocab.displayName, DisplayNameResource);

                Resource ValidFromResource = outputModel.createResource();
                ValidFromResource.addProperty(Vocab.type, Vocab.ValidFromDate);
                ValidFromResource.addLiteral(Vocab.has_value, validFromVal);
                catchment.addProperty(Vocab.validFrom, ValidFromResource);

                Resource ValidUntilResource = outputModel.createResource();
                ValidUntilResource.addProperty(Vocab.type, Vocab.ValidUntilDate);
                ValidUntilResource.addLiteral(Vocab.has_value, validUntilVal);
                catchment.addProperty(Vocab.validUntil, ValidUntilResource);

                Resource HydrologicalCatchmentAreaResource = outputModel.createResource();
                HydrologicalCatchmentAreaResource.addProperty(Vocab.type, Vocab.HydrologicalCatchmentArea);
                HydrologicalCatchmentAreaResource.addLiteral(Vocab.has_value, hydrologicalCatchmentAreaVal);
                catchment.addProperty(Vocab.hydrologicalCatchmentArea, HydrologicalCatchmentAreaResource);

                Resource FencedCatchmentAreaResource = outputModel.createResource();
                FencedCatchmentAreaResource.addProperty(Vocab.type, Vocab.FencedCatchmentArea);
                FencedCatchmentAreaResource.addLiteral(Vocab.has_value, fencedCatchmentAreaVal);
                catchment.addProperty(Vocab.fencedCatchmentArea, FencedCatchmentAreaResource);

                catchment.addProperty(Vocab.type, output);

            }


        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public static final class Vocab {
        private static final Model m_model = ModelFactory.createDefaultModel();
        public static final Property type = m_model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        public static final Property id = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#id");
        public static final Property name = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#name");
        public static final Property displayName = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#displayName");
        public static final Property validFrom = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#validFrom");
        public static final Property validUntil = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#validUntil");
        public static final Property hydrologicalCatchmentArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#hydrologicalCatchmentArea");
        public static final Property fencedCatchmentArea = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#fencedCatchmentArea");

        public static final Property has_value = m_model.createProperty("http://localhost:8080/ontology/domain-ontology/nwf.owl#has_value");

        public static final Resource Id = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Id");
        public static final Resource Name = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Name");
        public static final Resource DisplayName = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#DisplayName");
        public static final Resource ValidFromDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ValidFromDate");
        public static final Resource ValidUntilDate = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#ValidUntilDate");
        public static final Resource Catchment = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Catchment");
        public static final Resource FencedCatchmentArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#FencedCatchmentArea");
        public static final Resource HydrologicalCatchmentArea = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#HydrologicalCatchmentArea");
        public static final Resource Input = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Input");
        public static final Resource Output = m_model.createResource("http://localhost:8080/ontology/domain-ontology/nwf.owl#Output");

    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}

