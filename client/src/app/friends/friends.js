(function() {
  'use strict';

  angular
    .module('n3twork.friends', []);
})();

(function() {
  'use strict';

  angular
    .module('n3twork.friends')
    .controller('FriendsCtrl', FriendsCtrl);

  FriendsCtrl.$inject = ['APISvc', 'CacheSvc', '$routeParams', '$q', '$rootScope'];
  function FriendsCtrl(APISvc, CacheSvc, $routeParams, $q, $rootScope) {
    var vm = this;
    vm.addButtonLoading = {};

    vm.addToFriends = addToFriends;

    init();

    function init() {
      vm.loadingFriends = true;
      vm.loadingFriendRequests = true;
      var username = $routeParams.username;
      vm.itsMe = (username == $rootScope.userdata.username);
      CacheSvc.getUserData(username).then(function (userdata) {
        vm.userdata = userdata;
        if (vm.itsMe) {
          CacheSvc.getFriendRequests().then(function (friendRequestList) {
            vm.friendRequestList = friendRequestList;
            vm.loadingFriendRequests = false;
          }, function (error) {
            vm.loadingFriendRequests = false;
          });
        }
        CacheSvc.getFriendListOfUser(vm.userdata.id).then(function (friendList) {
          vm.friendList = friendList;
          vm.loadingFriends = false;
        }, function (error) {
          vm.loadingFriends = false;
        });
      }, function (error) {
        vm.loadingFriends = false;
        vm.doesntexist = true;
      });
    }

    function getUserData() {
      var deferred = $q.defer();
      vm.loadingFriends = true;
      if ($routeParams.username) {
        // if it's my username
        if ($routeParams.username == $rootScope.userdata.username) {
          // it's my own profile
          vm.loadingFriendRequests = true;
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
        vm.loadingFriendRequests = true;
        deferred.resolve($rootScope.userdata);
        vm.itsMe = true;
      }

      return deferred.promise;
    }

    function getUserDataFromAPI() {
      var deferred = $q.defer();
      // get userdata from API
      APISvc.request({
        method: 'POST',
        url: '/user',
        data: { 'username': $routeParams.username }
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

    function addToFriends(id) {
      vm.addButtonLoading[id] = true;
      APISvc.request({
        method: 'POST',
        url: '/user/friend/add',
        data: { 'friend': id }
      }).then(function (response) {
        if (response.data.successful) {
          // remove cache
          CacheSvc.removeFriendCache();
          init();
        } else {
          vm.addButtonLoading[id] = false;
          // error changing friend status
        }
      }, function (error) {
        vm.addButtonLoading[id] = false;
        // error changing friend status
      });
    }

  }


})();
