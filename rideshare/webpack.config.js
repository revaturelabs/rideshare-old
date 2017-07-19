const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const webpack = require('webpack');

module.exports = (env = {}) => {
  let config = {
    context: path.join(__dirname, 'src', 'main', 'webapp', 'static'),
    target: 'web',
    // devtool: 'source-map',
    devServer: {
      publicPath: '/',
      contentBase: [ path.join(__dirname, 'src', 'main', 'webapp', 'static') ]
    },
    resolve: {
      extensions: [ '.js' ],
      modules: [ './node_modules' ]
    },
    resolveLoader: {
      modules: [ './node_modules' ]
    },
    entry: {
      app: [ './app.js' ]
    },
    output: {
      path: path.join(__dirname, 'src', 'main', 'webapp', 'static'),
      filename: '[name].bundle.js'
    },
    module: {
      rules: [
        {
          test: /\.js$/,
          exclude: [ /\/node_modules\// ],
          use: {
            loader: 'babel-loader',
            options: {
              presets: [ 'env' ]
            }
          }
        }
      ]
    },
    plugins: [
      new webpack.optimize.UglifyJsPlugin({
        mangle: {
          'screw_ie8': true
        },
        compress: {
          'screw_ie8': true,
          'warnings': false
        },
        sourceMap: false
      })
    ]
  };
  return config;
};
