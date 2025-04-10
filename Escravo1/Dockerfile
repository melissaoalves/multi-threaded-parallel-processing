FROM openjdk:17

WORKDIR /app

COPY src/ /app/src/

RUN javac src/main/java/Escravo1Server.java

CMD ["java", "src/main/java/Escravo1Server.java"]

EXPOSE 8081
