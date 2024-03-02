FROM openjdk:21-jdk-slim AS BUILD

ARG module
RUN test -n "${module}"
ARG profile=DOCKER_BUILD_${module}

WORKDIR app
COPY .mvn ./.mvn
COPY mvnw ./mvnw
COPY pom.xml ./pom.xml
#COPY ${module} ./${module}                                      |#WHEN MULTIMODULE
COPY src ./src

RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline
#RUN ./mvnw -P ${profile} -am clean install -DskipTests=true     |#WHEN MULTIMODULE
RUN ./mvnw -am clean install -DskipTests=true
RUN cp ./target/*.jar ./app.jar

FROM openjdk:21-jdk-slim AS RUN

WORKDIR app
COPY --from=BUILD /app/app.jar ./

ENTRYPOINT ["java", "-jar", "./app.jar", "org.springframework.boot.loader.JarLauncher"]