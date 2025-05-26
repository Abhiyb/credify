FROM openjdk:17-jdk-slim
ADD target/investment-portfolio-tracker-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
