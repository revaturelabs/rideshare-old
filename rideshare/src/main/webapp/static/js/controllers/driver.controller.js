export let driverController = function($scope, $http, $state){

	// scope and function used to pass ride data to front end

	$scope.rides = {};

	$http.get("/ride")
	.then(function(response) {
		$scope.rides = response.data;
	});

	
	//get data that shows all active ride offers for user
	$scope.activeRides = {};

	$http.get("/ride/offer/active")
	.then(function(response){
		$scope.activeRides = response.data;
	});

	$scope.pastRides = {};

	//get data that shows all past ride offers for user
	$http.get("/ride/offer/history")
	.then(function(response){
		$scope.pastRides = response.data;
	});



};