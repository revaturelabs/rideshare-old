/* Dependencies */
import 'angular';
import '@uirouter/angularjs';
import 'angular-cookies';
import 'angular-animate';
import 'angular-touch';
import 'moment';
import 'angular-bootstrap-datetimepicker';
import { angularJwt } from 'angular-jwt';
import 'bootstrap/dist/js/bootstrap.min.js';

/* Styles */
import './css/app.css';
import 'bootstrap/dist/css/bootstrap.min.css';
// import 'bootstrap/dist/css/bootstrap-theme.min.css';
import 'angular-bootstrap-datetimepicker/src/css/datetimepicker.css';

/* Client Application Components */
import { configure, setup } from './app.config.js';
import { authFactory } from './js/auth.factory.js';

//var = function scope
//const and let = block scope

const app = angular.module('app', [
	'ui.router',
	'ngCookies',
	'ngAnimate',
	'ngTouch',
	'angular-jwt',
	'ui.bootstrap.datetimepicker'
]).factory('authFactory', [
	'$window',
	'$log',
	'$cookies',
	'jwtHelper',
	authFactory // this must always be the last element of this array
]);

app.config([
	'$stateRegistryProvider',
	'$urlServiceProvider',
	'jwtOptionsProvider',
	'$httpProvider',
	'$cookiesProvider',
	configure // this must always be the last element of this array
]);
app.run([
	'$stateRegistry',
	'$http',
	'$log',
	'$state',
	'$urlService',
	'authManager',
	'authFactory',
	setup // this must always be the last element of this array
]);
