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
            method: 'POST',
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
