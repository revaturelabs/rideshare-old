export let userProfileController = function ($scope, $http, $state){
    $scope.allpois = {}; 
    $scope.user = {}; 
    
    console.log("hi"); 
    
    //retrieve all pois
    $http.get("/poiController")
    .then(function (response){
        $scope.allpois = response.data; 
    });
    
    // how get poi from selected option 
    // set the pois to the user 
    $scope.setPois = function(){
        console.log("set poi called " + $scope.user.workPOI + " " + $scope.user.mainPOI); 
        
        $http.post("/user/updateCurrentUser", $scope.user)
        .then((formResponse) => {
            $state.go('main.userProfile');
        },
        (failedResponse) => {
            alert('failure');
        })
    }
    console.log("check " + 5); 
}