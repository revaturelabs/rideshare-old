export let mainController = function($scope, $http, $state, $location, authManager, authFactory){
	// view that is the parent of all the main views
	$scope.isAdmin = authFactory.isAdmin();

	$scope.logout = function() {
		authFactory.clearToken();
		$http.post('/logout', {}).then((res) => { authManager.unauthenticate(); });
	};

	$scope.authTest = function() {
		$http.get('/admin/users')
			.then((res) => {
				console.log(res);
				console.log(res.data);
			})
	}

}
