FROM eclipse-temurin:18-jre-alpine
WORKDIR /app
ARG JAR_FILE
COPY target/${JAR_FILE} app.jar
ENV MATCHING_ENGINE_IP="MATCHING_ENGINE_IP not set" \
    SPRING_PROFILES_ACTIVE="dev"
ENTRYPOINT ["java", "-jar", "app.jar"]