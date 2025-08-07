# https://just.systems/man/en/

set dotenv-load := true
set unstable := true
set fallback := true
set script-interpreter := ["bash", "-eu"]

export IP := env('IP', '127.0.0.1')
export GOOGLE_CLIENT_ID := env('GOOGLE_CLIENT_ID', 'google-client-id')
export GOOGLE_CLIENT_SECRET := env('GOOGLE_CLIENT_SECRET', 'google-client-secret')
export MCNOIZE := env('MCNOIZE', '1')
export MAVEN_ARGS:= "--color=always --no-transfer-progress -Dmaven.plugin.validation=NONE -Dk3s.skipRm=false -Dk3s.failIfExists=false -Dformatter.skip -Dtidy.skip -Dimpsort.skip"

[private]
@default:
	just --list --unsorted

# Lint files
@lint:
	docker run --rm --read-only --volume=$(pwd):$(pwd):ro --workdir=$(pwd) kokuwaio/yamllint --strict
	docker run --rm --read-only --volume=$(pwd):$(pwd):rw --workdir=$(pwd) kokuwaio/markdownlint --fix
	docker run --rm --read-only --volume $(pwd):$(pwd):ro --workdir $(pwd) kokuwaio/hadolint
	docker run --rm --read-only --volume=$(pwd):$(pwd):ro --workdir=$(pwd) kokuwaio/renovate
	docker run --rm --read-only --volume=$(pwd):$(pwd):ro --workdir=$(pwd) kokuwaio/shellcheck
	docker run --rm --read-only --volume=$(pwd):$(pwd):ro --workdir=$(pwd) woodpeckerci/woodpecker-cli lint
	docker run --rm --read-only --volume=$(pwd):$(pwd):ro --workdir=$(pwd) ghcr.io/grayc-de/plugins/backstage-entity-validator

##
## Build
##

# Build image with all dependencies.
build:
	docker build . --tag ghcr.io/inoa-io/inoa:snapshot


# Inspect image with dive
[script]
dive: build
	if hash dive 2>/dev/null; then
		dive ghcr.io/inoa-io/inoa:snapshot
	else
		docker run --rm --interactive --tty --volume=/var/run/docker.sock:/var/run/docker.sock:ro wagoodman/dive:latest ghcr.io/inoa-io/inoa:snapshot
	fi

##
## Integration tests
##

# Start the UI App locally
[group('groundcontrol')]
[working-directory: 'app']
ui:
  export IP=$IP && yarn start

# ensure all tools are present to launch GroundControl locally.
[group('groundcontrol')]
@ui-init:
  #!/bin/bash
  asdf install
  asdf exec nodejs 20.19.4 corepack enable
  asdf reshim nodejs
  cd app && yarn set version 4.9.2 && yarn install

# Start k3s instance and deploy everything.
[group('integration-tests')]
up: build start deploy
  xdg-open "http://help.$IP.nip.io:8080"

# Start k3s instance.
[group('integration-tests')]
[working-directory: 'test']
@start:
	mvn k3s:run k3s:image

# Stop k3s instance.
[group('integration-tests')]
[working-directory: 'test']
@stop:
	mvn k3s:rm -Dk3s.skipRm=false

# Restart k3s instance and deploy everything.
[group('integration-tests')]
restart: stop start deploy

# Deploy everything.
[group('integration-tests')]
[working-directory: 'test']
deploy:
	mvn resources:copy-resources@k3s k3s:apply -Dip=$IP -Dmcnoize.replicas=$MCNOIZE -Dgoogle.client.id=$GOOGLE_CLIENT_ID -Dgoogle.client.secret=$GOOGLE_CLIENT_SECRET

# Build image and redeploy inoa.
[group('integration-tests')]
[working-directory: 'test']
redeploy: build
	mvn k3s:image k3s:restart

# Run integration tests.
[group('integration-tests')]
[working-directory: 'test']
test:
	mvn integration-test -Dip=$IP

# Run integration tests after running `just up` or `just test` for fast test execution.
[group('integration-tests')]
[working-directory: 'test']
test-only:
	mvn integration-test -Dk3s.skip -Dip=$IP
