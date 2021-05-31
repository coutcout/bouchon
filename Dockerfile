FROM openjdk:11
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
RUN mkdir -p /mnt/data
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]