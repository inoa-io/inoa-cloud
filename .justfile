# https://just.systems/man/en/

set fallback := true

[private]
@default:
	just --list --unsorted

# Lint files
@lint:
	docker run --rm --read-only --volume=$(pwd):$(pwd):ro --workdir=$(pwd) kokuwaio/yamllint --strict || true
	docker run --rm --read-only --volume=$(pwd):$(pwd):rw --workdir=$(pwd) kokuwaio/markdownlint || true
	docker run --rm --read-only --volume $(pwd):$(pwd):ro --workdir $(pwd) kokuwaio/hadolint
	docker run --rm --read-only --volume=$(pwd):$(pwd):ro --workdir=$(pwd) kokuwaio/renovate
	docker run --rm --read-only --volume=$(pwd):$(pwd):ro --workdir=$(pwd) kokuwaio/shellcheck
	docker run --rm --read-only --volume=$(pwd):$(pwd):ro --workdir=$(pwd) woodpeckerci/woodpecker-cli lint
	docker run --rm --read-only --volume=$(pwd):$(pwd):ro --workdir=$(pwd) ghcr.io/grayc-de/plugins/backstage-entity-validator
