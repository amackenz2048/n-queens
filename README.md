# Overview

A simple n-queens implementation using kotlin and coroutines to perform a multi-threaded search for solutions. Includes
builds for a JVM and native version using graalvm.

# Building

## JVM

```
./mvnw package
```

## Native

```
./mvnw -Pnative package
```

# Testing

You can use the provided docker container to build/run both the JVM and native binaries. This build uses a cache mount
so will require buildkit.

```
DOCKER_BUILDKIT=1 docker build . -t foo && docker run --rm foo
```