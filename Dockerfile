#######################################################################################
FROM ubuntu:23.04 as jre_builder

RUN apt-get update && \
    apt-get install -y \
      openjdk-18-jre-headless \
      curl \
      zip \
      unzip

RUN mkdir /usr/src/n-queens
WORKDIR /usr/src/n-queens

COPY .mvn .mvn
COPY mvnw mvnw
RUN chmod +x mvnw
COPY pom.xml pom.xml
COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw package

#######################################################################################
FROM ubuntu:23.04 as native_builder

RUN apt-get update && \
    apt-get install -y \
      build-essential \
      curl \
      libzip-dev \
      zip \
      unzip

RUN curl -s "https://get.sdkman.io" | bash 
RUN /bin/bash -c "source /root/.sdkman/bin/sdkman-init.sh && sdk install java 21.0.2-graal"

RUN mkdir /usr/src/n-queens
WORKDIR /usr/src/n-queens

COPY .mvn .mvn
COPY mvnw mvnw
RUN chmod +x mvnw
COPY pom.xml pom.xml
COPY src src

#ENV JAVA_HOME="/graalvm-ce-java17-22.3.1"
ENV PATH="/root/.sdkman/candidates/java/current/bin:${PATH}"
ENV JAVA_HOME="/root/.sdkman/candidates/java/current"

RUN --mount=type=cache,target=/root/.m2 ./mvnw -Pnative package

#######################################################################################
FROM ubuntu:23.04

RUN apt-get update && \
    apt-get install -y \
      bc \
      openjdk-18-jre-headless

COPY --from=jre_builder /usr/src/n-queens/target/n-queens-1.0-SNAPSHOT.jar nqueens.jar
COPY --from=native_builder /usr/src/n-queens/target/nqueens nqueens
COPY compare_performance.sh compare_performance.sh

RUN chmod +x nqueens
RUN chmod +x compare_performance.sh

ENTRYPOINT ["./compare_performance.sh"]
