FROM docker.io/kokuwaio/node:20@sha256:2f85acbab9704cee2a0c2247b0cd00091e066b9074b08900de01d043e040f454 AS app
WORKDIR /app
ARG YARN_NPM_REGISTRY_SERVER
ARG YARN_UNSAFE_HTTP_WHITELIST
COPY --chown=0:0 app/package.json app/yarn.lock app/.yarnrc.yml /app/
RUN --mount=type=cache,target=/root \
	--mount=type=cache,target=/tmp \
	yarn config --no-defaults && \
	yarn install --immutable
COPY --chown=0:0 app /app/
RUN --mount=type=cache,target=/app/.angular \
	--mount=type=cache,target=/root \
	--mount=type=cache,target=/tmp \
	yarn build --no-progress --configuration=production

FROM docker.io/eclipse-temurin:21.0.7_6-jre@sha256:3e08d54ec5a8780227a87ef2458a26c27c4b110e4443d25f055fbe2f96907139
COPY --chown=0:0 service/target/libs /app/libs
COPY --chown=0:0 service/target/inoa-service-*.jar /app/inoa.jar
COPY --chown=0:0 --from=app /app/dist /app/static
USER 1000
ENTRYPOINT ["java", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/inoa.jar"]
