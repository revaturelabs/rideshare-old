import { routing, logoutRedirector } from './app.routing.js';

export let configure = function($stateRegistryProvider, $urlServiceProvider, jwtOptionsProvider,
		$httpProvider, $cookiesProvider) {
	console.log('in configure');
	
	jwtOptionsProvider.config({
		// loginPath: '/#/login',
		unauthenticatedRedirector: ['$stateRegistry', '$state', logoutRedirector],
		authPrefix: '',
		authHeader: 'X-JWT-RIDESHARE',
		tokenGetter: ['authFactory', function(authFactory) {
			return authFactory.getToken();
		}],
		whiteListedDomains: ['maps.googleapis.com']
	});

	$httpProvider.interceptors.push('jwtInterceptor');
};

export let setup = function($stateRegistry, $http, $log, $state, $urlService, authManager, authFactory) {
	console.log('in setup');

	authManager.checkAuthOnRefresh();
	authManager.redirectWhenUnauthenticated();

	if (authManager.isAuthenticated()) {
		routing($stateRegistry, authFactory.isAdmin() ? 'ADMIN' : 'USER');
		$urlService.rules.initial({ state: 'main.passenger' });
	} else {
		routing($stateRegistry, null);
		$urlService.rules.initial({ state: 'slackLogin' });
		$http.get('/auth/check')
			.then(function(res) {
				$log.info(res);
				$log.info(res.data === false);
				if (res.data) {
					$log.info('processing authentication to obtain identity token')
					$http.get('/auth/process')
						.then((res) => {
							let token = res.headers('rideshare-identity-token');
							authFactory.setToken(token);
						})
						.catch((reason) => { $log.error(reason); });
				} else {

				}
			});
	}
};