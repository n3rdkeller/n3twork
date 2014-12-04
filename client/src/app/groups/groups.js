(function() {
  'use strict';

  angular
    .module('n3twork.groups')
    .controller('GroupsCtrl', GroupsCtrl);

  GroupsCtrl.$inject = ['CacheSvc', '$routeParams', '$modal', '$timeout'];
  function GroupsCtrl(CacheSvc, $routeParams, $modal, $timeout) {
    var vm = this;

    vm.openCreateGroupModal = openCreateGroupModal;

    init();

    function init() {
      vm.loadingGroups = true;
      CacheSvc.getUserData($routeParams.username).then(function (userdata) {
        vm.userdata = userdata;
        CacheSvc.getGroupListOfUser($routeParams.username).then(function (groupList) {
          vm.groupList = groupList;
          vm.loadingGroups = false;
        }, function (error) {
          vm.groupList = [];
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
        keyboard: false,
        backdrop: false
      });
      modalInstance.result.then(function (groupID) {
        $timeout(function() {
          vm.loadingGroups = true;
          CacheSvc.removeGroupCache();
          $location.path('group/' + groupID.toString());
        }, 500);
      }, function () {
        console.log('modal dismissed at ' + new Date());
      });
    }

  }


})();

(function() {
  'use strict';

  angular
    .module('n3twork.groups')
    .controller('CreateGroupCtrl', CreateGroupCtrl);

  CreateGroupCtrl.$inject = ['APISvc', '$location', 'CacheSvc', '$modalInstance', '$timeout'];
  function CreateGroupCtrl(APISvc, $location, CacheSvc, $modalInstance, $timeout) {
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
