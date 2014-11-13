(function() {
  'use strict';

  angular
    .module('n3twork.auth')
    .controller('AuthCtrl', AuthCtrl);

  AuthCtrl.$inject = ['APISvc', '$q'];

  function AuthCtrl(APISvc, $q) {
    var vm = this;
    var deferred = $q.defer();


    login('dieter', '', 'hi');

    function login(username, email, password) {
      APISvc.request({
        method: 'POST',
        url: '/login',
        data: {
          'username': username,
          'email': email,
          'password': password
        }
      })
      .then(function(response) {
        deferred.resolve(true);
        vm.data = response.data;
      });
      return;
    }
  }
})();
