FROM docker.io/library/node:20.20.0-slim@sha256:6c51af7dc83f4708aaac35991306bca8f478351cfd2bda35750a62d7efcf05bb AS app
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

FROM docker.io/library/maven:3.9.12-eclipse-temurin-21@sha256:594fe27803ed961f664eea91851107622bd39b67a801f12ba583c4419252295e AS mvn
WORKDIR /app
ARG MAVEN_ARGS="--batch-mode --color=always --no-transfer-progress -DskipTests -P=-dev"
ARG MAVEN_MIRROR_CENTRAL=https://mirror.grayc.dev/maven2
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

FROM docker.io/library/eclipse-temurin:21.0.7_6-jre@sha256:bca347dc76e38a60a1a01b29a7d1312e514603a97ba594268e5a2e4a1a0c9a8f
COPY --chown=0:0 --from=mvn /app/service/target/libs /app/libs
COPY --chown=0:0 --from=mvn /app/service/target/inoa-service-*.jar /app/inoa.jar
COPY --chown=0:0 --from=app /app/dist /app/static
USER 1000
ENTRYPOINT ["java", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/inoa.jar"]
