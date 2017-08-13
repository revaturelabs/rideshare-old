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
};

export let setup = function($stateRegistry, $http, $log, $state, $urlService, authManager, authFactory) {
	console.log('setting up');

	// authFactory.clearToken();

	// authManager.checkAuthOnRefresh();
	// authManager.redirectWhenUnauthenticated();

	let token = authFactory.getToken();
	let sessionId = authFactory.getSessionId();
	console.log(token === null)
	console.log(sessionId)

	if (angular.isDefined(sessionId) && sessionId !== null && sessionId !== 'null') {
		console.log('Found JSESSIONID Cookie')
		if (!authManager.isAuthenticated()) {
			$http.get('/auth/identity')
				.then((res) => {
					authFactory.setToken(res.data);
					authManager.authenticate();
				})
				.catch((reason) => {
					$log.error('Failed to get identity of authenticated user. ' + reason);
				});
		}
		if (authFactory.isBanned()) {
			routing($stateRegistry, null);
			$urlService.rules.initial({
				state: 'error',
				params: { reason: 'ban' }
			});
		} else if (authFactory.isAdmin()) {
			routing($stateRegistry, 'ADMIN');
			$urlService.rules.initial({ state: 'main.passenger' });
		} else {
			routing($stateRegistry, 'USER');
			$urlService.rules.initial({ state: 'main.passenger' });
		}
	} else {
		console.log('No JSESSIONID Cookie')
		routing($stateRegistry, null);
		$urlService.rules.initial({ state: 'slackLogin' });
	}
	$urlService.rules.otherwise({
    state: 'error',
    params: { reason: 'nopage' }
  });
};