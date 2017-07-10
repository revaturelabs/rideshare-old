export let passengerController = function($scope, $http, $state){
	$scope.test = 'passenger home';
	$scope.getPrincipal = function() {
		$http.get('auth/current').then((res) => { console.log(res); });
	};
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
};