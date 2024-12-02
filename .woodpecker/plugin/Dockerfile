###
### this is our build image with maven, java 17,node 18 and other stuff
###

FROM ghcr.io/grayc-de/plugins/node:20

### curl

RUN apt-get -qq update \
  && apt-get -qq install --yes --no-install-recommends curl \
  && rm -rf /etc/*- /var/cache/* /var/log/* /var/lib/apt/lists/* /var/lib/dpkg/*-old

### java 17

COPY --from=ghcr.io/grayc-de/base/java:17-jdk /opt/java /opt/java
ENV PATH=${PATH}:/opt/java/bin

### maven

COPY --from=ghcr.io/grayc-de/plugins/maven /opt/maven /opt/maven
COPY settings.xml /opt/maven/settings.xml
ENV MAVEN_ARGS="--batch-mode --no-transfer-progress --fail-at-end --settings /opt/maven/settings.xml --color=always"
ENV MAVEN_OPTS="-Dorg.slf4j.simpleLogger.showDateTime=true"
ENV PATH=${PATH}:/opt/maven/bin

### dockerd

RUN apt-get -qq update \
  && apt-get -qq install --yes --no-install-recommends docker.io ca-certificates \
  && rm -rf /etc/*- /var/cache/* /var/log/* /var/lib/apt/lists/* /var/lib/dpkg/*-old \
  && DOCKER="dockerd --log-level=error --data-root=/woodpecker/docker &> /dev/null &" \
  && echo "$DOCKER" > /usr/bin/dockerd-start \
  && chmod +x /usr/bin/dockerd-start
