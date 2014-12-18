(function() {
  'use strict';

  angular
    .module('n3twork.groups')
    .controller('MembersCtrl', MembersCtrl);

  MembersCtrl.$inject = ['APISvc', '$routeParams', '$q'];
  function MembersCtrl(APISvc, $routeParams, $q) {
    var vm = this;

    init();

    function init() {
      vm.loadingMembers = true;
      vm.groupID = parseInt($routeParams.id);
      getMemberList().then(function (response) {
        vm.memberList = response.memberList;
        vm.groupName = response.name;
        vm.loadingMembers = false;
      }, function (error) {
        vm.doesntexist = true;
      });
    }

    function getMemberList() {
      var deferred = $q.defer();
      // get memberList from API
      APISvc.request({
        method: 'POST',
        url: '/group/members',
        data: { 'group': vm.groupID }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }
  }

})();
