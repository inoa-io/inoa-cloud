###
### this is our build image with maven, java 17,node 18 and other stuff
###
FROM ghcr.io/grayc-de/base/java:17-jdk as JAVA
FROM ghcr.io/grayc-de/plugins/maven as MAVEN
FROM ghcr.io/grayc-de/plugins/node:20

### curl

RUN apt-get -qq update \
  && apt-get -qq install --yes --no-install-recommends curl=7.88.1-10+deb12u8 \
  && rm -rf /etc/*- /var/cache/* /var/log/* /var/lib/apt/lists/* /var/lib/dpkg/*-old

### java 17

COPY --from=JAVA /opt/java /opt/java
ENV PATH=${PATH}:/opt/java/bin

### maven

COPY --from=MAVEN /opt/maven /opt/maven
COPY settings.xml /opt/maven/settings.xml
ENV MAVEN_ARGS="--batch-mode --no-transfer-progress --fail-at-end --settings /opt/maven/settings.xml --color=always"
ENV MAVEN_OPTS="-Dorg.slf4j.simpleLogger.showDateTime=true"
ENV PATH=${PATH}:/opt/maven/bin

### dockerd

RUN apt-get -qq update \
  && apt-get -qq install --yes --no-install-recommends docker.io=20.10.24+dfsg1-1+deb12u1 ca-certificates=20230311 \
  && rm -rf /etc/*- /var/cache/* /var/log/* /var/lib/apt/lists/* /var/lib/dpkg/*-old \
  && DOCKER="dockerd --log-level=error --data-root=/woodpecker/docker &> /dev/null &" \
  && echo "$DOCKER" > /usr/bin/dockerd-start \
  && chmod +x /usr/bin/dockerd-start
