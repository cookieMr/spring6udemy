#########################################################
## Build Stage
#########################################################
FROM amazoncorretto:17 as build
WORKDIR /workspace
COPY . ./
RUN ./gradlew clean build --exclude-task test --no-build-cache

#########################################################
## Test Stage
#########################################################
FROM build as tests
RUN ./gradlew test

#########################################################
## Build Application Stage
#########################################################
FROM amazoncorretto:17
WORKDIR /app
COPY ./entrypoint.sh ./
RUN chmod u+x entrypoint.sh
COPY --from=build /workspace/build/libs/*.jar /app/app.jar
CMD ["./entrypoint.sh"]
