export let userProfileController = function ($scope, $http, $state){
	$scope.allpois = {}; 
	$scope.user = {}; 
	$scope.car = {};
	$scope.originalCar = {};
	$scope.buttonText = '';
	$scope.mainPoiOption;
	$scope.workPoiOption;
	
	// Retrieve user's information
	$http.get("user/me", $scope.user)
		.then((response) => {
			// Possible issue: We may not want all of a user's information
			// floating around on the client side. Should some information
			// be withheld in Java User controller?
			$scope.user = response.data;
			
			return $scope.getPois($scope, $http);
		}),
		(failedResponse) => {
			alert('Failed to get user\'s information');
		}
	
	//retrieve all pois
	$scope.getPois = function($scope, $http) {
		$http.get("/poiController")
		.then(function (response){
			$scope.allpois = response.data;
		})
		// Set 
		.then(function() {
			for(var i = 0; i < $scope.allpois.length; i++) {
				if($scope.user.mainPOI.poiId == $scope.allpois[i].poiId) {
					$scope.mainPoiOption = $scope.allpois[i];
				}
				if($scope.user.workPOI.poiId == $scope.allpois[i].poiId) {
					$scope.workPoiOption = $scope.allpois[i];
				}
			}
		});
	}
	
	// retrieve the user's current car
	$http.get("/car/myCar", $scope.car)
		.then((response) => {
			$scope.car = response.data;
			$scope.originalCar = response.data;
			console.log('car:')
			console.log($scope.car);
			console.log('original car:')
			console.log($scope.originalCar);
			if($scope.car === '') {
				$scope.buttonText = 'Add Car';
			}
			else {
				$scope.buttonText = 'Edit Car';
			}
		},
		(failedResponse) => {
			alert('failure'); 
		})
	
	// how get poi from selected option 
	// set the pois to the user 
	$scope.setPois = function(){  
		console.log('User main:')
		console.log($scope.user.mainPoi);
		console.log('Main option:')
		console.log($scope.mainPoiOption);
		$scope.user.mainPoi = $scope.mainPoiOption;
		$scope.user.workPoi = $scope.workPoiOption;
		
		$http.post("/user/updateCurrentUser", $scope.user)
		.then((formResponse) => {
			$state.go('main.userProfile');
		},
		(failedResponse) => {
			alert('failure');
		})
	}
	
	$scope.addCar = function() {
		$http.post('/car', $scope.car).then(
			(formResponse) => {
				$scope.buttonText = 'Edit Car';
			    $state.go('main.userProfile');
			},
			(failedResponse) => {
			    alert('Failure in addCar');
			}
		)
	}
	
	$scope.beforeUpdateCar = function() {
		console.log('before update --- car: ')
		console.log($scope.car);
		console.log('before update --- original car')
		console.log($scope.orignalCar);
		$scope.updateCar($scope);
	}
	
	$scope.updateCar = function($scope) {
		$http.post('/car/updateCar', $scope.car, $scope.originalCar).then(
				(formResponse) => {
					console.log($scope.car);
					console.log($scope.originalCar);
				    $state.go('main.userProfile');
				},
				(failedResponse) => {
				    alert('Failure in updateCar');
				}
			)
	}
	
	$scope.removeCar = function(){
		$http.post("/car/removeCar", $scope.car)
			.then((response) => {
				$scope.buttonText = 'Add Car';
				$state.go("main.userProfile");
			},
			(failedResponse) => {
				alert('failure');
			})
	}

}