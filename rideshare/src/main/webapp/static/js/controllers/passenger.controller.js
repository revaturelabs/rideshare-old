export let passengerController = function($scope, $http, $state, $location){

	// TODO: move this to a main controller for a
	$scope.logout = function() {
		// view that is the parent of all the main views

		$http.post('/logout', {})
		.then(function() {
			$location.path("/");
		})
		.catch(function(data) {
			console.log("Logout failed");
			self.authenticated = false;
		});
	};

	$scope.getActiveRequests = function() {
		$http.get('/ride/request/active')
		.then((res) => {
			console.log(res);
		})
	}
};
