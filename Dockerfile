FROM openjdk:21-jdk-slim AS BUILD

###  ============     DEFINE MODULE TO BUILD     ============  ###
ARG module
RUN test -n "${module}"
ARG profile=DOCKER_BUILD_${module}

###  ============   PREPARE SOURCES TO COMPILE   ============  ###
WORKDIR app
COPY .mvn ./.mvn
COPY mvnw ./mvnw
COPY pom.xml ./pom.xml
COPY ${module} ./${module}
COPY logging ./logging

###  ============      EXECUTE  COMPILATION      ============  ###
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline
RUN ./mvnw -P ${profile} -am clean install -DskipTests=true
RUN cp ./${module}/target/*.jar ./app.jar

FROM openjdk:21-jdk-slim AS RUN

WORKDIR app
COPY --from=BUILD /app/app.jar ./

###  ============        EXECUTE  PROJECT        ============  ###
ENTRYPOINT ["java", "-jar", "./app.jar", "org.springframework.boot.loader.JarLauncher"]