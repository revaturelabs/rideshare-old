export let adminRidesController = function($scope, $http, $state) {
	$scope.activeRides = {};
    $scope.rideHistory = {};
    $scope.ride = {}; 
    
    
	$scope.getActiveRides = function() {
		$http.get('admin/activeRides')
		.then((res) => {
			console.log(res);
			$scope.activeRides = res.data;
		})
	}
	
	$scope.getRideHistory = function() {
		$http.get('admin/rideHistory')
		.then((res) => {
			console.log(res);
			$scope.rideHistory = res.data;
		})
	}
    
    $scope.openARModal = function(index){
        $scope.ride = $scope.activeRides[index];
    }
    
    $scope.openRHModal = function(index){
        $scope.ride = $scope.rideHistory[index];
    }
    
    console.log("sanity check #" + 3); 
}