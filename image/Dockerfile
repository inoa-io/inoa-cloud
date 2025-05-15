FROM ghcr.io/grayc-de/plugins/node:20 AS app
RUN rm -rf /root/.yarnrc.yml
WORKDIR /app
ARG YARN_NPM_REGISTRY_SERVER
ARG YARN_UNSAFE_HTTP_WHITELIST
COPY app/package.json app/yarn.lock app/.yarnrc.yml /app/
RUN yarn config --no-defaults && yarn install --immutable
COPY app /app/
RUN yarn build --no-progress --configuration=production

FROM docker.io/eclipse-temurin:17-jre
COPY service/target/libs /app/libs
COPY service/target/inoa-service-*.jar /app/inoa.jar
COPY --from=app /app/dist /app/static
USER 1000
ENTRYPOINT ["java", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/inoa.jar"]
