export let userProfileController = function ($scope, $http, $state){
    $scope.allpois = {}; 
    $scope.user = {}; 
    $scope.car = {};
        
    //retrieve all pois
    $http.get("/poiController")
    .then(function (response){
        $scope.allpois = response.data; 
    });
    
    // retrieve the user's current car
	$http.get("/car/myCar", $scope.car)
		.then((response) => {
			$scope.car = response.data; 
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
				$state.go("main.userProfile");
			},
			(failedResponse) => {
				alert('failure');
			})
	}

}