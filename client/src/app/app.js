(function() {
  'use strict';
  angular
    .module('n3twork', [
      'ngRoute',
      'ui.bootstrap.showErrors',
      'n3twork.register',
      'n3twork.auth',
      'n3twork.main'
    ])
    .config(config);

  function config($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'app/register/register.html',
        controller: 'RegisterController'
      })
      .when('/main', {
        templateUrl: 'app/mainpage/main.html',
        controller: 'MainCtrl'
      })
      // .when('/login', {
      //   templateUrl: 'app/auth/auth.login.html',
      //   controller: 'AuthCtrl'
      // })
      .otherwise({
        redirectTo: '/'
      });
  }
})();
