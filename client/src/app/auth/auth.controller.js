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

    if ($window.localStorage.getItem('authdata')) {
      var parsedauthdata = JSON.parse($window.localStorage.getItem('authdata'));
      if (parsedauthdata.session && parsedauthdata.username) {
        vm.session = parsedauthdata.session;
        vm.username = parsedauthdata.username;
        vm.loggedin = true;
        $rootScope.loggedin = true;
      } else {
        vm.loggedin = false;
        $rootScope.loggedin = false;
      }
    }

    vm.submit = function() {
      login(vm.user.login, vm.user.pw);
    }

    function login(logindata, password) {
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
        vm.username = user;
        if (session) {
          vm.loggedin = true;
          $rootScope.loggedin = true;
          var authdata = {
            session: session,
            username: user
          }
          console.log(authdata);
          $window.localStorage.setItem('authdata', JSON.stringify(authdata));
        }
      });
      return;
    }

    vm.logout = function() {
      console.log('Logging now out.');
      $window.localStorage.removeItem('authdata');
      vm.loggedin = false;
      $rootScope.loggedin = false;
    }

}
})();
