(function() {
  'use strict';

  angular
    .module('n3twork')
    .service('UserSvc', UserSvc);

  UserSvc.$inject = ['$window', '$rootScope'];

  function UserSvc($window, $rootScope) {
    var userdata = {};

    var service = {
      isLoggedIn: isLoggedIn,
      getUserData: getUserData
    };
    return service;

    function isLoggedIn() {
      if ($window.localStorage.getItem('n3twork')) {
        return getUserData(JSON.parse($window.localStorage.getItem('n3twork')));
      } else {
        return false;
      }
    }

    function getUserData(parseddata) {
      if (parseddata.session && parseddata.name) {
        var userdata = {
          session: parseddata.session,
          name: parseddata.name,
          email: parseddata.email
        }
        // TODO: check session id at serverside (maybe check later)
        //        when actually doing a request
        userdata.loggedin = true;
        $rootScope.loggedin = true;
        $rootScope.userdata = userdata;
        return true;
      } else {
        $rootScope.loggedin = false;
        return false;
      }
    }
  }
})();
