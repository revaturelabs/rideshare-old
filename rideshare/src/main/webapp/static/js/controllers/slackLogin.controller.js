export let slackLoginController = function($scope, $state, authManager ) {
	if (authManager.isAuthenticated()) {
		$state.go('main.passenger');
	}
}
