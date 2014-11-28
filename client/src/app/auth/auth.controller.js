(function() {
  'use strict';

  angular
    .module('n3twork.auth')
    .controller('AuthCtrl', AuthCtrl);

  AuthCtrl.$inject = ['APISvc', 'UserSvc', '$window', '$location', '$rootScope'];

  function AuthCtrl(APISvc, UserSvc, $window, $location, $rootScope) {
    var vm = this;

    // methods
    vm.submit = submit;
    vm.logout = logout;

    $rootScope.loggedin = false;
    vm.loginFailed = false;

    UserSvc.isLoggedIn();

    function submit() {
      login(vm.user.login, vm.user.pw);
    }

    function login(logindata, password) {
      vm.loginFailed = false;
      vm.loading = true;
      UserSvc.login(logindata, password).then(function (response) {
        vm.loading = false;
        if (response.data.successful) {
          vm.loginFailed = false;
        } else {
          vm.loginFailed = true;
        }
        }, function (error) {
          vm.loading = false;
          vm.loginFailed = true;
      });
    }

    function logout() {
      UserSvc.logout();
    }
  }

})();
