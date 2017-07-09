export let passengerController = function($scope, $http, $state){
	$scope.test = 'passenger home';
	$scope.getPrincipal = function() {
		$http.get('auth').then((res) => { console.log() });
	};
	
};