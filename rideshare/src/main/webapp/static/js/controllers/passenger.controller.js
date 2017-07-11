export let passengerController = function($scope, $http, $state, $location){
	$scope.test = 'passenger home';
	$scope.getPrincipal = function() {
		$http.get('auth/current').then((res) => { console.log(res); });
	};
	$scope.logout = function() { // TODO: move this to a main controller for a view that is the parent of all the main views
    $http.post('/logout', {})
			.then(function() {
				$location.path("/");
			})
			.catch(function(data) {
      	console.log("Logout failed");
      	self.authenticated = false;
			});
  };
};