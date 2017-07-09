export let addCarController = function($scope, $http, $state){
	$scope.car = {};
	$scope.addCar = function() {
		console.log($scope.car);
		beforeSend: function(xhr) {
			xhr.setRequestHeader("X-CSRF-TOKEN", token);
		}
		$http.post('/car', $scope.car).then(
			(formResponse) => {
				$state.go('success');
			},
			(failedResponse) => {
				alert('Failure');
			}
		)
	}
};
