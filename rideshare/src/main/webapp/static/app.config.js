import { routing, logoutRedirector } from './app.routing.js';

export let configure = function($stateRegistryProvider, $urlServiceProvider, jwtOptionsProvider,
		$httpProvider, $cookiesProvider) {
	jwtOptionsProvider.config({
		// loginPath: '/#/login',
		unauthenticatedRedirector: ['$stateRegistry', '$state', logoutRedirector],
		authPrefix: '',
		authHeader: 'X-JWT-RIDESHARE-USER',
		tokenGetter: ['authFactory', function(authFactory) {
			return authFactory.getToken();
		}],
		whiteListedDomains: ['maps.googleapis.com']
	});

	$httpProvider.interceptors.push('jwtInterceptor');
	$httpProvider.interceptors.push('authInterceptor');
};

export let setup = function($stateRegistry, $http, $log, $state, $urlService, authManager, authFactory) {
	let token = authFactory.getToken();
	if (angular.isUndefined(token) || token === null || token === 'null') {
		$log.info('no token found, checking authentication');
		$http.get('/auth/check')
			.then((res) => {
				if (res.data) {
					$log.info('authentication confirmed, obtaining identity token');
					$http.get('/auth/identity')
						.then((res) => {
							authFactory.setToken(res.data);
							if (!authFactory.isBanned()) {
								authManager.authenticate();
							}
						})
						.catch((reason) => {
							$log.error('Failed to get identity token of authenticated user. ' + reason);
						});
				}
			})
			.then(() => { routing($stateRegistry, $urlService, true, authFactory.isBanned(), authFactory.isAdmin()); });
	} else {
		$log.info('token found, configuring routes');
		routing($stateRegistry, $urlService, false, authFactory.isBanned(), authFactory.isAdmin());
	}

	// authManager.checkAuthOnRefresh();
	// authManager.redirectWhenUnauthenticated();
};