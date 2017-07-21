export let driverController = function($scope, $http, $state){
	// scope and function used to pass ride data to front end
	$scope.isArray = angular.isArray;
	$scope.rides = {};
	
	$http.get("/ride")
	.then(function(response) {
		$scope.rides = response.data;
		
		$http.get("/user/me")
		.then(function(response) {
			if(response.data.mainPOI != null) {
				$scope.selectedItem = $scope.allPoi[response.data.mainPOI.poiId-1];
			}
			else {
				$scope.selectedItem = $scope.allPoi[0];
			}
		});
	});

	// Setting mPOI in case a user does not have a mPOI.
	$scope.poiId = {id : 1};

	// Setting to empty arrays for correct ng-repeat processing.
	$scope.openRequest = [];
	$scope.activeRides = [];
	$scope.pastRides = [];

	$scope.updateSort = function (item){
		$http.get("/ride/request/open/"+item.poiId)
		.then(function(response) {
			$scope.openRequest = response.data;	
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
			setTimeout(function(){$state.reload();}, 500);
		});

	}


	function compare(a,b) {
		if (a.availRide.availRideId < b.availRide.availRideId)
			return -1;
		if (a.availRide.availRideId > b.availRide.availRideId)
			return 1;
		return 0;
	}

	$http.get("/ride/offer/active")
	.then(function(res){
		$http.get("/ride/offer/open")
		.then(function(response){
			$scope.activeOffers = response.data;
			organizeData(res, "active");
			});
		});

	$http.get("/ride/offer/history")
	.then(function(response){
		organizeData(response, "history");
		});

	// scope provides structure of object needed to crreate an offer
	$scope.offer = {car : {}, pickupPOI : {}, dropoffPOI : {}, seatsAvailable:0, time:"", notes:"",open: true};


	// method to add offer through http post
	$scope.addOffer = function(pickup,dropoff,notes,time,seats) {

		$scope.offer.car = $scope.car;
		$scope.offer.pickupPOI = pickup;
		$scope.offer.dropoffPOI = dropoff;

		if(notes == undefined || notes == "") {
			notes = "N/A";
		}

		$scope.offer.notes = notes;
		$scope.offer.time = new Date(time);
		$scope.offer.seatsAvailable = seats;

		$http.post('/ride/offer/add', $scope.offer).then(
			(formResponse) => {
				setTimeout(function(){$state.reload();}, 500);
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
					
					setTimeout(function(){$state.reload();}, 500);
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
		$scope.allPoi = response.data;
	});
	
	/*
	 * Organizes Ride list data by combining RideRequests with matching AvailableRide objects.
	 */
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
			
			for(let i = 0; i < $scope.activeOffers.length; i++) {
				for(let k = 0; k < $scope.activeRides.length; k++) {
					if($scope.activeOffers[i].availRideId == $scope.activeRides[k].availRide.availRideId){
						$scope.activeOffers.splice(i, 1);
						i--;
						break;
					}
				}
			}
		}
		else if (reqString == "history") {
			$scope.pastRides = listReq;
		}
	}
	
	//stops past dates from being selected in date/time picker
	$scope.startDateBeforeRender = function($dates) {
		  const todaySinceMidnight = new Date();
		    todaySinceMidnight.setUTCHours(0,0,0,0);
		    $dates.filter(function (date) {
		      return date.utcDateValue < todaySinceMidnight.getTime();
		    }).forEach(function (date) {
		      date.selectable = false;
		    });
		};
	
};
