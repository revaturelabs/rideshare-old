export let userProfileController = function ($scope, $http, $state){
	$scope.allpois = {}; 
	$scope.user = {}; 
	$scope.car = {};
	$scope.buttonText = '';
	//retrieve all pois
	$http.get("/poiController")
	.then(function (response){
		$scope.allpois = response.data; 
	});
	
	// retrieve the user's current car
	$http.get("/car/myCar", $scope.car)
		.then((response) => {
			$scope.car = response.data;
			
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
			    alert('Failure');
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