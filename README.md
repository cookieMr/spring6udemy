# Spring 6 App

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/cookieMr/spring6udemy/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/cookieMr/spring6udemy/tree/master)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=cookieMr_spring6udemy&metric=alert_status)](https://sonarcloud.io/summary/overall?id=cookieMr_spring6udemy)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=cookieMr_spring6udemy&metric=coverage)](https://sonarcloud.io/summary/overall?id=cookieMr_spring6udemy)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=cookieMr_spring6udemy&metric=sqale_index)](https://sonarcloud.io/summary/overall?id=cookieMr_spring6udemy)

[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=cookieMr_spring6udemy&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=cookieMr_spring6udemy)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=cookieMr_spring6udemy&metric=bugs)](https://sonarcloud.io/summary/overall?id=cookieMr_spring6udemy)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=cookieMr_spring6udemy&metric=code_smells)](https://sonarcloud.io/summary/overall?id=cookieMr_spring6udemy)

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=cookieMr_spring6udemy&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=cookieMr_spring6udemy)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=cookieMr_spring6udemy&metric=security_rating)](https://sonarcloud.io/summary/overall?id=cookieMr_spring6udemy)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=cookieMr_spring6udemy&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=cookieMr_spring6udemy)

This is a follow along for a [Udemy course](https://www.udemy.com/course/spring-framework-6-beginner-to-guru/).

## Running the App

### Run local MySQL as a Docker Container
Run MySQL docker image:
```bash
docker run --publish 3306:3306 \
  --name local-mysql \
  --env MYSQL_ROOT_PASSWORD=mysql_very_secret_root_password \
  --volume ./src/main/resources/sql:/docker-entrypoint-initdb.d \
  --health-interval 7s \
  --health-timeout 7s \
  --health-retries 4 \
  --health-start-period 10s \
  --health-cmd 'mysqladmin ping mysql' \
  --detach mysql:8.1.0
```

### Run Local App (with necessary dockers)
First of all run the local MySQL as a Docker Container.

And then run the Gradle task:
```bash
./gradlew clean build test check
./gradlew bootRun --args='--spring.profiles.active=local'
```

To build the same Docker image and to run it as a docker composed app
(along with all necessary dockers e.g. MySQL) just run the following command:
```bash
docker-compose down --volumes --remove-orphans && \
docker-compose up --build
```

To check if a Spring Boot App is healthy run:
```bash
curl -f http://localhost:8080/api/actuator/health && echo -e
```

## Useful Internal Links
* [H2 Console](http://[::1]:8080/h2-console) (available only with H2, by default config points to MySQL)
* [Swagger - OpenAPI v3](http://[::1]:8080/api/swagger-ui/index.html)

## Useful External Links
* [Lazydocker](https://github.com/jesseduffield/lazydocker)
