import { angularJwt } from 'angular-jwt';
import { permission, uiPermission } from 'angular-permission';
import { passengerController } from './js/controllers/passenger.controller.js';
import { driverController } from './js/controllers/driver.controller.js';
import { historyController } from './js/controllers/history.controller.js';
import { slackLoginController } from './js/controllers/slackLogin.controller.js';
import { addCarController } from './js/controllers/addCar.controller.js';

//var = function scope
//const and let = block scope

// TODO: resolve view routing bugs related to authentication and logout

const app = angular.module('app', ['ui.router', permission, uiPermission, 'angular-jwt']);

app.run(function(authManager) {
	authManager.checkAuthOnRefresh();
	authManager.redirectWhenUnauthenticated();
});

app.config(function($stateProvider, $urlRouterProvider, $httpProvider, jwtOptionsProvider){

	jwtOptionsProvider.config({
		unauthenticatedRedirectPath: '/#/slackLogin',
		authPrefix: '',
		tokenGetter: ['options', function(options) {
			return localStorage.getItem('RideShare_auth_token');
		}]
	});

	$httpProvider.interceptors.push('jwtInterceptor');


	$urlRouterProvider.otherwise('/slackLogin');

	$stateProvider
	.state('slackLogin', {
		url: '/slackLogin',
		templateUrl: 'partials/slackLogin.html',
		controller: slackLoginController
	})

	.state('passenger',{
		url: '/passenger',
		templateUrl : 'partials/passenger.html',
		controller : passengerController,
		data: { requiresLogin: true }
	})

	.state('driver',{
		url: '/driver',
		templateUrl : 'partials/driver.html',
		controller : driverController,
		data: { requiresLogin: true }
	})

	.state('history',{
		url: '/history',
		templateUrl : 'partials/history.html',
		controller : historyController,
		data: { requiresLogin: true }
	})

	.state('addCar' ,{
		url: '/addCar',
		templateUrl : 'partials/addCar.html',
		controller : addCarController,
		data: { requiresLogin: true }
	})


});
