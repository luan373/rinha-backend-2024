FROM ghcr.io/graalvm/jdk-community:21
COPY target/luan-javalin.jar /luan-javalin.jar
# This is the port that your javalin application will listen on
#EXPOSE 9080:9080
ENTRYPOINT ["java", "-jar", "/luan-javalin.jar"]