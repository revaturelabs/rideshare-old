export let passengerController = function($scope, $http, $state, $location){

	// global variables
	let user;
	let poiLimit = 0;

	$http.get("user/me").then(function(response){
		// get current user
		user = response.data;
		
		// gets user main POI then sets the starting point
		// drop down to the users main POI
		if(user.mainPOI == null){
			// sets the default drop down option to 1
			let userPOI = 'start1';
			$scope[userPOI] = true;
		}else{
			// sets the start drop down to the users main POI
			let userPOI = 'start'+user.mainPOI.poiId;
			$scope[userPOI] = true;
		}
	})
	.then(function(){
		$http.get('poiController').then(function(response){
			let allPOI = response.data;
			let userMainPOI;
			$scope.allMainPOI = allPOI;
			
			// check if the user main POI is null
			if(user.mainPOI == null){
				// if null set the default coordinates to 1st address in the
				// database
				userMainPOI = {lat: allPOI[0].latitude, lng: allPOI[0].longitude};
			}else{
				// get the current user main POI
				userMainPOI = {lat: user.mainPOI.latitude, lng: user.mainPOI.longitude};
			}

			// create markers for all the current POI
			let locations = [];
			for(let x = 0; x < response.data.length; x++){
				let temp = {lat: allPOI[x].latitude, lng: allPOI[x].longitude};
				locations.push(temp);
			};
			
			// create the label numbering
			let labels = [];
			for(let x = 1; x < response.data.length+1; x++){
				labels.push(x.toString());
			}
			
			// used to initialize the google map
			function initMap() {
				let directionsDisplay = new google.maps.DirectionsRenderer();
				let directionsService = new google.maps.DirectionsService();
				
				var map = new google.maps.Map(document.getElementById('map'), {
					zoom: 15,
					center: userMainPOI,
					disableDefaultUI: true
				});

				var infowindow = new google.maps.InfoWindow({
					content: "here"
				});

				// Add some markers to the map.
				// Note: The code uses the JavaScript Array.prototype.map()
				// method to create an array of markers based on a given
				// "locations" array. The map() method here has nothing
				// to do with the Google Maps API.
				var markers = locations.map(function(location, i) {
					return new google.maps.Marker({
						position: location,
						label: labels[i]
					});
				});
				
				
				
				for(let x = 0; x < markers.length; x++){
					let id = x+1;
					document.getElementById("mapText").innerHTML = 'Choose Start Point';
					
					// add event listener to each marker on the map
					markers[x].addListener('click',function(){
						// set each ng-selected value to false
						for(let x = 0; x<markers.length; x++){
							let temp1 = 'start' + id;
							let temp2 = 'destination' + id;
							
							$scope[temp1] = false;
							$scope[temp2] = false;
						}
						
						if(poiLimit === 1){
							markers[x].setIcon('http://earth.google.com/images/kml-icons/track-directional/track-8.png');
							
							// Remove blue markers and text once route shown
							$scope.clearMapMarkers();
							document.getElementById("mapText").innerHTML = '';
							
							let temp2 = 'destination' + id;
							$scope[temp2] = true;
							$scope.$apply();
							poiLimit = 2;

							$scope.showDirections();
						}
						
						if(poiLimit === 0){
							markers[x].setIcon('http://earth.google.com/images/kml-icons/track-directional/track-8.png');
							document.getElementById("mapText").innerHTML = 'Choose Destination';
							
							let temp1 = 'start' + id;
							$scope[temp1] = true;
							$scope.$apply();
							poiLimit = 1;
						}
						
					}, false);

				}
				
				// remove push pins from map, by setting the markers to default
				$scope.clearMapMarkers = function() {
					poiLimit = 0;
					directionsDisplay.setMap(null);
					document.getElementById("mapText").innerHTML = 'Choose Start Point';
				
					for(let x = 0; x < markers.length; x++){
						markers[x].setIcon();
					}
				};
				
				//show the current route from start to destination
				$scope.showDirections = function() {
					
					//get the current drop down options id
					let select1 = document.getElementById("fromPOI");
					let start = select1.options[select1.selectedIndex].id;
					
					let select2 = document.getElementById("toPOI");
					let destination = select2.options[select2.selectedIndex].id;
					
					directionsDisplay.setMap(map);
					
					let request = {
							//get the longitude and latitude to match the selected poi
							origin: {lat: allPOI[start].latitude, lng: allPOI[start].longitude},
							destination: {lat: allPOI[destination].latitude, lng: allPOI[destination].longitude},
							travelMode: 'DRIVING'
					}
					
					//use google map api to show the current route
					directionsService.route(request, function(result, status){
						directionsDisplay.setDirections(result);
					});
				};

				// Add a marker cluster to manage the markers.
				var markerCluster = new MarkerClusterer(map, markers,
						{imagePath: '../js/googleMapAPI/m'});

			}

			// initialize the google map
			initMap();
		});
		
	});

	
	
	// scope and function used to pass ride data to front end
	$scope.isArray = angular.isArray;
	$scope.rides = {};
	
	
	
	
	$http.get("/ride")
	.then(function(response) {
		$scope.rides = response.data;
		return $http.get("/poiController");
	})
	.then(function(response) {
		$scope.allPoi = response.data;
		return $http.get("/user/me");
	})
	.then(function(response){
		if(response.data.mainPOI != null) {
			$scope.selectedItem = $scope.allPoi[response.data.mainPOI.poiId-1];
		} else {
			$scope.selectedItem = $scope.allPoi[0];
		}
	});
		

	// Setting mPOI in case a user does not have a mPOI.
	$scope.poiId = {id : 1};

	// Setting to empty arrays for correct ng-repeat processing.
	$scope.openOffer = [];
	$scope.activeRides = [];
	$scope.pastRides = [];

	$scope.updateSort = function (item){
		$http.get("/ride/offer/open/"+2)
		.then(function(response) {
			$scope.openOffer = response.data;
			console.log("-.-.-.-.-.-.-.-.-");
			console.log(response.data);
			console.log($scope.openOffer);
			console.log("-.-.-.-.-.-.-.-.-");
		});

	}

	$scope.updateSort(2);
	
	// show open requests from a poi
	$http.get("/ride/offer/open/"+$scope.poiId.id)
	.then(function(response) {
		$scope.openOffer = response.data;
		console.log("Populated open offers");
		console.log($scope.openOffer);
	});

	$http.get("/ride/request/active")
	.then(function(response){
		$scope.activeRides = response.data;
	});

	$http.get("/ride/request/history")
	.then(function(response){
		$scope.pastRides = response.data;
	});

	// accept open offers
	$scope.acceptOffer = function(id){
		console.log($scope.openOffer);
		$http.get("/ride/offer/accept/"+id)
		.then(function(response) {
//			for(let i = 0; i < $scope.openOffer.length; i++){
//				if($scope.openOffer[i].availRideId == id) {
//					$scope.openOffer.splice(i, 1);
//					$scope.$apply;
//				}
//			}
		});

		setTimeout(function(){$state.reload();}, 500);
	}

	$scope.cancelRide = function(rideId) {
		$http.get('/ride/request/cancelRide/' + rideId).then(
			(response) => {
				for(let i = 0; i < $scope.activeRides.length; i++){
					if($scope.activeRides[i].availRide.availRideId == rideId) {
						$scope.activeRides.splice(i, 1);
						$scope.$apply;
					}
				}
				setTimeout(function(){$state.reload();}, 500);
			}
		);
	};
	$scope.date = new Date().getTime();
	$scope.completeRide = function(rideId) {
		$http.post('/ride/request/complete/' + rideId).then((response) => {
			for(let i = 0; i < $scope.activeRides.length; i++){
				if($scope.activeRides[i].rideId == rideId) {
					$scope.activeRides.splice(i, 1);
					$scope.$apply;
				}
			}
			setTimeout(function(){$state.reload();}, 500);
		});
	};
	
	$scope.addRequest = function(pickup,dropoff,notes,time) {

		$scope.newRequest = {};

		let select1 = document.getElementById("fromPOI");
		let start = $scope.allMainPOI[select1.options[select1.selectedIndex].id];

		let select2 = document.getElementById("toPOI");
		let destination = $scope.allMainPOI[select2.options[select2.selectedIndex].id];

		$scope.newRequest.pickupLocation = start;
		$scope.newRequest.dropOffLocation = destination;

		if(notes == undefined || notes == "") {
			notes = "N/A";
		}

		$scope.newRequest.notes = notes;
		$scope.newRequest.time = new Date(time);
		$scope.newRequest.status = 'OPEN';
		$scope.newRequest.user = user;
		
		console.log($scope.newRequest);

		$http.post('/ride/request/add', $scope.newRequest).then(
			(formResponse) => {
				setTimeout(function(){$state.reload();}, 500);
			},
			(failedResponse) => {
				alert('Failure');
			}
		)
	};
	
	
	function compare(a,b) {
		if (a.request.requestId < b.request.requestId)
			return -1;
		if (a.request.requestId > b.request.requestId)
			return 1;
		return 0;
	}
	
	$http.get("/ride/request/active")
	.then(function(res){
		$http.get("/ride/request/open")
		.then(function(response){
			$scope.activeRequests = response.data;
			organizeData(res, "active");
			});
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
		let currentAvailId = list[0].request.requestId;
		list.sort(compare); 
		listReq = [list[0]];
		for(let i = 0; i < list.length; i++){
			console.log(list);
			if((currentAvailId != list[i].request.requestId) && i == list.length-1){
				listReq[counter++].request = temp;
				temp = [];
				temp.push(list[i].request);
				listReq[counter] = list[i];
				listReq[counter].request = temp;
				console.log("if 1");
				console.log(list);
				
			}
			else if ((currentAvailId == list[i].request.requestId) && i == list.length-1){
				temp.push(list[i].request);
				listReq[counter].request = temp;
				console.log("if 2");
				console.log(list);
			}
			else if((currentAvailId != list[i].request.requestId)){
				currentAvailId = list[i].request.requestId;
				console.log("if 3");
				console.log(list);
				if(temp.length > 0){
					listReq[counter++].request = temp;
					listReq[counter] = list[i];
					temp = [];
					console.log("if 4");
					console.log(list);
				}
			} 
			if(i != list.length-1) temp.push(list[i].request);
		}
		if(reqString == "active") {
			$scope.activeRides = listReq;
			
			console.log("-----------HELLO---------");
			console.log($scope.activeRides);
			console.log($scope.activeRequests);
			console.log("-----------HELLO---------");
			for(let i = 0; i < $scope.activeRequests.length; i++) {
				for(let k = 0; k < $scope.activeRides.length; k++) {
					if($scope.activeRequests[i].requestId == $scope.activeRides[k].request.requestId){
						$scope.activeRequests.splice(i, 1);
						i--;
						break;
					}
				}
			}
			
			console.log("-----------HE2222LLO---------");
			console.log($scope.activeRequests);
			console.log("-----------HE2222LLO---------");
		}
		else if (reqString == "history") {
			$scope.pastRides = listReq;
		}
	}
	
//	$scope.cancelRequest = function(activeReqId){
//		$http.get('/ride/request/cancel/' + activeReqId).then(
//				(response) => {
//					for(let i = 0; i < $scope.activeRequests.length; i++){
//						for(let j = 0; j < $scope.activeRequests[i].request.length; j++){
//							if($scope.activeRequests[i].request[j].requestId == activeReqId) {
//								console.log($scope.activeRequests[i].request);
//								$scope.activeRequests[i].request.splice(j, 1);
//								console.log($scope.activeRequests[i].request);
//								$scope.$apply;
//							}
//						}
//					}
//					
//					setTimeout(function(){$state.reload();}, 500);
//				}
//		);
//	}
	
	$scope.cancelActiveRequest = function(activeReqId){
		$http.get('/ride/request/cancelActive/' + activeReqId).then(
				(response) => {
					console.log(activeReqId);
					console.log($scope.activeRequests);
					for(let i = 0; i < $scope.activeRequests.length; i++){
						if($scope.activeRequests[i].requestId == activeReqId) {
							$scope.activeRequests[i].splice(i, 1);
							$scope.$apply;
						}
					}
					
					setTimeout(function(){console.log($scope.activeRequests[0].request.length + " " + "HERE");$state.reload();}, 500);
				}
		);
	}
};
