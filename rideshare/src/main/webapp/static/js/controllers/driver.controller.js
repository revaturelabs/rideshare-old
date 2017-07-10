export let driverController = function($scope, $http, $state){

	// scope and function used to pass ride data to front end

	$scope.rides = {};

	$http.get("/ride")
	.then(function(response) {
		$scope.rides = response.data;
	});

	$scope.openRides = {};

	$http.get("/ride/request/active")
	.then(function(response){
		$scope.openRides = response.data;
	});

	$scope.pastRides = {};

	$http.get("/ride/request/history")
	.then(function(response){
		$scope.pastRides = response.data;
	});



};