const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const webpack = require('webpack');

let source = path.join(__dirname, 'src', 'main', 'webapp', 'static');
let output = path.join(__dirname, 'src', 'main', 'resources', 'static');

module.exports = (env = {}) => {
  let config = {
    context: path.join(__dirname, 'src', 'main', 'webapp', 'static'),
    target: 'web',
    // devtool: 'source-map',
    devServer: {
      publicPath: '/',
      contentBase: [ path.join(__dirname, 'src', 'main', 'resources', 'static') ]
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
      path: path.join(__dirname, 'src', 'main', 'resources', 'static'),
      filename: '[name].bundle.js'
    },
    module: {
      rules: [
        {
          test: /\.js$/,
          exclude: [ /\/node_modules\// ],
          use: [
            'babel-loader'
          ]
        }
      ]
    },
    plugins: [
      new CopyWebpackPlugin(
        [
          { from: `${source}/css`, to: `${output}/css` },
          { from: `${source}/images`, to: `${output}/images` },
          { from: `${source}/partials`, to: `${output}/partials` },
          { from: `${source}/js/googleMapAPI`, to: `${output}/js/googleMapAPI` },
          { from: `${source}/js/lib`, to: `${output}/js/lib` },
          { from: `${source}/js/moment.js`, to: `${output}/moment.js` },
          { from: `${source}/index.html`, to: `${output}/index.html` },
        ]
      ),
      new webpack.optimize.UglifyJsPlugin({
        mangle: {
          'screw_ie8': true
        },
        compress: {
          'screw_ie8': true,
          'warnings': false
        },
        sourceMap: false
      }),
      new webpack.NoEmitOnErrorsPlugin()
    ]
  };
  return config;
};
