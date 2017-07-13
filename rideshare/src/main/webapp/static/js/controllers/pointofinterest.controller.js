export let poiController = function ($scope, $http, $state) {
    $scope.poi = {};

    // Used to bypass Same Origin Policy
    $scope.createCORSRequest = function(method, url)
    {
        var xhr = new XMLHttpRequest();

        if ("withCredentials" in xhr) 
        {
            // XHR for Chrome/Firefox/Opera/Safari.
            xhr.open(method, url, true);
        } 
        else if (typeof XDomainRequest != "undefined") 
        {
            // XDomainRequest for IE.
            xhr = new XDomainRequest();
            xhr.open(method, url);
        } 
        else 
        {
            // CORS not supported.
            xhr = null;
        }

        return xhr;
    }
    
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

        var xhr = $scope.createCORSRequest('GET', url);
        
        xhr.onload = function(response) {
        	var result = JSON.parse(xhr.responseText);
            $scope.poi.latitude = result.results[0].geometry.location.lat;
            $scope.poi.longitude = result.results[0].geometry.location.lng;
            
            // add poi
            $http.post("/poiController/addPoi", $scope.poi)
                .then((formResponse) => {
                    $state.go('poi');
                },
                (failedResponse) => {
                    alert('failure');
                })
        }
        xhr.send();
    }   // end of addPoi function()
};

