(function() {
  'use strict';

  angular
    .module('n3twork')
    .service('UserSvc', UserSvc);

  UserSvc.$inject = ['APISvc', '$window', '$rootScope', '$location', '$q'];

  function UserSvc(APISvc, $window, $rootScope, $location, $q) {
    var userdata = {};

    var service = {
      isLoggedIn: isLoggedIn,
      getUserData: getUserData,
      setUserData: setUserData,
      login: login,
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
      if (parseddata.session && parseddata.username) {
        var userdata = {
          session: parseddata.session,
          username: parseddata.username,
          email: parseddata.email,
          emailhash: parseddata.emailhash,
          id: parseddata.id,
          otherProperties: parseddata.otherProperties
        }
        if (parseddata.firstName) { userdata.firstName = parseddata.firstName };
        if (parseddata.lastName) { userdata.lastName = parseddata.lastName };

        // TODO: check session id at serverside (maybe check later)
        //        when actually doing a request
        $rootScope.loggedin = true;
        $rootScope.userdata = userdata;
        return true;
      } else {
        $rootScope.loggedin = false;
        return false;
      }
    }

    function setUserData(userdata) {
      if (userdata) {
        $rootScope.userdata = userdata;
        $rootScope.loggedin = true;
        $window.localStorage.setItem('n3twork', JSON.stringify(userdata));
        return true;
      } else {
        return false;
      }
    }

    function login(logindata, password) {
      var deferred = $q.defer();
      APISvc.request({
        method: 'POST',
        url: '/login',
        data: {
          'login': logindata,
          'password': password
        }
      }).then(function (response) {
        if (response.data.successful) {
          // set userdata from response
          var userdata = {
            session: response.data.session,
            username: response.data.username,
            email: response.data.email,
            emailhash: response.data.emailhash,
            id: response.data.id,
            otherProperties: response.data.otherProperties
          }
          if (response.data.firstName) { userdata.firstName = response.data.firstName };
          if (response.data.lastName) { userdata.lastName = response.data.lastName };

          // set data rootScope and localstorage
          if (setUserData(userdata)) {
            $rootScope.loggedin = true;
          }
          deferred.resolve(response.data.successful);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function logout() {
      APISvc.request({
        method: 'POST',
        url: '/logout',
        data: {}
      })
      .then(function (response) {
        if (response.data.successful) {
          console.log('Successfully logged out.');
        } else {
          console.log('Error logging out at server.')
        }
        localLogout();
      });
    }

    function localLogout() {
      $window.localStorage.removeItem('n3twork');
      $rootScope.loggedin = false;
      $location.path('/login');
    }

  }
})();
