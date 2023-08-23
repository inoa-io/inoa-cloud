###
### graal
###

FROM ${image.graal} AS graal

WORKDIR /build
ARG USE_NATIVE_IMAGE_JAVA_PLATFORM_MODULE_SYSTEM=false

COPY target/lib/ /build/lib/
COPY target/${project.build.finalName}.jar /build/

RUN native-image \
  --no-fallback --static --libc=musl --target=linux-amd64 \
  --report-unsupported-elements-at-runtime \
  -jar ${project.build.finalName}.jar

###
### node
###

FROM ${image.node} AS node

WORKDIR /build

# cache dependences first
COPY ui/package.json ui/package-lock.json /build/
RUN npm clean-install --no-fund --no-audit --strict-peer-deps

# create application
COPY ui /build/
RUN npm run build

###
### runtime
###

# curl is used in docker-compose healthcheck
FROM curlimages/curl

LABEL \
  org.opencontainers.image.title="${project.name}" \
  org.opencontainers.image.description="${project.description}" \
  org.opencontainers.image.url=${project.url} \
  org.opencontainers.image.source=${project.url} \
  org.opencontainers.image.vendor="${project.organization.name}" \
  org.opencontainers.image.version=${project.version} \
  org.opencontainers.image.ref.name=${image.tag} \
  org.opencontainers.image.base.name=curlimages/curl

COPY --from=graal /build/${project.build.finalName} /${project.build.finalName}
COPY --from=node /build/dist /static

USER 1000
EXPOSE 8080

ENTRYPOINT ["/${project.build.finalName}"]
