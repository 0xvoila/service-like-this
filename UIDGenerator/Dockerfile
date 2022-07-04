FROM maven as build

COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package


FROM jetty:9-jre8-openjdk
COPY --from=build /home/app/target/root.war /var/lib/jetty/webapps
EXPOSE 8080
CMD java -jar "$JETTY_HOME/start.jar"
