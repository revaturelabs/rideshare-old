export let adminUsersController = function($scope, $http, $state) {
	$scope.getUsers = function() {
		$http.get('admin/users')
		.then((res) => {
			console.log(res);
		})
	}
}