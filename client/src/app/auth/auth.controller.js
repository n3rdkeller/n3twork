(function() {
  'use strict';

  angular
    .module('n3twork.auth')
    .controller('AuthCtrl', AuthCtrl);

  function AuthCtrl() {
    var vm = this;

    login();

    function login(username, email, password) {
      if (!username) {};
      return;
    }
  }
})();
