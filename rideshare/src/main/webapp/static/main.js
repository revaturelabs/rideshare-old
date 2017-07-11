import { permission, uiPermission } from 'angular-permission';
import { slackLoginController } from './js/controllers/slackLogin.controller.js';

const app = angular.module('app', ['ui.router', permission, uiPermission]);

app.controller('slackLoginController', slackLoginController);