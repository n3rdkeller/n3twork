(function() {
  'use strict';

  angular
    .module('n3twork.groups')
    .controller('GroupsCtrl', GroupsCtrl);

  GroupsCtrl.$inject = ['CacheSvc', '$routeParams', '$modal', '$timeout', '$location', '$rootScope'];
  function GroupsCtrl(CacheSvc, $routeParams, $modal, $timeout, $location, $rootScope) {
    var vm = this;

    vm.openCreateGroupModal = openCreateGroupModal;

    init();

    function init() {
      vm.loadingGroups = true;
      CacheSvc.getUserData($routeParams.username).then(function (userdata) {
        vm.userdata = userdata;
        if (vm.userdata.username == $rootScope.userdata.username) {
          vm.itsMe = true;
        }
        CacheSvc.getGroupListOfUser(vm.userdata.username).then(function (groupList) {
          vm.groupList = groupList;
          vm.loadingGroups = false;
        }, function (error) {
          vm.loadingGroups = false;
        });
      }, function (error) {
        vm.doesntexist = true;
      });
    }

    function openCreateGroupModal() {
      var modalInstance = $modal.open({
        templateUrl: 'app/groups/createGroupModal.html',
        controller: 'CreateGroupCtrl',
        controllerAs: 'create',
        keyboard: false
      }).result.then(function (groupName) {
        vm.loadingGroups = true;
        CacheSvc.removeGroupCache();
        CacheSvc.getGroupListOfUser(vm.userdata.username).then(function (groupList) {
          vm.groupList = groupList;
          $timeout(function() {
            $location.path('group/' + getIdForGroupName(groupName));

          }, 500);
          vm.loadingGroups = false;
        }, function (error) {
          vm.loadingGroups = false;
        });
      }, function () {
        // modal dismissed
      });
    }

    function getIdForGroupName (name) {
      for (var group in vm.groupList) {
        if (vm.groupList[group].groupName == name) {
          return vm.groupList[group].groupID;
        }
      }
      return 0;
    }

  }


})();

(function() {
  'use strict';

  angular
    .module('n3twork.groups')
    .controller('CreateGroupCtrl', CreateGroupCtrl);

  CreateGroupCtrl.$inject = ['APISvc', '$modalInstance'];
  function CreateGroupCtrl(APISvc, $modalInstance) {
    var vm = this;

    vm.ok = okButtonPressed;
    vm.cancel = cancelButtonPressed;


    function okButtonPressed () {
      vm.loading = true;
      APISvc.request({
        method: 'POST',
        url: '/group/create',
        data: {
          'groupName': vm.groupName,
          'groupDescr': vm.groupDescr
        }
      }).then(function (response) {
        vm.loading = false;
        if (response.data.successful) {
          $modalInstance.close(vm.groupName);
        } else {
          vm.errorOccured = true;
          vm.errorReason = response.data.reason;
        }
      }, function (error) {
        vm.loading = false;
        vm.errorOccured = true;
        vm.errorReason = error;
      });
    }

    function cancelButtonPressed () {
      $modalInstance.dismiss('dismissed');
    }

  }
})();
