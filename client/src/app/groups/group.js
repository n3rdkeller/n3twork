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

  GroupCtrl.$inject = ['APISvc','CacheSvc', '$routeParams', '$q', '$rootScope', '$modal', '$timeout', '$window'];
  function GroupCtrl(APISvc, CacheSvc, $routeParams, $q, $rootScope, $modal, $timeout, $window) {
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
      var lastOne = (vm.memberList.length == 1) && vm.isMember;
      if (lastOne) {
        confirmGroupLeave().then(function (successful) {
          vm.loadingMembers = true;
          vm.loadingGroup = true;
          changeGroupStatus(lastOne).then(function (successful) {
            $timeout(function() {
              // go back one in history
              $window.history.back();
            }, 1000);
          }, function (error) {
            vm.statusButtonLoading = false;
          });
        }, function (error) {
          vm.statusButtonLoading = false;
        });
      } else {
        changeGroupStatus().then(function (successful) {
          vm.statusButtonLoading = false;
        }, function (error) {
          vm.statusButtonLoading = false;
        }) ;
      }
    }

    function changeGroupStatus (lastOne) {
      var deferred = $q.defer();

      APISvc.request({
          method: 'POST',
          url: '/user/group/' + (vm.isMember ? 'leave' : 'join'),
          data: { 'group': parseInt($routeParams.id) }
        }).then(function (response) {
          if (response.data.successful) {
            // remove cache
            CacheSvc.removeGroupCache();
            if (lastOne) {
              deferred.resolve(true);
            } else {
              // change member status
              vm.isMember = !vm.isMember;
              // check member status again
              checkIfMember().then(function (isMember) {
                vm.isMember = isMember;
                deferred.resolve(true);
              }, function (error) {
                deferred.reject(false);
                // error
              });
            }
          } else {
            deferred.reject(false);
            // error changing member status
          }
        }, function (error) {
          deferred.reject(false);
          // error changing member status
        });

      return deferred.promise;
    }

    function confirmGroupLeave () {
      var deferred = $q.defer();

      // confirmation if i was the last one
      var modalInstance = $modal.open({
        templateUrl: 'groupLeaveConfirmation.html',
        controller: 'LeaveConfirmationCtrl',
        controllerAs: 'confirm',
        size: 'sm'
      }).result.then(function (confirmed) {
        vm.confirmed = confirmed;
        if (confirmed) {
          deferred.resolve(confirmed);
        }
      }, function () {
        vm.confirmed = false;
        deferred.reject(vm.confirmed);
      });

      return deferred.promise;
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


(function() {
  'use strict';

  angular
    .module('n3twork.groups')
    .controller('LeaveConfirmationCtrl', LeaveConfirmationCtrl);

  LeaveConfirmationCtrl.$inject = ['$modalInstance'];
  function LeaveConfirmationCtrl($modalInstance) {
    var vm = this;

    vm.yes = yesButtonPressed;
    vm.no = noButtonPressed;


    function yesButtonPressed () {
      vm.loading = true;
      $modalInstance.close(true);
    }

    function noButtonPressed () {
      vm.loading = true;
      $modalInstance.dismiss(false);
    }

  }
})();
