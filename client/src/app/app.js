(function() {
  'use strict';
  angular
    .module('n3twork', [
      'ngRoute',
      'ui.bootstrap',
      'ui.bootstrap.showErrors',
      'n3twork.register',
      'n3twork.auth',
      'n3twork.main'
    ])
    .config(config);

  function config($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'app/mainpage/main.html',
        controller: 'MainCtrl',
        resolve: authResolver
      })
      .when('/register', {
        templateUrl: 'app/register/register.html',
        controller: 'RegisterController',
      })
      .when('/login', {
        redirectTo: '/register',
      })
      .otherwise({
        redirectTo: '/',
        resolve: authResolver
      });
  }

  /**
   * determines if a user is authenticated
   */
  var authResolver = {
    auth: ['$q', '$rootScope', 'UserSvc', function($q, $rootScope, UserSvc){

      var deferred = $q.defer();
      var loggedIn = UserSvc.isLoggedIn();

      if(loggedIn){
        deferred.resolve(loggedIn);
      }else{
        deferred.reject({authenticated: false, redirectTo: '/register'});
      }

      return deferred.promise;
    }]
  };

})();

(function() {
  'use strict';

  /**
   * Eventhandler for route change events
   * success -> normal direction to intended route
   * error   -> several strategies depending on the eventObj
   *            see '$routeChangeError'
   */
  angular
  .module('n3twork')
  .run(['$rootScope', '$location', routeChange]);


  function routeChange($rootScope, $location) {
    $rootScope.$on('$routeChangeSuccess', function(loggedIn) {
      // ...
    });

    $rootScope.$on('$routeChangeError', function(event, current, previous, eventObj) {
      if (eventObj.authenticated === false) {
        $location.path('/register');
      }

      if (eventObj.redirectTo) {
        // console.log('redirecting to: ' + eventObj.redirectTo);
        $location.path(eventObj.redirectTo);
      }
    });
  }

})();
