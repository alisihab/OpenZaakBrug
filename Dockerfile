FROM openjdk:11-jdk
WORKDIR /home
COPY ./target/zds-to-zgw.jar zds-to-zgw.jar
ENTRYPOINT ["java", "-jar", "zds-to-zgw.jar"]