export const environment = {
	appTitle: "INOA Groundcontrol",
	backendUri: window.location.origin,
	fleetApiBasePath: "http://127.0.0.1:1080",
	fleetApiToken: "foo-bar",
	measurementApiBasePath: "http://127.0.0.1:1080",
	measurementApiToken: "foo-bar",
	version: "${project.version}",
	buildDate: "${maven.build.timestamp}",
	gitBasePath: "${project.parent.scm.url}/commit/",
	gitCommitIdFull: "${git.commit.id.full}",
	gitCommitIdDescription: "${git.commit.id.describe}"
};
