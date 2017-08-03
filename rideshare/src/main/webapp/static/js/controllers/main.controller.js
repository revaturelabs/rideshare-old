export let mainController = function($scope, $http, $state, $location, authManager, authFactory){
	// view that is the parent of all the main views
	$scope.isAdmin = authFactory.isAdmin();

	$scope.logout = function() {
		authFactory.clearToken();
		authFactory.clearSessionId();
		$http.post('/logout', {}).then((res) => { authManager.unauthenticate(); });
	};

}
