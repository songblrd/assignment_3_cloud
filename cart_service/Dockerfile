# build app using maven
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# copy wrapper and pom.xml to cache dependencies
COPY .mvn/ .mvn
COPY mvnw mvnw.cmd pom.xml ./
RUN ./mvnw dependency:go-offline

# build jar file then copy src code
COPY src/ src/
RUN ./mvnw clean package -DskipTests

# run app using jre
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy built jar
COPY --from=build /app/target/*.jar app.jar

# expose port 8081
EXPOSE 8081

#run springboot app
ENTRYPOINT ["java", "-jar", "app.jar"]