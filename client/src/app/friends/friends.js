(function() {
  'use strict';

  angular
    .module('n3twork.friends', []);
})();

(function() {
  'use strict';

  angular
    .module('n3twork.friends')
    .controller('FriendsCtrl', FriendsCtrl);

  FriendsCtrl.$inject = ['APISvc', '$routeParams'];
  function FriendsCtrl(APISvc, $routeParams) {
    var vm = this;



  }


})();
