FROM docker.io/library/node:20.19.3-slim@sha256:f8f6771d949ff351c061de64ef9cbfbc5949015883fb3b016b34aca6d0e5b8cc AS app
WORKDIR /app
ARG YARN_NPM_REGISTRY_SERVER
ARG YARN_UNSAFE_HTTP_WHITELIST
COPY app/package.json app/yarn.lock app/.yarnrc.yml /app/
RUN --mount=type=cache,target=/root \
	--mount=type=cache,target=/tmp \
	corepack enable && \
	yarn config --no-defaults && \
	yarn install --immutable
COPY app /app/
RUN --mount=type=cache,target=/app/.angular \
	--mount=type=cache,target=/root \
	--mount=type=cache,target=/tmp \
	yarn build --no-progress --configuration=production

FROM docker.io/library/maven:3.9.10-eclipse-temurin-21@sha256:615bd38fa00dd2416d90cbbc99895b9955400fa8110e83646e3e04ebcb7c471e AS mvn
WORKDIR /app
ARG MAVEN_ARGS="--batch-mode --color=always --no-transfer-progress -DskipTests -P=-dev"
ARG MAVEN_MIRROR_CENTRAL=https://mirror.grayc.io/maven2
RUN mkdir "$HOME/.m2" && printf "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\
<settings>\n\
	<localRepository>/tmp/mvn-repo</localRepository>\n\
	<mirrors>\n\
		<mirror>\n\
			<url>%s</url>\n\
			<mirrorOf>central</mirrorOf>\n\
		</mirror>\n\
	</mirrors>\n\
</settings>" "$MAVEN_MIRROR_CENTRAL" > "$HOME/.m2/settings.xml" && cat "$HOME/.m2/settings.xml" 
COPY pom.xml lombok.config ./
COPY api/pom.xml api/pom.xml
COPY service/pom.xml service/pom.xml
RUN mvn install -N
COPY api/ api/
RUN mvn install -pl api
COPY service/ service/
RUN mvn install -pl service

FROM docker.io/library/eclipse-temurin:21.0.7_6-jre@sha256:ffed502a66e8be6e27b6317fcd5913e967dd79e1e2f5135b2c1ffce557930ec3
COPY --chown=0:0 --from=mvn /app/service/target/libs /app/libs
COPY --chown=0:0 --from=mvn /app/service/target/inoa-service-*.jar /app/inoa.jar
COPY --chown=0:0 --from=app /app/dist /app/static
USER 1000
ENTRYPOINT ["java", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/inoa.jar"]
