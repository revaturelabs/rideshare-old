import { landingSampleController } from './js/controllers/landingSample.controller.js';

//var = function scope
//const and let = block scope 

const app = angular.module('app', ['ui.router']);


app.config(function($stateProvider, $urlRouterProvider){
	
	$urlRouterProvider.otherwise('/home');
	
	$stateProvider
	
		.state('home',{
			url: '/home',
			templateUrl : 'partials/landingsample.html',
			controller : landingSampleController
		}	)
		.state('success',{
			url: '/success',
			templateUrl: 'partials/successSample.html',
			controller: function($scope, $http){
			}
		})
		
		
	
	
	
})