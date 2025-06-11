FROM docker.io/library/node:20.19.2-slim@sha256:7cd3fbc830c75c92256fe1122002add9a1c025831af8770cd0bf8e45688ef661 AS app
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

FROM docker.io/library/maven:3.9.9-eclipse-temurin-21@sha256:3a4ab3276a087bf276f79cae96b1af04f53731bec53fb2e651aca79e4b10211e AS mvn
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
RUN --mount=type=cache,target=/tmp/mvn-repo mvn install -N
COPY api/ api/
RUN --mount=type=cache,target=/tmp/mvn-repo mvn install -pl api
COPY service/ service/
RUN --mount=type=cache,target=/tmp/mvn-repo mvn install -pl service

FROM docker.io/library/eclipse-temurin:21.0.7_6-jre@sha256:02df6e67e0d0ba516810a238629ae194e1d88cd6c75674bc74fc7030763ee0b6
COPY --chown=0:0 --from=mvn /app/service/target/libs /app/libs
COPY --chown=0:0 --from=mvn /app/service/target/inoa-service-*.jar /app/inoa.jar
COPY --chown=0:0 --from=app /app/dist /app/static
USER 1000
ENTRYPOINT ["java", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/inoa.jar"]
