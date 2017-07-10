export let driverController = function($scope, $http, $state){
	
	// scope and function used to pass ride data to front end
	
	$scope.rides = {};
	
	$http.get("/ride")
		.then(function(response) {
			$scope.rides = response.data;
		});
	
};