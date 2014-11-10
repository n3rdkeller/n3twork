(function() {
  'use strict';

  angular
    .module('n3twork.register')
    .controller('RegisterController', RegisterController);

  function RegisterController() {
    var vm = this;

    vm.message = "";

    vm.user = {
      username: "",
      password: "",
      confirmPassword: ""
    };

    vm.submit = function(isValid) {
      console.log("h");
      if (isValid) {
        vm.message = "Submitted " + vm.user.username;
      } else {
        vm.message = "There are still invalid fields.";
      }
    };

  };
})();
