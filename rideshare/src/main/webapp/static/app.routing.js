import { mainController } from './js/controllers/main.controller.js';
import { passengerController } from './js/controllers/passenger.controller.js';
import { driverController } from './js/controllers/driver.controller.js';
import { historyController } from './js/controllers/history.controller.js';
import { slackLoginController } from './js/controllers/slackLogin.controller.js';
import { errorController } from './js/controllers/error.controller.js';
import { adminRidesController } from './js/controllers/adminRides.controller.js';
import { adminUsersController } from './js/controllers/adminUsers.controller.js';
import { adminPoiController } from './js/controllers/adminPOI.controller.js';
import { userProfileController } from './js/controllers/userProfile.controller.js';

export let routing = function($stateRegistry, $urlService, isAuthenticated, isBanned, isAdmin) {
	console.log('setting up routes')
	registerRoutes($stateRegistry, isAdmin);

	if (!isAuthenticated) {
		$urlService.rules.initial({ state: 'slackLogin' });
	} else if (isBanned) {
		$urlService.rules.initial({
			state: 'error',
			params: { reason: 'ban' }
		});
	} else if (isAdmin) {
		// the page an admin is redirected to could differ from the page normal users get sent to
		$urlService.rules.initial({ state: 'main.passenger' });
	} else {
		$urlService.rules.initial({ state: 'main.passenger' });
	}
	$urlService.rules.otherwise({
		state: 'error',
		params: { reason: 'nopage' }
	});
}

let registerRoutes = function($stateRegistry, isAdmin) {
	/* Register views that should be available before authentication */
	$stateRegistry.register({
		name: 'slackLogin',
		url: '/login',
		template: require('./partials/slackLogin.html'),
		controller: slackLoginController,
		data: { requiresLogin: false }
	});
	$stateRegistry.register({
		name: 'error',
		url: '/error',
		template: require('./partials/error.html'),
		controller: errorController,
		data: { requiresLogin: false }
	});

	/* Register views that should be available to all authenticated users */
	$stateRegistry.register({
		name: 'main',
		url: '/main',
		template: require('./partials/main.html'),
		controller: mainController,
		data: { requiresLogin: true }
	});
	$stateRegistry.register({
		name: 'main.passenger',
		url: '/passenger',
		template: require('./partials/passenger.html'),
		controller: passengerController,
		data: { requiresLogin: true }
	});
	$stateRegistry.register({
		name: 'main.driver',
		url: '/driver',
		template: require('./partials/driver.html'),
		controller: driverController,
		data: { requiresLogin: true }
	});
	$stateRegistry.register({
		name: 'main.userProfile',
		url: '/userProfile',
		template: require('./partials/userProfile.html'),
		controller: userProfileController,
		data: { requiresLogin: true }
	});

	if (isAdmin) {
		/* Register views that are exclusively for administrators */
		$stateRegistry.register({
			name: 'main.adminRides',
			url: '/adminRides', 
			template: require('./partials/adminRides.html'),
			controller: adminRidesController,
			data: { requiresLogin: true }
		});
		$stateRegistry.register({
			name: 'main.adminUsers',
			url: '/adminUsers',
			template: require('./partials/adminUsers.html'),
			controller: adminUsersController,
			data: { requiresLogin: true }
		});
		$stateRegistry.register({
			name: 'main.adminPoi',
			url: '/adminPoi',
			template: require('./partials/adminPOI.html'),
			controller: adminPoiController,
			data: { requiresLogin: true }
		});
	}
};

export let logoutRedirector = function($stateRegistry, $state) {
	$state.go('slackLogin');
	$stateRegistry.deregister('main');
};