(function() {
  'use strict';

  angular
    .module('n3twork.auth')
    .controller('AuthCtrl', AuthCtrl);

  AuthCtrl.$inject = ['APISvc', '$q', '$window', '$rootScope'];

  function AuthCtrl(APISvc, $q, $window, $rootScope) {
    var vm = this;
    var deferred = $q.defer();

    vm.loggedin = false;
    vm.loginButtonClicked = false;
    vm.loginFailed = false;

    if ($window.localStorage.getItem('authdata')) {
      var parsedauthdata = JSON.parse($window.localStorage.getItem('authdata'));
      if (parsedauthdata.session && parsedauthdata.username) {
        vm.session = parsedauthdata.session;
        vm.username = parsedauthdata.username;
        vm.loggedin = true;
        vm.loginFailed = false;
        $rootScope.loggedin = true;
        vm.loginButtonClicked = true;
      } else {
        vm.loginFailed = false;
        vm.loggedin = false;
        $rootScope.loggedin = false;
      }
    }

    vm.submit = function() {
      login(vm.user.login, vm.user.pw);
    }

    function login(logindata, password) {
      vm.loginButtonClicked = true;
      APISvc.request({
        method: 'POST',
        url: '/login',
        data: {
          'login': logindata,
          'password': password
        }
      })
      .then(function(response) {
        deferred.resolve(true);
        var session = response.data.session;
        var user = response.data.username;
        if (user) {
          vm.username = user;
          if (session) {
            vm.loggedin = true;
            $rootScope.loggedin = true;
            vm.loginFailed = false;
            var authdata = {
              session: session,
              username: user
            }
            $window.localStorage.setItem('authdata', JSON.stringify(authdata));
          }
        } else {
          vm.loginFailed = true;
        }
      });
      return;
    }

    vm.logout = function() {
      $window.localStorage.removeItem('authdata');
      vm.loggedin = false;
      $rootScope.loggedin = false;
      vm.loginButtonClicked = false;
    }

}
})();
