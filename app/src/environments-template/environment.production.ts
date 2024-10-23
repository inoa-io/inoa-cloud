export const environment = {
	appTitle: "INOA Groundcontrol",
	production: true,
	backendUri: window.location.origin,
	fleetApiBasePath: window.location.origin,
	fleetApiToken: "",
	measurementApiToken: "",
	version: "${project.version}",
	buildDate: "${maven.build.timestamp}",
	gitBasePath: "${project.parent.scm.url}/commit/",
	gitCommitIdFull: "${git.commit.id.full}",
	gitCommitIdDescription: "${git.commit.id.describe}"
};
