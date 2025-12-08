// frontend/karma.conf.js

module.exports = function (config) {
  config.set({
    // ...
    // Add the launcher if not already configured
    plugins: [
        // ...
        require('karma-chrome-launcher'),
        // ...
    ],

    browsers: ['ChromeHeadlessNoSandbox'], // Use this custom definition

    customLaunchers: {
        ChromeHeadlessNoSandbox: {
            base: 'ChromeHeadless',
            flags: [
                '--no-sandbox', // CRITICAL for running inside Docker
                '--disable-gpu'
            ]
        }
    },
    // ...
  });
};
