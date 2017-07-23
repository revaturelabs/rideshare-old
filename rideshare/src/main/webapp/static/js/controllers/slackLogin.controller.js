export let slackLoginController = function($scope, $http, $state) {
	$http.get('/auth/process')
		.then(function(res) {
			let token = res.headers('rideshare-token')
			console.log(token);
			localStorage.setItem('RideShare_auth_token', token);
			$state.go('main.passenger');
		});

	// $scope.checkAuth = function() {
	// 	$http.get("/auth/check")
	// 	.then(function(res) {
	// 		console.log(res);
	// 		if (res.data !== null && res.data === true) {
	// 			$scope.authenticated = true;
	// 			$state.go('main.passenger');
	// 		} else {
	// 			$scope.authenticated = false;
	// 		}
	// 	})
	// 	.catch(function() {
	// 		$scope.authenticated = false;
	// 	});
	// }
	// $scope.checkAuth();
	


	// $http.get('/auth/token')
	// .then(function(res) {
	// 	// put res (token) in localstorage
	// 	localStorage.setItem('RideShare_auth_token', res.headers('rideshare-token'));
	// 	if ($rootScope.isAuthenticated) {
	// 		$state.go('main.passenger');
	// 	}
	// });
}
