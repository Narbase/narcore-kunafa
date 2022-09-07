config.devServer = config.devServer || {}; // create devServer in case it is undefined
config.devServer.historyApiFallback = true;
//config.devServer.watchOptions = {
//    "aggregateTimeout": 20000,
//    "poll": 1000
//}
config.module.rules.push( {
      test: /\.css$/i,
      use: ['style-loader', 'css-loader'],
    }
);
config.module.rules.push( {
          test: /\.(woff(2)?|ttf|eot|svg)(\?v=\d+\.\d+\.\d+)?$/,
          use: [
            {
              loader: 'file-loader',
              options: {
                name: '[name].[ext]',
                outputPath: 'fonts/'
              }
            }
          ]
        }
);

config.output = config.output || {}; // create output in case it is undefined
config.output.publicPath = '/'
config.devServer.host = "localhost"

//For production
//config.mode = "production";
//config.devtool = '';
