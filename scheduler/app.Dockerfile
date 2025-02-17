FROM eclipse-temurin:17-jdk-jammy

ARG PROFILE
ENV PROFILE=${PROFILE}
ARG JAR_FILE=build/libs/*.jar
ARG LOGBACK-FILE=logback-spring.xml
COPY ${JAR_FILE} app.jar
COPY src/main/resources/${LOGBACK-FILE} config/${LOGBACK-FILE}
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul",  "-jar", "app.jar", "--spring.profiles.active=${PROFILE}"]