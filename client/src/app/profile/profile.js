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

  ProfileCtrl.$inject = ['APISvc', '$q', '$rootScope', '$routeParams'];

  function ProfileCtrl(APISvc, $q, $rootScope, $routeParams) {
    var vm = this;

    vm.friendAction = friendAction;

    init();

    function init() {
      getUserData().then(function (userdata) {
        vm.userdata = userdata;
        checkIfFriend().then(function (isFriend) {
          vm.isFriend = isFriend;
        }, function (error) {
          // error
        });
        getGroupList().then(function (groupList) {
          vm.grouplist = groupList;
        }, function (error) {
          vm.grouplist = [];
        });
        getFriendList().then(function (friendList) {
          vm.friendlist = friendList;
        }, function (error) {
          vm.friendlist = [];
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

    function getGroupList() {
      var deferred = $q.defer();
      vm.loadingGroups = true;
      // get groupList from API
      APISvc.request({
        method: 'POST',
        url: '/user/groups',
        data: { 'username': vm.userdata.username }
      }).then(function (response) {
        vm.loadingGroups = false;
        if (response.data.successful) {
          deferred.resolve(response.data.groupList);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function getFriendList() {
      var deferred = $q.defer();
      vm.loadingFriends = true;
      // get friendList from API
      APISvc.request({
        method: 'POST',
        url: '/user/friends',
        data: { 'id': vm.userdata.id }
      }).then(function (response) {
        vm.loadingFriends = false;
        if (response.data.successful) {
          deferred.resolve(response.data.friendList);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function checkIfFriend() {
      var deferred = $q.defer();
      vm.checkingFriend = true;
      // get OWN friendList from API
      APISvc.request({
        method: 'POST',
        url: '/user/friends',
        data: { }
      }).then(function (response) {
        vm.checkingFriend = false;
        if (response.data.successful) {
          for (var i = 0; i < response.data.friendList.length; i++) {
            if (response.data.friendList[i].id == vm.userdata.id) {
              deferred.resolve(true);
            }
          }
          deferred.resolve(false);
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
        vm.friendButtonLoading = false;
        if (response.data.successful) {
          // change friend status
          vm.isFriend = !vm.isFriend;
        } else {
          // error changing friend status
        }
      }, function (error) {
        // error changing friend status
      });
    }

  }
})();
