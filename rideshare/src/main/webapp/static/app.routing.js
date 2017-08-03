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

export let routing = function($stateRegistry, accessRights) {
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

	if (accessRights === 'ADMIN') {
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
		$stateRegistry.register({
			name: 'main.userProfile',
			url: '/userProfile',
			template: require('./partials/userProfile.html'),
			controller: userProfileController,
			data: { requiresLogin: true }
		});
	} else if (accessRights === 'USER') {
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
	}
};

export let logoutRedirector = function($stateRegistry, $state) {
	$state.go('login');
	$stateRegistry.deregister('main');
};