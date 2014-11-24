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
        deferred.resolve(true);
        if (response.data.successful) {
          $rootScope.loggedin = true;
          vm.loginFailed = false;
          var userdata = {
            session: response.data.session,
            name: response.data.username,
            email: response.data.email
          }
          $rootScope.userdata = userdata;
          $window.localStorage.setItem('n3twork', JSON.stringify(userdata));
          $location.path('/');
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
      $rootScope.loggedin = false;
      $location.path('/login');
    }

}
})();
