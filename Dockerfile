FROM openjdk:11-jdk AS builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} target/application.jar
RUN java -Djarmode=layertools -jar target/application.jar extract

FROM openjdk:11-jdk
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]