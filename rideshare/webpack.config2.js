const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = (env = {}) => {
  let config = {
    context: path.join(__dirname, 'src', 'main', 'resources', 'static'),
    target: 'web',
    devtool: 'source-map',
    // devServer: {},
    resolve: {
      extensions: [ '.js' ],
      modules: [ './node_modules' ]
    },
    resolveLoader: {
      modules: [ './node_modules' ]
    },
    entry: {
      main: [ '' ]
    },
    output: {
      path: path.join(),
      filename: ''
    },
    module: {
      rules: [
        {
          test: /\.js$/,
          exclude: [ /\/node_modules\// ]
        },
        {
          
        }
      ]
    }
  };
  return config;
};