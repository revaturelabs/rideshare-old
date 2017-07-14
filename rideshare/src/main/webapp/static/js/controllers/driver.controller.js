export let driverController = function($scope, $http, $state){

	// scope and function used to pass ride data to front end

	$scope.rides = {};

	$http.get("/ride")
	.then(function(response) {
		$scope.rides = response.data;
	});


	// get data that shows all active ride offers for user
	$scope.activeRides = {};

	function compare(a,b) {
		if (a.availRide.availRideId < b.availRide.availRideId)
			return -1;
		if (a.availRide.availRideId > b.availRide.availRideId)
			return 1;
		return 0;
	}
	
	$http.get("/ride/offer/active")
	.then(function(response){
		
		let list = response.data;
		let listReq = [];
		let temp = [];
		let counter = 0;
		let currentAvailId = list[0].availRide.availRideId;
		list.sort(compare); 
		listReq = [list[0]];
		
		for(let i = 0; i < list.length; i++){
			if((currentAvailId != list[i].availRide.availRideId) ||  i == list.length-1){
				currentAvailId = list[i].availRide.availRideId;
				
				if(temp.length > 0){
					listReq[counter++].request = temp;
					listReq[counter] = list[i];
					temp = [];
				}
				if(i == list.length-1){
					//temp.length = 1;
					//listReq[counter].request = temp;
				}
			} 
			temp.push(list[i].request);
		}

		
		$scope.activeRides = listReq;
		console.log($scope.activeRides);
	});

	// get data that shows all past ride offers for user
	$scope.pastRides = {};

	$http.get("/ride/offer/history")
	.then(function(response){
		console.log("ride/offer/history...");
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
					$state.go('main.driver');
				},
				(failedResponse) => {
					alert('Failure');
				}
		)
	};

	$scope.offerCancel = function(activeRideId) {
		console.log(activeRideId);
		$http.get('/ride/offer/cancel/' + activeRideId).then(
				(response) => {
					console.log(response.data);
					$state.go('main.driver');

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
