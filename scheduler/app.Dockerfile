FROM eclipse-temurin:17-jdk-jammy

ARG PROFILE
ENV PROFILE=${PROFILE}
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY build/resources/main/logback-spring.xml config/logback-spring.xml
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul",  "-jar", "app.jar", "--spring.profiles.active=${PROFILE}"]
