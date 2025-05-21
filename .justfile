# https://just.systems/man/en/

set unstable := true
set fallback := true
set script-interpreter := ["bash", "-eu"]

export IP := env('IP', '127.0.0.1')
export MCNOIZE := env('MCNOIZE', '1')
export MAVEN_ARGS:= "--color=always --no-transfer-progress -Dmaven.plugin.validation=NONE -Dk3s.skipRm=false -Dk3s.failIfExists=false"

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
	mvn install -pl image -am -P!dev -Dmaven.test.skip

##
## Integration tests
##

# Start k3s instance and deploy everything.
[group('integration-tests')]
up: build start deploy
  xdg-open "http://help.$IP.nip.io:8080"

# Start k3s instance.
[group('integration-tests')]
[working-directory: 'test']
@start:
	mvn k3s:run k3s:image -Dk3s.failIfExists=false

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
	mvn resources:copy-resources@k3s k3s:apply -Dip=$IP

# Build image and redeploy inoa.
[group('integration-tests')]
[working-directory: 'test']
redeploy: build
	mvn k3s:image k3s:restart

