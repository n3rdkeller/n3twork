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
        method: 'GET',
        url: '/login',
        headers: {
          'Content-Type': 'application/json'
        },
        data: {
          'data': {
            'email': email,
            'username': username,
            'password': password
          }
        }
      })
      .then(function(response) {
        deferred.resolve(true);
        console.log(response);
        vm.data = response.data;
      });
      return;
    }
  }
})();
