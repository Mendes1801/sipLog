FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Pega o .jar que o JENKINS já compilou e enviou para a pasta target/
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Executa o app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]