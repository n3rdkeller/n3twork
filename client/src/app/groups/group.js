(function() {
  'use strict';

  angular
    .module('n3twork.groups', []);
})();

(function() {
  'use strict';

  angular
    .module('n3twork.groups')
    .controller('GroupCtrl', GroupCtrl);

  GroupCtrl.$inject = ['APISvc', '$routeParams', '$q', '$rootScope'];
  function GroupCtrl(APISvc, $routeParams, $q, $rootScope) {
    var vm = this;

    init();

    function init() {
      getGroupData().then(function (groupData) {
        vm.groupData = groupData;
      }, function (error) {
        vm.errorOccured = true;
        vm.doesntexist = true;
      });
    }


    function getGroupData() {
      vm.loadingGroup = true;
      var deferred = $q.defer();
      // get groupList from API
      APISvc.request({
        method: 'POST',
        url: '/group/show',
        data: { 'group': parseInt($routeParams.id) }
      }).then(function (response) {
        vm.loadingGroup = false;
        if (response.data.successful) {
          deferred.resolve(response.data);
        } else {
          deferred.reject(response.data.successful);
          vm.errorOccured = true;
        }
      }, function (error) {
        vm.loadingGroup = false;
        vm.errorOccured = true;
        deferred.reject(error);
      });

      return deferred.promise;
    }


  }


})();
