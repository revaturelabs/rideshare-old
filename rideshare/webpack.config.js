const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = (env = {}) => {
  let config = {
    context: path.join(__dirname, 'src', 'main', 'webapp', 'static'),
    target: 'web',
    // devtool: 'source-map',
    // devServer: {},
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
          use: [
            'babel-loader'
          ]
        }
      ]
    }//,
    // plugins: [
    //   new HtmlWebpackPlugin({
    //     template: path.join(__dirname, 'src', 'main', 'webapp', 'static', 'index.html'),
    //     filename: './index.html',
    //     hash: false,
    //     inject: true,
    //     compile: true,
    //     favicon: false,
    //     minify: false,
    //     cache: true,
    //     showErrors: true,
    //     title: 'Webpack App'
    //   })
    // ]
  };
  return config;
};