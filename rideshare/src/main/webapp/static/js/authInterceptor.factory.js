export let authInterceptor = function($q, $window) {
	return {
		response: function(res) {
			console.log(res);
			let rideshareToken = res.headers('rideshare-token');
			if (rideshareToken) {
				$window.localStorage.setItem('RideShare_auth_token', rideshareToken);
			}
		}//,
		// responseError: function(rejection) {
		// 	console.log('you got an error')
		// }
	};
}