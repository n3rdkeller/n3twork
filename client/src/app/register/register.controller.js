(function() {
  'use strict';

  angular
    .module('n3twork.register')
    .controller('RegisterController', RegisterController);

  function RegisterController() {
    var vm = this;
    vm.title = 'RegisterController';
  }
})();
