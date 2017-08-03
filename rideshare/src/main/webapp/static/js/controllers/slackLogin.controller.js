export let slackLoginController = function($scope, $rootScope, /* $http, */ $state /* , $log, authFactory */) {
	if ($rootScope.isAuthenticated) {
		$state.go('main.passenger');
	}

	// $http.get('/auth/check')
	// 	.then(function(res) {
	// 		if (res.data) {
	// 			$http.get('/auth/process')
	// 				.then(function(res) {
	// 					let token = res.headers('rideshare-token');
	// 					// localStorage.setItem('RideShare_auth_token', token);
	// 					authFactory.setToken(token);
	// 					$state.go('main.passenger');
	// 				})
	// 				.catch(function(reason) {
	// 					$log.error(reason);
	// 					authFactory.clearToken();
	// 				});
	// 		}
	// 	});
}
