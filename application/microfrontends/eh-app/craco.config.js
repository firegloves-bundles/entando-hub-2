const path = require('path');

module.exports = {
    webpack: {
      configure: (webpackConfig, { env, paths }) => {
        const appPackageJson = require(paths.appPackageJson);
        const entry = paths.appIndexJs; 
        const output = {
            filename: `static/js/${appPackageJson.name}-${appPackageJson.version}.js`,
            library: {
                type: 'umd',
                name: 'eh-app',
            },
            path: path.resolve(__dirname, 'build')
        };
        paths.appBuild = output.path;
        webpackConfig.entry = entry;
        webpackConfig.output = output;
 
        return webpackConfig;
      },
    },
  };
