import { angularJwt } from 'angular-jwt';
import { mainController } from './js/controllers/main.controller.js';
import { passengerController } from './js/controllers/passenger.controller.js';
import { driverController } from './js/controllers/driver.controller.js';
import { historyController } from './js/controllers/history.controller.js';
import { slackLoginController } from './js/controllers/slackLogin.controller.js';
import { errorController } from './js/controllers/error.controller.js';
import { adminRidesController } from './js/controllers/adminRides.controller.js';
import { adminUsersController } from './js/controllers/adminUsers.controller.js';
import { adminPoiController } from './js/controllers/adminPOI.controller.js';
import { userProfileController } from './js/controllers/userProfile.controller.js';
import { authService } from './js/auth.service.js'

//var = function scope
//const and let = block scope 

const app = angular.module('app', ['ui.router', 'angular-jwt']);

app.service('authService', authService);

app.run(function(authManager, authService) {
	authManager.checkAuthOnRefresh();
	authManager.redirectWhenUnauthenticated();
});

app.config(function($stateProvider, $urlRouterProvider, $httpProvider, jwtOptionsProvider, authService){
	
	jwtOptionsProvider.config({
		loginPath: '/#/login',
		unauthenticatedRedirectPath: '/',
		authPrefix: '',
		tokenGetter: ['options', function(options) {
			return localStorage.getItem('RideShare_auth_token');
		}],
		whiteListedDomains: ['maps.googleapis.com']
	});

	$httpProvider.interceptors.push('jwtInterceptor');


	$urlRouterProvider.otherwise('/login');

	$stateProvider
		.state('main', {
			url: '/main',
			templateUrl: 'partials/main.html',
			controller: mainController,
			data: { requiresLogin: true }
		})
	
		.state('slackLogin', {
			url: '/login',
			templateUrl: 'partials/slackLogin.html',
			controller: slackLoginController
		})
	
		.state('main.passenger',{
			url: '/passenger',
			templateUrl : 'partials/passenger.html',
			controller : passengerController,
			data: { requiresLogin: true }
		})
	
		.state('main.driver',{
			url: '/driver',
			templateUrl : 'partials/driver.html',
			controller : driverController,
			data: { requiresLogin: true }
		})
	
		.state('main.adminRides' , {
			url: '/adminRides', 
			templateUrl : 'partials/adminRides.html',
			controller : adminRidesController,
			data: { requiresLogin: true }
		})
		
		.state('main.adminUsers', {
			url: '/adminUsers',
			templateUrl: 'partials/adminUsers.html',
			controller : adminUsersController,
			data: { requiresLogin: true }
		})
		
		.state('main.adminPoi',{
			url: '/adminPoi',
			templateUrl : 'partials/poi.html',
			controller : adminPoiController,
			data: { requiresLogin: true }
		})
    
		.state('main.userProfile', {
			url: '/userProfile',
			templateUrl : 'partials/userProfile.html',
			controller : userProfileController,
			data: { requiresLogin: true }
		})

		.state('error', {
			url: '/error',
			templateUrl: 'partials/error.html',
			controller: errorController
		})
});
