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
      APISvc.request({
        method: 'POST',
        url: '/login',
        data: {
          'login': logindata,
          'password': password
        }
      })
      .then(function(response) {
        vm.loading = false;
        if (response.data.successful) {
          // set data to $rootScope
          var userdata = {
            session: response.data.session,
            name: response.data.username,
            email: response.data.email,
          }
          if (response.data.firstname) { userdata.firstname = response.data.firstname };
          if (response.data.lastname) { userdata.lastname = response.data.lastname };
          $rootScope.userdata = userdata;

          // set data to localstorage
          if (UserSvc.setUserData()) {
            $location.path('/');
            vm.loginFailed = false;
            $rootScope.loggedin = true;
          } else {
            vm.loginFailed = true;
          }
          vm.loading = false;
        } else {
          vm.loading = false;
          vm.loginFailed = true;
        }
      });
      return;
    }

    function logout() {
      UserSvc.logout();
    }
  }

})();
