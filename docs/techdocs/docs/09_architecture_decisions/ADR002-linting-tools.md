---

tags:

- architecture
- decisions

---

# ADR002: Linting Tools

- Status: {==__DECIDED__==}
- Date: __2024-10-18__

## Context

In a project that is developed by multiple people, some rules for code and file formatting are needed.
These rules should be verified within the CI pipelines.

We do have (mainly) these scenarios for linting of files in the INOA project:

1. Java source code `*.java`
2. Yaml files for configurations `*.yaml`
3. SQL for database updates `*.sql`
4. Markdown for documentation `*.md`
5. Shell scripts for executing stuff `*.sh`
6. Kustomize files for k8s configurations `/fluxcd/**/*.yaml`
7. Dockerfiles for image generation `Dockerfile` and `Dockerfile.dockerignore`
8. Backstage catalog files for software documentation `catalog-info.yaml` and `docs/backstage/**/*.yaml`

All these types should be formatted in a standardized way.

## Options

### A) Different Tools per Technology

There are multiple solutions to lint for the different scenarios that are commonly used:

1. **Java** - [Checkstyle](https://checkstyle.sourceforge.io/)
2. **Typescript** - [Typescript ESLint](https://typescript-eslint.io/) (ESLint & Prettier)
3. **Yaml** - [Yamllint](https://www.yamllint.com/)
4. **SQL** - [SQLFluff](https://sqlfluff.com/)
5. **Markdown** - [Markdownlint](https://github.com/DavidAnson/markdownlint)
6. **Shell** - [Shellcheck](https://www.shellcheck.net/)
7. **Kustomize** - [kubectl kustomize](https://kubernetes.io/docs/reference/kubectl/generated/kubectl_kustomize/)
8. **Dockerfiles** - [Hadolint](https://github.com/hadolint/hadolint)
9. **Backstage** - [Backstage Entity Validator](https://github.com/RoadieHQ/backstage-entity-validator)

### B) Spotless Maven Plugin

An alternative "one-for-all" solution is the [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven).

1. It supports - out of the box - the linting (and formatting) of the scenarios 1. - 6. (**Java, Typescript, Yaml, SQL, Markdown, Shell**).
2. It also supports the automated formatting of files according the predefined rules.
3. The Java Support and ruleset is compatible with IntelliJ and Eclipse IDEs.

## Decision

We decided to use **Option B), the Maven Spotless plugin** as linter and formatter for scenario **1. - 6.** for the following reasons:

1. Less configuration - Simplicity
2. Automated formatting via `mvn spotless:apply` reduces effort for developers

All linting steps are done within the woodpecker workflow [lint.yaml](../../../../.woodpecker/lint.yaml).

That means these tools are in use:

1. **Java** - [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven)
2. **Typescript** - [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven)
3. **Yaml** - [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven)
4. **SQL** - [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven)
5. **Markdown** - [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven)
6. **Shell** - [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven)
7. **Kustomize** - [kubectl kustomize](https://kubernetes.io/docs/reference/kubectl/generated/kubectl_kustomize/)
8. **Dockerfiles** - [Hadolint](https://github.com/hadolint/hadolint)
9. **Backstage** - [Backstage Entity Validator](https://github.com/RoadieHQ/backstage-entity-validator)

## Consequences

1. The rules of Spotless for **Yaml**, **Markdown** and maybe **Typescript** are not equal to the rules of the common tools. Therefor formatting of non maven projects will differ from INOA.
2. The rules for markdown are not configurable in Maven Spotless Plugin (at time of this decision). So we can't adjust them to be equal to [Markdownlint](https://github.com/DavidAnson/markdownlint).
3. The linting is done within a maven build step, which couples the non java technologies (**Typescript, Yaml, Markdown**) more into the java world and increases complexity for these artefacts.
4. For the linting of the last three scenarios (**Kustomize, Dockerfiles, Backstage**) we are still using individual tools.

