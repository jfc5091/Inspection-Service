FROM openjdk:11-jre-slim
VOLUME /tmp
ADD /target/Inspection-Service.jar Inspection-Service.jar
RUN bash -c 'touch /Inspection-Service.jar'
EXPOSE 9140
ENTRYPOINT ["java", "-Dspring.profiles.active=qa", "-jar", "/Inspection-Service.jar"]