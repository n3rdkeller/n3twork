(function() {
  'use strict';

  angular
    .module('n3twork.register')
    .controller('RegisterController', RegisterController);

  RegisterController.$inject = ['APISvc', '$q', '$rootScope'];

  function RegisterController(APISvc, $q, $rootScope) {
    var vm = this;
    var deferred = $q.defer();

    vm.loggedin = $rootScope.loggedin;
    vm.submitted = false;
    vm.signedup = false;

    vm.user = {
      username: "",
      email: "",
      password: "",
    };


    vm.submit = function(isValid) {
      if (isValid) {
        APISvc.request({
          method: 'POST',
          url: '/register',
          data: {
            'username': vm.user.username,
            'email': vm.user.mail,
            'password': APISvc.hashpw(vm.user)
          }
        })
        .then(function(response) {
          deferred.resolve(true);
          if (response.data.successful) {
            vm.submitted = true;
            vm.signedup = true;
            vm.message = "Congratz! Successfully signed up. You can now login.";
          } else {
            vm.submitted = true;
            vm.signedup = false;
            vm.message = "Error signing up. Try again using another username.";
          }
        });
      } else {
        console.log("There are still invalid fields.");
      }
    };

  };
})();
