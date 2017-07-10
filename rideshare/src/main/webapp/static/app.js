import { passengerController } from './js/controllers/passenger.controller.js';
import { driverController } from './js/controllers/driver.controller.js';
import { historyController } from './js/controllers/history.controller.js';
import { mainController } from './js/controllers/main.controller.js';

//var = function scope
//const and let = block scope 


const app = angular.module('app', ['ui.router']);

app.config(function($stateProvider, $urlRouterProvider){
	
	$urlRouterProvider.otherwise('/passenger');
	
	$stateProvider
	
		// .state('login',{
		// 	url: '/login',
		// 	templateUrl : 'partials/slackLogin.html',
		// 	controller : slackLoginController
		// })

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
