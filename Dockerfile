FROM gradle:7.2-jdk11

WORKDIR /reservation
COPY . .
RUN gradle clean build

CMD gradle bootRun