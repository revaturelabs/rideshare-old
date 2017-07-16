export let driverController = function($scope, $http, $state){
	// scope and function used to pass ride data to front end
	$scope.isArray = angular.isArray;
	$scope.rides = {};
	
	$http.get("/ride")
	.then(function(response) {
		$scope.rides = response.data;
	});

	// changes poi that is used in the openRequest
	// TODO: get default scope from user

	$scope.poiId = {id : 1};

	$scope.openRequest = [];

	$scope.updateSort = function (item){
		console.log(item.poiId);
		$http.get("/ride/request/open/"+item.poiId)
		.then(function(response) {
			$scope.openRequest = response.data;
			console.log($scope.openRequest);
			console.log(new Date ($scope.openRequest[0].time).getTime());
		});

	}

	// show open requests from a poi
	$http.get("/ride/request/open/"+$scope.poiId.id)
	.then(function(response) {
		$scope.openRequest = response.data;
	});

	// accept open requests
	$scope.acceptReq = function(id){


		$http.get("/ride/request/accept/"+id)
		.then(function(response) {

		});

		$state.reload();
	}


	// shows all open (unconfirmed) offers for a user
	$scope.openRides = {};

	$http.get("/ride/offer/open/"+$scope.poiId.id)
	.then(function(response) {
		$scope.openRides = response.data;
	});

	// get data that shows all active ride offers for user
	$scope.activeRides = [];

	function compare(a,b) {
		if (a.availRide.availRideId < b.availRide.availRideId)
			return -1;
		if (a.availRide.availRideId > b.availRide.availRideId)
			return 1;
		return 0;
	}

	$http.get("/ride/offer/active")
	.then(function(response){organizeData(response, "active")});

	// get data that shows all past ride offers for user
	$scope.pastRides = {};

	$http.get("/ride/offer/history")
	.then(function(response){organizeData(response, "history")});


	// scope provides structure of object needed to crreate an offer
	$scope.offer = {car : {}, pickupPOI : {}, dropoffPOI : {}, seatsAvailable:0, time:"", notes:"",open: true};


	// method to add offer through http post
	$scope.addOffer = function(pickup,dropoff,notes,time,seats) {

		$scope.offer.car = $scope.car;
		$scope.offer.pickupPOI = pickup;
		$scope.offer.dropoffPOI = dropoff;
		$scope.offer.notes = notes;
		$scope.offer.time = new Date(time);
		$scope.offer.seatsAvailable = seats;

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
		$http.get('/ride/offer/cancel/' + activeRideId).then(
				(response) => {
					for(let i = 0; i < $scope.activeRides.length; i++){
						if($scope.activeRides[i].availRide.availRideId == activeRideId) {
							$scope.activeRides.splice(i, 1);
							$scope.$apply;
						}
					}
				}
		);
	};


	// get all info needed to make a new offer
	$scope.car = {};

	$http.get("/car/myCar")
	.then(function(response){
		$scope.car = response.data;
	});


	$scope.allPoi = {};

	$http.get("/poiController")
	.then(function(response){
		console.log(response.data);
		$scope.allPoi = response.data;
	});

	
	function organizeData(response, reqString){
		if(response.data.length == 0){
			let temp = [];
			$scope.activeRides = temp;
			return;
		}
		let list = response.data;
		let listReq = [];
		let temp = [];
		let counter = 0;
		let currentAvailId = list[0].availRide.availRideId;
		list.sort(compare); 
		listReq = [list[0]];
		for(let i = 0; i < list.length; i++){

			if((currentAvailId != list[i].availRide.availRideId) && i == list.length-1){
				listReq[counter++].request = temp;
				temp = [];
				temp.push(list[i].request);
				listReq[counter] = list[i];
				listReq[counter].request = temp;
			}
			else if ((currentAvailId == list[i].availRide.availRideId) && i == list.length-1){
				temp.push(list[i].request);
				listReq[counter].request = temp;
			}
			else if((currentAvailId != list[i].availRide.availRideId)){
				currentAvailId = list[i].availRide.availRideId;

				if(temp.length > 0){
					listReq[counter++].request = temp;
					listReq[counter] = list[i];
					temp = [];
				}
			} 
			if(i != list.length-1) temp.push(list[i].request);
		}
		if(reqString == "active") {
			$scope.activeRides = listReq;
		}
		else if (reqString == "history") {
			$scope.pastRides = listReq;
		}
	}
	
};
