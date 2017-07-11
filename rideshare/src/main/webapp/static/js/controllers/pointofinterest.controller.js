export let poiController = function ($scope, $http, $state) {
    $scope.poi = {};

    // addPoi function()
    $scope.addPoi = function () {
        console.log($scope.poi); //for debugging later

        // get address from form and format it for google maps response
        var address = "" + $scope.poiAddressLine1 +
            $scope.poiAddressLine2 + ", " + $scope.poiCity +
            ", " + $scope.poiState;
        address.replace(/\s+/g, '+'); //replace white space with +

        // retrieve the latitude and longitude from the form response
        var url = "https://maps.googleapis.com/maps/api/geocode/" +
            "json?address=" + address +
            "&key=AIzaSyB_mhIdxsdRfwiAHVm8qPufCklQ0iMOt6A";
        
        $http.get(url)
            .then(function (response) {
                $scope.latitude = response.results[0].geometry.location.lat;
                $scope.longitude = response.results[0].geometry.location.lng;
            });

        // add poi
        $http.post("/poi", $scope.poi)
            .then((formResponse) => {
                $state.go('poi');   
            },
            (failedResponse) => {
                alert('failure');
            })
    }   // end of addPoi function()
};

