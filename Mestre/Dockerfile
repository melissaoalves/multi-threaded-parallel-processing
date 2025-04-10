FROM openjdk:17

WORKDIR /app

COPY src/ /app/src/

RUN javac src/main/java/MestreServer.java

CMD ["java", "src/main/java/MestreServer.java"]

EXPOSE 8080
