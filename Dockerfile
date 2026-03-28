FROM eclipse-temurin:21-jdk-alpine
RUN apk add --no-cache postgresql-client
WORKDIR /app
# Assumes you have already run 'mvn clean package -DskipTests' locally
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
