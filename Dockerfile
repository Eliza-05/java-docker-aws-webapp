FROM eclipse-temurin:21

WORKDIR /usrapp/bin

ENV PORT 8080

COPY /target/classes /usrapp/bin/classes
COPY /target/dependency /usrapp/bin/dependency

CMD ["java", "-cp", "./classes:./dependency/*", "edu.eci.tdse.MicroSpringBoot"]