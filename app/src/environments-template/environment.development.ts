export const environment = {
	appTitle: "INOA Groundcontrol",
	backendUri: window.location.origin,
	fleetApiBasePath: window.location.origin + "/api",
	fleetApiToken: "",
	measurementApiToken: "",
	version: "${project.version}",
	buildDate: "${maven.build.timestamp}",
	gitBasePath: "${project.parent.scm.url}/commit/",
	gitCommitIdFull: "${git.commit.id.full}",
	gitCommitIdDescription: "${git.commit.id.describe}"
};
