FROM eclipse-temurin:17-jre

WORKDIR /usr/src/app

COPY eco-core/target/eco-core-1.0.0.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java","-jar","app.jar"]