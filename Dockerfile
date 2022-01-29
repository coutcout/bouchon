FROM openjdk:11
ARG JAR_FILE
ARG CONFIG_FOLDER

COPY ${JAR_FILE} app.jar
COPY ${CONFIG_FOLDER} config

RUN mkdir -p /mnt/data \
        mkdir -p /mnt/logs
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.config.location=config/application.yml"]