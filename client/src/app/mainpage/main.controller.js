(function() {
  'use strict';

  angular
    .module('n3twork.main')
    .controller('MainCtrl', MainCtrl);

  MainCtrl.$inject = ['APISvc', '$q'];

  function MainCtrl(APISvc, $q) {
    var vm = this;
    var deferred = $q.defer();

    getData();

    function getData() {
      APISvc.request({
        method: 'GET',
        url: '/'
      }).then(function(response) {
        deferred.resolve(true);
        vm.username = response.data;
      })
    }
  }
})();
