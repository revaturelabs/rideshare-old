export let errorController = function($scope, $http, $state, $timeout) {
	$scope.banned = $state.params['reason'] === 'ban';
	$scope.nopage = $state.params['reason'] === 'nopage';
	$scope.message;
	if ($scope.banned) {
		$scope.message = "You have been banned from this application!"
	} else if ($scope.nopage) {
		$scope.message = "No pages by that name here!";
	}	else {
		$scope.message = "You broke the internet!";
	}

	if (!$scope.banned) {
		$timeout(() => {
			$state.go('slackLogin');
		}, 6000);
	}
}