import { angularJwt } from 'angular-jwt';
import { permission, uiPermission } from 'angular-permission';
import { passengerController } from './js/controllers/passenger.controller.js';
import { driverController } from './js/controllers/driver.controller.js';
import { historyController } from './js/controllers/history.controller.js';
import { slackLoginController } from './js/controllers/slackLogin.controller.js';
import { addCarController } from './js/controllers/addCar.controller.js';
import { adminRidesController } from './js/controllers/adminRides.controller.js';
import { adminUsersController } from './js/controllers/adminUsers.controller.js';
import { poiController } from './js/controllers/pointofinterest.controller.js';

//var = function scope
//const and let = block scope 


const app = angular.module('app', ['ui.router', permission, uiPermission, 'angular-jwt']);

app.config(function($stateProvider, $urlRouterProvider, $httpProvider, jwtOptionsProvider){
	
	jwtOptionsProvider.config({
	      tokenGetter: [function() {
	        return localStorage.getItem('RideShare_auth_token');
	      }],
	      whiteListedDomains: ['maps.googleapis.com']
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
			controller : passengerController
		})
		
		.state('driver',{
			url: '/driver',
			templateUrl : 'partials/driver.html',
			controller : driverController
		})
		
		.state('history',{
			url: '/history',
			templateUrl : 'partials/history.html',
			controller : historyController
		})

		.state('addCar' ,{
			url: '/addCar',
			templateUrl : 'partials/addCar.html',
			controller : addCarController
		})
		
		.state('adminRides' , {
			url: '/adminRides', 
			templateUrl : 'partials/adminRides.html',
			controller : adminRidesController
		})
		
		.state('adminUsers', {
			url: '/adminUsers',
			templateUrl: 'partials/adminUsers.html',
			controller : adminUsersController
		})
	
		.state('poi',{
			url: '/poi',
			templateUrl : 'partials/poi.html',
			controller : poiController
		})
	
});
s