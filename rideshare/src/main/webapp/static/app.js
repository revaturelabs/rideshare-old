import { permission, uiPermission } from 'angular-permission';
import { passengerController } from './js/controllers/passenger.controller.js';
import { driverController } from './js/controllers/driver.controller.js';
import { historyController } from './js/controllers/history.controller.js';
import { slackLoginController } from './js/controllers/slackLogin.controller.js';

//var = function scope
//const and let = block scope 


const app = angular.module('app', ['ui.router', permission, uiPermission]);

app.config(function($stateProvider, $urlRouterProvider){
	
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
		
	
	
});
