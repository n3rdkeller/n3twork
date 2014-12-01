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
      getUserData().then(function (userdata) {
        vm.userdata = userdata;
        vm.checkingFriend = true;
        if (!vm.itsMe) {
          CacheSvc.checkIfFriend(vm.userdata.id).then(function (isFriend, trueFriend) {
            vm.isFriend = isFriend;
            vm.trueFriend = trueFriend;
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
      });
    }

    function getUserData() {
      var deferred = $q.defer();
      if ($routeParams.username) {
        // if it's my username
        if ($routeParams.username == $rootScope.userdata.username) {
          // it's my own profile
          deferred.resolve($rootScope.userdata);
          vm.itsMe = true;
        } else {
          getUserDataFromAPI().then(function (userdata) {
            deferred.resolve(userdata);
          }, function (error) {
            deferred.reject(error);
          });
        }
      } else {
        // it's my own profile
        deferred.resolve($rootScope.userdata);
        vm.itsMe = true;
      }

      return deferred.promise;
    }

    function getUserDataFromAPI() {
      var deferred = $q.defer();
      vm.loadingUserData = true;
      // get userdata from API
      APISvc.request({
        method: 'POST',
        url: '/user',
        data: { 'username': $routeParams.username }
      }).then(function (response) {
        vm.loadingUserData = false;
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
          if (!vm.isFriend) {
            vm.trueFriend = false;
            vm.friendButtonLoading = false;
          } else {
            CacheSvc.checkIfFriend(vm.userdata.id).then(function (isFriend, trueFriend) {
              vm.friendButtonLoading = false;
              vm.trueFriend = trueFriend;
            }, function (error) {
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
