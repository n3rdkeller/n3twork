(function() {
  'use strict';

  angular
    .module('n3twork.profile', []);
})();

(function() {
  'use strict';

  angular
    .module('n3twork.profile')
    .controller('ProfileCtrl', ProfileCtrl);

  ProfileCtrl.$inject = ['APISvc', 'CacheSvc', '$q', '$rootScope', '$routeParams'];

  function ProfileCtrl(APISvc, CacheSvc, $q, $rootScope, $routeParams) {
    var vm = this;

    vm.friendAction = friendAction;

    init();

    function init() {
      vm.loadingUserData = true;
      CacheSvc.getUserData($routeParams.username).then(function (userdata) {
        vm.loadingUserData = false;
        vm.userdata = userdata;
        vm.itsMe = (vm.userdata.id == $rootScope.userdata.id);
        vm.checkingFriend = true;
        if (!vm.itsMe) {
          CacheSvc.checkIfFriend(vm.userdata.id).then(function (friendInfoArray) {
            vm.isFriend = friendInfoArray[0];
            vm.trueFriend = friendInfoArray[0];
            vm.checkingFriend = false;
          }, function (error) {
            vm.checkingFriend = false;
          });
        }
        vm.loadingGroups = true;
        CacheSvc.getGroupListOfUser(vm.userdata.username).then(function (groupList) {
          vm.grouplist = groupList;
          vm.loadingGroups = false;
        }, function (error) {
          vm.loadingGroups = false;
        });
        vm.loadingFriends = true;
        CacheSvc.getFriendListOfUser(vm.userdata.id).then(function (friendList) {
          vm.friendlist = friendList;
          vm.loadingFriends = false;
        }, function (error) {
          vm.loadingFriends = false;
        });
      }, function (error) {
        vm.doesntexist = true;
        vm.loadingUserData = false;
      });
    }


    function friendAction() {
      vm.friendButtonLoading = true;
      APISvc.request({
        method: 'POST',
        url: '/user/friend/' + (vm.isFriend ? 'remove' : 'add'),
        data: { 'friend': vm.userdata.id }
      }).then(function (response) {
        if (response.data.successful) {
          // change friend status
          vm.isFriend = !vm.isFriend;
          // remove cache
          CacheSvc.removeFriendCache();
          if (!vm.isFriend) {
            vm.trueFriend = false;
            vm.friendButtonLoading = false;
          } else {
            CacheSvc.checkIfFriend(vm.userdata.id).then(function (isFriend, trueFriend) {
              vm.friendButtonLoading = false;
              vm.trueFriend = trueFriend;
            }, function (error) {
              // error
            });
          }
        } else {
          // error changing friend status
        }
      }, function (error) {
        // error changing friend status
      });
    }

  }
})();
