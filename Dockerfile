FROM openjdk:11
ARG JAR_FILE
ARG CONFIG_FOLDER

COPY ${JAR_FILE} /home/app.jar
COPY ${CONFIG_FOLDER} /home/config

RUN mkdir -p /mnt/data \
        mkdir -p /mnt/logs \
        mkdir -p /home/bouchon/config \

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/home/app.jar", "--spring.config.location=/home/config/application.yml"]