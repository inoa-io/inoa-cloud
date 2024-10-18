// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html

module.exports = (config) => {
	config.set({
		frameworks: ["jasmine", "@angular-devkit/build-angular"],
		plugins: [require("karma-jasmine"), require("karma-chrome-launcher"), require("@angular-devkit/build-angular/plugins/karma")],
		browsers: ["ChromeHeadlessCI"],
		customLaunchers: {
			ChromeHeadlessCI: {
				base: "ChromeHeadless",
				flags: ["--no-sandbox"]
			}
		}
	});
};
