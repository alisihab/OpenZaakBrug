FROM openjdk:11-jdk
ENV TZ=Europe/Amsterdam
WORKDIR /home
COPY ./target/zds-to-zgw.jar zds-to-zgw.jar
ENTRYPOINT ["java", "-jar", "zds-to-zgw.jar"]