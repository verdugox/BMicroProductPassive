FROM openjdk:11
VOLUME /tmp
EXPOSE 8084
ADD ./target/ProductPassive-0.0.1-SNAPSHOT.jar ms-productpassive.jar
ENTRYPOINT ["java", "-jar","/ms-productpassive.jar"]

