#!/bin/bash
set +x;
set +e;

# this file tries to remove some noise and defaults from grafana dashboards 

strip () {
	< "$1" jq --sort-keys \
		| jq 'del(.iteration)' \
		| jq 'del(.style)' \
		| jq 'del(.id)' \
		| jq 'del(.refresh)' \
		| jq 'if (.annotations.list[0].builtIn == 1) then del(.annotations.list[0]) else . end' \
		| jq 'if (.fiscalYearStartMonth == 0) then del(.fiscalYearStartMonth) else . end' \
		| jq 'if (.liveNow == false) then del(.liveNow) else . end' \
		| jq '.editable=false' \
		| jq '.version=1' \
		| jq '.graphTooltip=2' \
		| jq '.time.to="now"' \
		| jq 'walk(if type == "array" then map(select(. != null)) elif type == "object" then with_entries(select(.value != null and .value != "" and .value != [] and .value != {})) else . end)' \
		> "$1.tmp"
	rm "$1"
	mv "$1.tmp" "$1"
}
export -f strip

# apply to all json files touched in last 1 day

find . -type f -mtime -1 -wholename '**/*.json' -exec bash -c 'strip "$@"' bash {} +
