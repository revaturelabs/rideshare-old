export let authService = function($http, $window) {
	this.getToken = function() {
		let token = $window.localStorage.getItem('RideShare_auth_token');
		if (angular.isDefined(token)) {
			return token;
		} else {

		}
	};
	this.getUser = function() {
		let user = $window.localStorage.getItem('RideShare_auth_user');
		if (angular.isDefined(user)) {
			return user;
		} else {
			console.log('no user information available.')
		}
	};
}