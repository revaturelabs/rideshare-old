export let authFactory = function($http, $window, jwtHelper) {
	return {
		getToken: function() {

		},
		requestToken: function() {

		},
		acquireToken: function() {

		},
		setToken: function(token) {

		},
		clearToken: function() {

		},
		getUser: function() {

		},
	};
}

export let AuthService = function($window) {
	this.getToken = function() {
		let token = $window.localStorage.getItem('RideShare_auth_token');
		if (angular.isDefined(token)) {
			return token;
		} else {
			return null;
		}
	};
	this.getUser = function() {
		let user = $window.localStorage.getItem('RideShare_auth_user');
		if (angular.isDefined(user)) {
			return user;
		} else {
			return null;
		}
	};
	this.setToken = function(token) {
		$window.localStorage.setItem('RideShare_auth_token', token);
	};
	this.clearToken = function() {
		$window.localStorage.removeItem('RideShare_auth_token');
	}
}