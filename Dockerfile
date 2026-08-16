FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY /out/artifacts/LotteryUpdated_jar/LotteryUpdated.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]