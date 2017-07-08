export let landingSampleController = function($scope, $http, $state){
  $scope.user = {};
  $scope.addUser = function(){
    $http.post('sample', $scope.user).then(
        (formResponse)=>{
          $state.go('success');
        }, 
        (failedResponse)=>{
          alert('Failure');
        }
    )
  }
};