# Estágio de execução (Runtime)
# Usamos o Eclipse Temurin (distribuição oficial recomendada do OpenJDK) 
# na versão 21 com base Alpine para economizar RAM e espaço em disco.
FROM eclipse-temurin:21-jre-alpine

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Define uma variável para o caminho do arquivo JAR (facilita manutenção)
ARG JAR_FILE=target/*.jar

# Copia o arquivo .jar gerado pelo Maven para dentro do container
# O nome 'app.jar' é fixo para o ponto de entrada
COPY ${JAR_FILE} app.jar

# Expõe a porta solicitada
EXPOSE 8081

# Variável de ambiente para configurações extras do Java (como o limite de memória)
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando para iniciar a aplicação
# Utilizamos o array para que o sinal de interrupção (SIGTERM) seja repassado corretamente
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]