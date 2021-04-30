FROM adoptopenjdk/openjdk11:alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} ./
ENTRYPOINT ["java","-jar","./sudoku-multi-jvm-1.0.jar"]