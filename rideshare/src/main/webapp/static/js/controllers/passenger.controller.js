export let passengerController = function($scope, $http, $state, $location){
	$scope.test = 'passenger home';

	$scope.getPrincipal = function() {
		$http.get('auth/current').then((res) => { console.log(res); });
	};

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

	$scope.getToken = function() {
		$http.get('auth/token')
			.then((res) => {
				console.log(res);
				console.log(res.headers('token'));
			})
			.catch((res) => {
				console.log(res);
			})
	}

	$scope.getActiveRequests = function() {
		$http.get('/ride/request/active')
		.then((res) => {
			console.log(res.data);
		})
	}

	$scope.getRequestHistory = function() {
		$http.get('/ride/request/history')
		.then((res) => {
			console.log(res.data);
		})
	}
};
