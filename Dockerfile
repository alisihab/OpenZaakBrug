FROM openjdk:11-jdk
WORKDIR /home
COPY ./target/zds-to-zgw.jar zds-to-zgw.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=travis-ci", "zds-to-zgw.jar"]