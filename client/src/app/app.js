(function() {
  'use strict';
  angular
    .module('n3twork', [
      'ngRoute',
      'ui.bootstrap',
      'ui.bootstrap.showErrors',
      'n3twork.register',
      'n3twork.auth',
      'n3twork.profile',
      'n3twork.settings',
      'n3twork.search'
    ])
    .config(config);

  function config($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'app/profile/profile.html',
        controller: 'ProfileCtrl',
        controllerAs: 'profile',
        resolve: authResolver

        // TODO: Here comes News Feed
      })
      .when('/register', {
        templateUrl: 'app/register/register.html',
        controller: 'RegisterCtrl',
        controllerAs: 'register'
      })
      .when('/login', {
        redirectTo: '/register'
      })
      .when('/settings', {
        templateUrl: 'app/settings/settings.html',
        controller: 'SettingsCtrl',
        controllerAs: 'settings',
        resolve: authResolver
      })
      .when('/search', {
        templateUrl: 'app/search/search.html',
        controller: 'SearchCtrl',
        controllerAs: 'search',
        resolve: authResolver
      })
      .when('/user/:username', {
        templateUrl: 'app/profile/profile.html',
        controller: 'ProfileCtrl',
        controllerAs: 'profile',
        resolve: authResolver
      })
      .otherwise({
        redirectTo: '/'
      });
  }

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
        $location.path(eventObj.redirectTo);
      }
    });
  }

})();


(function() {
  'use strict';

  angular
    .module('n3twork')
    .run(['$rootScope', '$location', NavCtrl]);

  function NavCtrl($rootScope, $location) {
    $rootScope.isActive = isActive;

    function isActive (viewLocation) {
      return viewLocation === $location.path();
    }
  }

})();

