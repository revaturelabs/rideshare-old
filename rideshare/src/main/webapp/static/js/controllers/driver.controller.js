export let driverController = function($scope, $http, $state){

	// scope and function used to pass ride data to front end

	$scope.rides = {};

	$http.get("/ride")
	.then(function(response) {
		$scope.rides = response.data;
	});
	
	//changes poi that is used in the openRequest
	//TODO: get default scope from user
	$scope.poiId = {id : 0};
	
	$scope.updateSort = function (){
		
		$scope.poiId.id = $scope.selectedItem.poiId;
		console.log($scope.poiId.id);
		
		$http.get("/ride/request/open/"+$scope.poiId.id)
		.then(function(response) {
			$scope.openRequest = response.data;
		});
		
	}
	
	//show open requests from a poi
	$scope.openRequest = {};

	$http.get("/ride/request/open/"+$scope.poiId.id)
	.then(function(response) {
		$scope.openRequest = response.data;
	});
	
	//shows all open (unconfirmed) offers for a user
	$scope.openRides = {};

	$http.get("/ride/offer/open/")
	.then(function(response) {
		$scope.openRides = response.data;
	});

	// get data that shows all active ride offers for user
	$scope.activeRides = {};

	$http.get("/ride/offer/active")
	.then(function(response){
		$scope.activeRides = response.data;
	});

	// get data that shows all past ride offers for user
	$scope.pastRides = {};

	$http.get("/ride/offer/history")
	.then(function(response){
		$scope.pastRides = response.data;
	});


	// scope provides structure of object needed to crreate an offer
	$scope.offer = {car : {}, pickupPOI : {}, dropoffPOI : {}, seatsAvailable:0, time:"", notes:"",open: true};
	console.log($scope.offer);


	// method to add offer through http post
	$scope.addOffer = function(pickup,dropoff,notes,time,seats) {

		$scope.offer.car = $scope.car;
		$scope.offer.pickupPOI = pickup;
		$scope.offer.dropoffPOI = dropoff;
		$scope.offer.notes = notes;
		$scope.offer.time = new Date(time);
		$scope.offer.seatsAvailable = seats;
		console.log($scope.offer);

		$http.post('/ride/offer/add', $scope.offer).then(
				(formResponse) => {
					$state.go('driver');
				},
				(failedResponse) => {
					alert('Failure');
				}
		)
	};

	// get all info needed to make a new offer
	$scope.car = {};

	$http.get("/car/myCar")
	.then(function(response){
		$scope.car = response.data;
		console.log($scope.car);
	});


	$scope.allPoi = {};

	$http.get("/poiController")
	.then(function(response){
		$scope.allPoi = response.data;
		console.log($scope.allPoi);
	});

};
