export let mainController = function($scope, $http, $state, $location){
	// view that is the parent of all the main views

	$scope.logout = function() {

		$http.post('/logout', {})
		.then(function() {
			$location.path("/");
		})
		.catch(function(data) {
			console.log("Logout failed");
			self.authenticated = false;
		});
	};

}
