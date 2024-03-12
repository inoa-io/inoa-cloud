FROM ghcr.io/grayc-de/node:20 AS app
WORKDIR /app

COPY app/package.json app/yarn.lock app/.yarnrc.yml /app/
RUN corepack enable && yarn install --immutable && rm -rf .yarn /root/.cache /root/.yarn /tmp/*

COPY app /app/
RUN yarn build --configuration=production && rm -rf .angular /root/.cache /root/.yarn /tmp/*

# TODO monaco is included in a very strange way - check!
RUN rm -rf \
  /app/dist/assets/monaco-editor/*.md \
  /app/dist/assets/monaco-editor/*.txt \
  /app/dist/assets/monaco-editor/*.json \
  /app/dist/assets/monaco-editor/dev \
  /app/dist/assets/monaco-editor/esm

FROM docker.io/eclipse-temurin:17-jre
WORKDIR /app

COPY --from=app /app/dist /app/static
COPY service/target/libs /app/libs
COPY service/target/inoa-service-*.jar /app/inoa.jar

ENTRYPOINT ["java", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/inoa.jar"]
USER 1000
