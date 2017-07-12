export let poiController = function ($scope, $http, $state) {
    $scope.poi = {};

    // addPoi function()
    $scope.addPoi = function () {
        console.log($scope.poi); //for debugging later

        if($scope.poi.addressLine2 === undefined)
            $scope.poi.addressLine2 = "";
        // get address from form and format it for google maps response
        var address = "" + $scope.poi.addressLine1 + " "
            + $scope.poi.addressLine2 + ", " +
            $scope.poi.city +
            ", " + $scope.poi.state;
        // console.log(type of address);
        address = address.replace(/\s/g, '+'); // replace white space with +
        // console.log("address: " + address); // debugging 

        // retrieve the latitude and longitude from the form response
        var url = "https://maps.googleapis.com/maps/api/geocode/" +
            "json?address=" + address +
            "&key=AIzaSyB_mhIdxsdRfwiAHVm8qPufCklQ0iMOt6A";
        // console.log("url " + url); // debugging

        var jsonresponse = []; 

        $http.get(url)
            .then(function (response) {
                console.log("sanity check #" + 2);

                $scope.result = response; 
                $scope.poi.latitude = $scope.result.data.results[0].geometry.location.lat;
                $scope.poi.longitude = $scope.result.data.results[0].geometry.location.lng;
            });

        // add poi
        $http.post("/poiController/addPoi", $scope.poi)
            .then((formResponse) => {
                $state.go('poi');
            },
            (failedResponse) => {
                alert('failure');
            })
    }   // end of addPoi function()
};

