//var = function scope
//const and let = block scope 

const app = angular.module('app', ['ui.router']);


app.config(function($stateProvider, $urlRouterProvider){
	
	$urlRouterProvider.otherwise('/home');
	
	$stateProvider
	
		.state('home',{
			url: '/home',
			templateUrl : 'partials/landingsample.html',
			controller : function($scope, $http, $state){
				$scope.user = {};
				$scope.addUser = function(){
					$http.post('sample', $scope.user).then(
							(formResponse)=>{
								$state.go('success');
								
							}, 
							(failedResponse)=>{
								alert('Failure');
							}
					)
				}
			}
		}	)
		.state('success',{
			url: '/success',
			templateUrl: 'partials/successSample.html',
			controller  : function($scope, $http){
			}
		})
		
		
	
	
	
})