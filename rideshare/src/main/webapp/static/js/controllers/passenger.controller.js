export let passengerController = function($scope, $http, $state, $location){

	$scope.getActiveRequests = function() {
		$http.get('/ride/request/active')
		.then((res) => {
			console.log(res.data);
		})
	}

	$scope.getRequestHistory = function() {
		$http.get('/ride/request/history')
		.then((res) => {
			console.log(res.data);
		})
	}


	//global variables
	let user;
	let poiLimit = 0;

	$http.get("user/me").then(function(response){
		// get current user
		user = response.data;
		
		//gets user main POI then sets the starting point
		//drop down to the users main POI
		if(user.mainPOI == null){
			//sets the default drop down option to 1
			let userPOI = 'start1';
			$scope[userPOI] = true;
		}else{
			//sets the start drop down to the users main POI
			let userPOI = 'start'+user.mainPOI.poiId;
			$scope[userPOI] = true;
		}
	});

	$http.get('poiController').then(function(response){
		let allPOI = response.data;
		let userMainPOI;
		
		$scope.allMainPOI = allPOI;
		
		// check if the user main POI is null
		if(user.mainPOI == null){
			//if null set the default coordinates to 1st address in the database
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
		
		//create the label numbering
		let labels = [];
		for(let x = 1; x < response.data.length+1; x++){
			labels.push(x.toString());
		}
		
		// used to initialize the google map
		function initMap() {
			var map = new google.maps.Map(document.getElementById('map'), {
				zoom: 15,
				center: userMainPOI,
				disableDefaultUI: true
			});

			var infowindow = new google.maps.InfoWindow({
				content: "here"
			});

			// Add some markers to the map.
			// Note: The code uses the JavaScript Array.prototype.map() method
			// to create an array of markers based on a given "locations" array.
			// The map() method here has nothing to do with the Google Maps API.
			var markers = locations.map(function(location, i) {
				return new google.maps.Marker({
					position: location,
					label: labels[i]
				});
			});
			
			
			
			for(let x = 0; x < markers.length; x++){
				let id = x+1;
				
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
						
						let temp2 = 'destination' + id;
						$scope[temp2] = true;
						$scope.$apply();
						poiLimit = 2;
					}
					
					if(poiLimit === 0){
						markers[x].setIcon('http://earth.google.com/images/kml-icons/track-directional/track-8.png');
						
						let temp1 = 'start' + id;
						$scope[temp1] = true;
						$scope.$apply();
						poiLimit = 1;
					}
					
				}, false);

			}
			
			//remove push pins from map, by setting the markers to default
			$scope.clearMapMarkers = function() {
				poiLimit = 0;
				for(let x = 0; x < markers.length; x++){
					markers[x].setIcon();
				}
			};

			// Add a marker cluster to manage the markers.
			var markerCluster = new MarkerClusterer(map, markers,
					{imagePath: '../js/googleMapAPI/m'});

		}

		// initialize the google map
		initMap();
	});
};
