export let adminUsersController = function($scope, $http, $state) {
	$scope.users;
	$scope.getUsers = function() {
		$http.get('admin/users')
		.then((res) => {
			console.log(res);
			$scope.users = res.data;
		})
	}
}