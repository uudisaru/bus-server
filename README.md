# Bus location server

![Java CI with Maven](https://github.com/uudisaru/bus-server/workflows/Java%20CI%20with%20Maven/badge.svg)

The service publishes bus locations to web clients via HTTP Server sent events.
The service is a part of Docker demo - see (bus-app)[https://github.com/uudisaru/bus-app] for complete application.

This project uses Quarkus web and vertx reactive frameworks and is implemented in Kotlin programming language.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```
./mvnw quarkus:dev
```

## Building Docker image

Docker image is built using quarkus Jib maven plugin:

```bash
$ ./build.sh
```
