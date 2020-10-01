FROM adoptopenjdk:8-jre-openj9

EXPOSE 8010

ADD build/libs/*.jar team.jar

ENTRYPOINT ["java", "-jar", "team.jar"]
