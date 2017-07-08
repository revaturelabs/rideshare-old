const path = require('path');

const config = {
  context: path.join(__dirname, './Application/src/main/resources/static'),
  entry: './app',
  output: {
    path: path.join(__dirname, './Application/src/main/resources/static'),
    filename: 'index.js'
  },
  resolve: {
    extensions: ['', '.js']
  },
  module: {
    loaders: [
      {
        test: /\.js$/,
        exclude: /(node_modules)/,
        loader: 'babel-loader'
      },
      {
        test: /\.js$/,
        exclude: /(node_modules)/,
        loader: 'eslint-loader'
      }
    ]
  },
};

module.exports = config;
