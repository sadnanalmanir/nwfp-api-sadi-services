#
FROM maven:latest AS MAVEN_TOOL_CHAIN

# copy project config and source
COPY pom.xml /tmp/
COPY src /tmp/src/

# set directory for running instructions that follow
WORKDIR /tmp/

# compile and package java sources into deployable archive
RUN mvn package


#FROM tomcat:7 # does NOT work with POSTing RDF data, only n3 format works with SADI services
#FROM tomcat:8
FROM tomcat:9
# tomcat:10.x and later implements jakarta.servlet.http.HttpServlet instead of javax.servlet.http.HttpServlet

# expose port 8080
EXPOSE 8080

# deploy domain ontology, service ontologies, and input files onto tomcat server
COPY ontology $CATALINA_HOME/webapps/ontology

# deploy the compiled archive onto tomcat server
COPY --from=MAVEN_TOOL_CHAIN /tmp/target/nwfp*.war $CATALINA_HOME/webapps/nwfp-api-sadi-services.war

# deploy hydra GUI backend with pre-built queries
#COPY hydra-gui-backend $CATALINA_HOME/webapps/hydra-gui-backend


HEALTHCHECK --interval=1m --timeout=3s CMD wget --quiet --tries=1 --spider http://localhost:8080/nwfp-api-sadi-services/ || exit 1