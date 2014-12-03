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

  GroupCtrl.$inject = ['APISvc','CacheSvc', '$routeParams', '$q', '$rootScope'];
  function GroupCtrl(APISvc, CacheSvc, $routeParams, $q, $rootScope) {
    var vm = this;
    vm.groupAction = groupAction;

    init();

    function init() {
      vm.loadingGroup = true;
      vm.loadingMembers = true;
      vm.statusButtonLoading = true;
      getGroupData().then(function (groupData) {
        vm.loadingGroup = false;
        vm.groupData = groupData;
        checkIfMember().then(function (isMember) {
          vm.isMember = isMember;
          vm.loadingMembers = false;
          vm.statusButtonLoading = false;
        });
      }, function (error) {
        vm.loadingGroup = false;
        vm.errorOccured = true;
        vm.doesntexist = true;
      });
    }


    function getGroupData() {
      var deferred = $q.defer();
      // get groupList from API
      APISvc.request({
        method: 'POST',
        url: '/group/show',
        data: { 'group': parseInt($routeParams.id) }
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



    function getMemberList() {
      var deferred = $q.defer();
      // get groupList from API
      APISvc.request({
        method: 'POST',
        url: '/group/members',
        data: { 'group': parseInt($routeParams.id) }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.memberList);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function groupAction() {
      vm.statusButtonLoading = true;
      APISvc.request({
        method: 'POST',
        url: '/user/group/' + (vm.isMember ? 'leave' : 'join'),
        data: { 'group': parseInt($routeParams.id) }
      }).then(function (response) {
        if (response.data.successful) {
          // change member status
          vm.isMember = !vm.isMember;
          // remove cache
          CacheSvc.removeGroupCache();
          checkIfMember().then(function (isMember) {
            vm.isMember = isMember;
            vm.statusButtonLoading = false;
          }, function (error) {
            // error
          });
        } else {
          // error changing member status
        }
      }, function (error) {
        // error changing member status
      });
    }

    function checkIfMember() {
      var deferred = $q.defer();

      getMemberList().then(function (memberList) {
        vm.memberList = memberList;
        for (var i = 0; i < memberList.length; i++) {
          if (memberList[i].id == $rootScope.userdata.id) {
            deferred.resolve(true);
          }
        }
        deferred.resolve(false);
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }


  }

})();
