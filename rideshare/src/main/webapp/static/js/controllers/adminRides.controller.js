export let adminRidesController = function($scope, $http, $state) {
	$scope.getActiveRides = function() {
		$http.get('admin/activeRides')
		.then((res) => {
			console.log(res);
		})
	}
	
	$scope.getRideHistory = function() {
		$scope.rideHistory;
		$http.get('admin/rideHistory')
		.then((res) => {
			console.log(res);
			$scope.rideHistory = res.data;
		})
	}
}