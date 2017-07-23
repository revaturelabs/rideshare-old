export let slackLoginController = function($scope, $http, $state) {
	$http.get('/auth/process')
		.then(function(res) {
			let token = res.headers('rideshare-token');
			localStorage.setItem('RideShare_auth_token', token);
			$state.go('main.passenger');
		});
}
