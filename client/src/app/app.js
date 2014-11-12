(function() {
  'use strict';
  angular
    .module('n3twork', [
      'ngRoute',
      'n3twork.register',
      'n3twork.auth',
      'n3twork.hw'
    ])
    .config(config);

  function config($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'app/register/register.html',
        controller: 'RegisterController'
      })
      .when('/hw', {
        templateUrl: 'app/main/main.html',
        controller: 'HwController',
        resolve: ['APISvc']
      })
      .otherwise({
        redirectTo: '/'
      });
  }
})();
