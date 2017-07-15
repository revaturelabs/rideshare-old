export let adminUsersController = function($scope, $http, $state) {
	$scope.users ={};
    $scope.user = {}; 
    
	$scope.getUsers = function() {
		$http.get('admin/users')
		.then((res) => {
			console.log(res);
			$scope.users = res.data;
		})
	}
    
    $scope.openModal = function(index){
        $scope.user = $scope.users[index];
    }
    
    $scope.changeAdmin = function(){
        if ($scope.isAdmin === undefined)
            $scope.isAdmin = false; 
        
        var url = "/admin/updateStatus/" + $scope.user.userId + "/" + $scope.isAdmin; 
        
        $http.post(url, $scope.user)
        .then((formResponse) => {
            $state.reload('main.adminUsers');
        },
        (failedResponse) => {
            alert('failure'); 
        })
    }
}