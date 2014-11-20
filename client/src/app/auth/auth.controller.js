(function() {
  'use strict';

  angular
    .module('n3twork.auth')
    .controller('AuthCtrl', AuthCtrl);

  AuthCtrl.$inject = ['APISvc', 'UserSvc', '$q', '$window', '$location', '$rootScope'];

  function AuthCtrl(APISvc, UserSvc, $q, $window, $location, $rootScope) {
    var vm = this;
    var deferred = $q.defer();

    // methods
    vm.submit = submit;
    vm.logout = logout;

    vm.loggedin = false;
    $rootScope.loggedin = false;
    vm.loginButtonClicked = false;
    vm.loginFailed = false;

    UserSvc.isLoggedIn();

    function submit() {
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
          $rootScope.username = user;
          if (session) {
            vm.loggedin = true;
            $rootScope.loggedin = true;
            $rootScope.session = session;
            vm.loginFailed = false;
            var authdata = {
              session: session,
              username: user
            }
            $window.localStorage.setItem('n3twork', JSON.stringify(authdata));
            $location.path('/');
          }
        } else {
          $location.path('/login');
          vm.loginFailed = true;
        }
      });
      return;
    }

    function logout() {
      // TODO: API-Request
      $window.localStorage.removeItem('n3twork');
      vm.loggedin = false;
      $rootScope.loggedin = false;
      $location.path('/login');
      vm.loginButtonClicked = false;
    }

}
})();
