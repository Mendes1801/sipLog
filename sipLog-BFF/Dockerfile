# Estágio 1: Build (Compilação)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copia os arquivos de configuração de dependências primeiro (otimiza o cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e compila o projeto pulando os testes para gerar o .jar rápido
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Execução (Imagem enxuta apenas com o JRE)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o .jar gerado no estágio anterior
COPY --from=builder /app/target/*.jar bff-app.jar

# Expõe a porta que o Spring Boot vai rodar (mude se o BFF usar uma porta diferente)
EXPOSE 8081

# Comando de inicialização do BFF
ENTRYPOINT ["java", "-jar", "bff-app.jar"]