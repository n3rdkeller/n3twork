(function() {
  'use strict';
  angular
    .module('n3twork', [
      'ngRoute',
      'n3twork.register',
      'n3twork.auth'
    ])
    .config(config);

  function config($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'app/register/register.html',
        controller: 'RegisterController'
      })
      .otherwise({
        redirectTo: '/'
      });
  }
})();
