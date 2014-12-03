(function() {
  'use strict';

  angular
    .module('n3twork.auth', []);
})();

(function() {
  'use strict';

  angular
    .module('n3twork.auth')
    .controller('AuthCtrl', AuthCtrl);

  AuthCtrl.$inject = ['UserSvc', '$location'];

  function AuthCtrl(UserSvc, $location) {
    var vm = this;

    // methods
    vm.login = login;
    vm.logout = logout;

    UserSvc.isLoggedIn();

    function login() {
      vm.loginFailed = false;
      vm.loadingLogin = true;
      UserSvc.login(vm.user.login, vm.user.pw).then(function (successful) {
        vm.loadingLogin = false;
        if (successful) {
          vm.loginFailed = false;
          $location.path('/');
        } else {
          vm.loginFailed = true;
        }
      }, function (error) {
        vm.loadingLogin = false;
        vm.loginFailed = true;
      });
    }

    function logout() {
      UserSvc.logout();
    }
  }

})();
