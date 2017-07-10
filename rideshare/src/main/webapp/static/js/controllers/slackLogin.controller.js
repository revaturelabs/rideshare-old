export let slackLoginController = function($scope, $http, $state) {
  $http.get("/auth/current")
    .then(function(res) {
      $scope.authenticated = true;
    })
    .catch(function() {
      $scope.user = "N/A";
      $scope.authenticated = false;
    });
  $scope.logout = function() {
    $http.post('/logout', {})
      .then(function() {
        $scope.authenticated = false;
        $location.path("/");
      })
      .catch(function(data) {
        console.log("Logout failed")
        $scope.authenticated = false;
      });
  };
}