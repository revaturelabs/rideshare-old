export let authInterceptor = function(authFactory) {
	return {
		'response': function(res) {
			let identityToken = res.headers('Set-RideshareIdentityToken');
			if (angular.isDefined(identityToken) && identityToken !== null && identityToken !== 'null') {
				authFactory.setToken(identityToken);
			}
			return res;
		}
	};
}