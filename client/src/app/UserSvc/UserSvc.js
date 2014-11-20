(function() {
    'use strict';

    angular
        .module('n3twork')
        .service('UserSvc', UserSvc);

    UserSvc.$inject = ['$window', '$rootScope'];

    function UserSvc($window, $rootScope) {
        var authdata = {};

        var service = {
            isLoggedIn: isLoggedIn,
            authdata: authdata
        };
        return service;

        function isLoggedIn() {
          return checkLocalStorage();
        }

        // already data in localstorage?
        function checkLocalStorage() {
          if ($window.localStorage.getItem('n3twork')) {
            var parsedauthdata = JSON.parse($window.localStorage.getItem('n3twork'));
            if (parsedauthdata.session && parsedauthdata.username) {
              authdata.session = parsedauthdata.session;
              authdata.username = parsedauthdata.username;
              // TODO: check session id at serverside (maybe check later)
              //        when actually doing a request
              authdata.loggedin = true;
              $rootScope.loggedin = true;
              $rootScope.authdata = authdata;
              return true;
            } else {
              $rootScope.loggedin = false;
              return false;
            }
          }
        }

    }
})();
