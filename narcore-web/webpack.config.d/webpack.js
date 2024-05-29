const webpack = require('webpack');

config.resolve.modules.push("../../processedResources/js/main");
config.resolve.conditionNames = ['import', 'require', 'default'];

if (config.devServer) {
    config.devServer.historyApiFallback = true;
    config.devServer.host = "localhost"
    config.devServer.hot = true;
    config.devtool = 'eval-cheap-source-map';

} else {
    config.devtool = undefined;
}

// disable bundle size warning
config.performance = {
    assetFilter: function (assetFilename) {
      return !assetFilename.endsWith('.js');
    },
};

// Add IgnorePlugin to ignore 'moment' module
config.plugins = (config.plugins || []).concat([
    new webpack.IgnorePlugin({
        resourceRegExp: /^moment$/,
    })
]);



//Down -- needed for jsonwebtoken/sign to work
config.resolve = {
  fallback: {
    buffer: require.resolve('buffer'),
    crypto: require.resolve('crypto-browserify'),
    stream: require.resolve('stream-browserify'),
    util: require.resolve('util'),
    vm: require.resolve('vm-browserify'),
    events: require.resolve('events/'),
    process: require.resolve('process/browser'),
  },
};
config.plugins = config.plugins || [];
config.plugins.push(
  new webpack.ProvidePlugin({
    Buffer: ['buffer', 'Buffer'],
    process: 'process/browser',
  })
);
//Up-- needed for jsonwebtoken/sign to work



config.output.publicPath = '/'

