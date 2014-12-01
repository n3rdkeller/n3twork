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
    vm.submit = submit;
    vm.logout = logout;

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
        if (response.successful) {
          vm.loginFailed = false;
          $location.path('/');
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
