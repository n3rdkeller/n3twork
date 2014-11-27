(function() {
  'use strict';

  angular
    .module('n3twork')
    .service('UserSvc', UserSvc);

  UserSvc.$inject = ['APISvc', '$window', '$rootScope', '$location'];

  function UserSvc(APISvc, $window, $rootScope, $location) {
    var userdata = {};

    var service = {
      isLoggedIn: isLoggedIn,
      getUserData: getUserData,
      setUserData: setUserData,
      logout: logout,
      localLogout: localLogout
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
        if (parseddata.firstname) { userdata.firstname = parseddata.firstname };
        if (parseddata.lastname) { userdata.lastname = parseddata.lastname };

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

    function setUserData() {
      if ($rootScope.userdata) {
        $window.localStorage.setItem('n3twork', JSON.stringify($rootScope.userdata));
        return true;
      } else {
        return false;
      }
    }


    function logout() {
      APISvc.request({
        method: 'POST',
        url: '/logout',
        data: {}
      })
      .then(function(response) {
        console.log('Successfully logged out.');
      });
      localLogout();
    }

    function localLogout() {
      $window.localStorage.removeItem('n3twork');
      $rootScope.loggedin = false;
      $location.path('/login');
    }

  }
})();
