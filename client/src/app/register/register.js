(function() {
  'use strict';

  angular
    .module('n3twork.register', ['ngMessages']);
})();

// controller
(function() {
  'use strict';

  angular
    .module('n3twork.register')
    .controller('RegisterCtrl', RegisterCtrl);

  RegisterCtrl.$inject = ['APISvc', 'UserSvc', '$rootScope', '$timeout', '$location'];

  function RegisterCtrl(APISvc, UserSvc, $rootScope, $timeout, $location) {
    var vm = this;

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
        .then(function (response) {
          vm.loading = false;
          if (response.data.successful) {
            vm.submitted = true;
            vm.signedup = true;
            vm.message = "Congratz! Successfully signed up. You will shortly be logged in.";
            $timeout(function () {
              UserSvc.login(vm.user.username, vm.user.pw).then(function (success) {
                if (success) {
                 $location.path('/');
                }
              })
            }, 3000);
          } else {
            vm.submitted = true;
            vm.signedup = false;
            vm.message = "Error signing up. Either your email or username is already in use.";
          }
        });
      } else {
        console.log("There are still invalid fields.");
      }
    };

  };
})();


// directives
(function() {
  'use strict';

  angular
    .module('n3twork.register')
    .directive('compareTo', compareTo);

  function compareTo () {
    return {
      require: "ngModel",
      scope: {
        otherModelValue: "=compareTo"
      },

    link: function(scope, element, attributes, ngModel) {
      ngModel.$validators.compareTo = function(modelValue) {
        return modelValue == scope.otherModelValue;
      };

      scope.$watch("otherModelValue", function() {
        ngModel.$validate();
      });
    }
    };
  }
})();

(function() {
  'use strict';

  angular
    .module('n3twork.register')
    .directive('ngTaken', ngTaken);

  ngTaken.$inject = ['APISvc', '$timeout'];

  function ngTaken (APISvc, $timeout) {
    var directive = {
      link: link,
    };
    return directive;

    function link(scope, element, attrs, ctrl) {
      //scope.$watch(check);
      //element.on('blur', check);

      function check() {
        var name = element.val();
        if (name != '') {
          console.log('Now doing check: ' + name)
          APISvc.request({
            method: 'GET',
            url: '/register/checkuser',
            data: {
              'username': name
            }
          }).then(function (response) {
            console.log(ctrl);
            var taken = response.data.taken;
            var testedname = response.data.username;
            if (taken && testedname === name) {
              console.log("SORRY, USERNAME ALREADY TAKEN");
              ctrl.setValidity('isTaken', true);
            } else if (!taken && testedname === name) {
              console.log("Yey, username is available");
              ctrl.setValidity('isTaken', false);
            }
          });
        }
      }

    }

  }
})();

