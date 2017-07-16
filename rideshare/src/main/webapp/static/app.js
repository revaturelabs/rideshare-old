import { angularJwt } from 'angular-jwt';
import { permission, uiPermission } from 'angular-permission';
import { mainController } from './js/controllers/main.controller.js';
import { passengerController } from './js/controllers/passenger.controller.js';
import { driverController } from './js/controllers/driver.controller.js';
import { historyController } from './js/controllers/history.controller.js';
import { slackLoginController } from './js/controllers/slackLogin.controller.js';
import { addCarController } from './js/controllers/addCar.controller.js';
import { errorController } from './js/controllers/error.controller.js';
import { loginErrorController } from './js/controllers/loginError.controller.js';
import { adminRidesController } from './js/controllers/adminRides.controller.js';
import { adminUsersController } from './js/controllers/adminUsers.controller.js';
import { adminPoiController } from './js/controllers/adminPOI.controller.js';
import { userProfileController } from './js/controllers/userProfile.controller.js';

//var = function scope
//const and let = block scope 

const app = angular.module('app', ['ui.router', permission, uiPermission, 'angular-jwt']);

app.config(function($stateProvider, $urlRouterProvider, $httpProvider, jwtOptionsProvider){
	
	jwtOptionsProvider.config({
		authPrefix: '',  
		tokenGetter: [
			() => localStorage.getItem('RideShare_auth_token')
		],
		whiteListedDomains: ['maps.googleapis.com']
	});

	$httpProvider.interceptors.push('jwtInterceptor');


	$urlRouterProvider.otherwise('/slackLogin');

	$stateProvider
		.state('main', {
			url: '/main',
			templateUrl: 'partials/main.html',
			controller: mainController
		})
	
		.state('slackLogin', {
			url: '/slackLogin',
			templateUrl: 'partials/slackLogin.html',
			controller: slackLoginController
		})
	
		.state('main.passenger',{
			url: '/passenger',
			templateUrl : 'partials/passenger.html',
			controller : passengerController
		})
	
		.state('main.driver',{
			url: '/driver',
			templateUrl : 'partials/driver.html',
			controller : driverController
		})
	
		.state('main.addCar' ,{
			url: '/addCar',
			templateUrl : 'partials/addCar.html',
			controller : addCarController
		})
	
		.state('main.adminRides' , {
			url: '/adminRides', 
			templateUrl : 'partials/adminRides.html',
			controller : adminRidesController
		})
		
		.state('main.adminUsers', {
			url: '/adminUsers',
			templateUrl: 'partials/adminUsers.html',
			controller : adminUsersController
		})
		
		.state('main.adminPoi',{
			url: '/adminPoi',
			templateUrl : 'partials/poi.html',
			controller : adminPoiController
		})
    
		.state('main.userProfile', {
				url: '/userProfile',
				templateUrl : 'partials/userProfile.html',
				controller : userProfileController 
		})

		.state('main.error', {
			url: '/error',
			templateUrl: 'partials/error.html',
			controller: errorController
		})
});
