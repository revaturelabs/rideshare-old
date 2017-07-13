export let poiController = function ($scope, $http, $state) {
    $scope.poi = {};
    $scope.allpois = [];

    // retrieve the poiType objects
    $scope.types = {};
    $http.get("/poiController/type")
        .then(function (response) {
            $scope.types = response.data;
        })  // end of poiType retrieval 

    // addPoi function()
    $scope.addPoi = function () {
        // must first get lat/lng THEN sumbit data to backend
        // prevent undefined response 
        if ($scope.poi.addressLine2 === undefined)
            $scope.poi.addressLine2 = "";
        // get address and format it for google maps response
        var address = "" + $scope.poi.addressLine1 + " "
            + $scope.poi.addressLine2 + ", " +
            $scope.poi.city +
            ", " + $scope.poi.state;
        address = address.replace(/\s/g, '+'); // replace white space with +
    
        // store url to retrieve response from google maps api 
        var url = "https://maps.googleapis.com/maps/api/geocode/" +
            "json?address=" + address +
            "&key=AIzaSyB_mhIdxsdRfwiAHVm8qPufCklQ0iMOt6A";

        // extract latitude and longitude with google maps api
        $http.get(url)
            .then(function (response) {
                $scope.result = response;
                $scope.poi.latitude = $scope.result.data.results[0].geometry.location.lat;
                $scope.poi.longitude = $scope.result.data.results[0].geometry.location.lng;
            })
            .then(function () {
                // send poi to backend
                $http.post("/poiController/addPoi", $scope.poi)
                    .then((formResponse) => {
                        $state.go('poi');
                        document.getElementById("addPoi-form").reset();
                    },
                    (failedResponse) => {
                        alert('failure');
                    })
            });
    }   // end of addPoi() function

    // removePoi() 
    $scope.removePoi = function () {
        console.log($scope.poi);
    }   // end of removePoi() function

    console.log("sanity check #" + 19);  // sanity cache check 

    // retrieve all pois
    $scope.getPois = function () {
        $http.get("/poiController")
            .then(function (response) {
                console.log("getting poicontroller" + response.data);
                $scope.allpois = response.data;
            });
    }   // end of getPois () function 
};

