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

    vm.submit = submit;

    vm.user = {
      username: "",
      email: "",
      password: "",
    };


    function submit(isValid) {
      if (isValid) {
        vm.loading = true;
        APISvc.request({
          method: 'POST',
          url: '/register',
          data: {
            'username': vm.user.username,
            'email': vm.user.mail,
            'password': vm.user.pw
          }
        })
        .then(function(response) {
          vm.loading = false;
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
