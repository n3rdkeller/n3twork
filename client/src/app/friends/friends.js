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

    init();

    function init() {
      vm.loadingFriends = true;
      vm.loadingFriendRequests = true;
      getUserData().then(function (userdata) {
        vm.userdata = userdata;
        CacheSvc.getFriendListOfUser(vm.userdata.id).then(function (friendList) {
          vm.friendList = friendList;
          vm.loadingFriends = false;
        }, function (error) {
          vm.loadingFriends = false;
        });
        CacheSvc.getFriendRequests().then(function (friendRequestList) {
          vm.friendRequestList = friendRequestList;
          vm.loadingFriendRequests = false;
        }, function (error) {
          vm.loadingFriendRequests = false;
        });
      }, function (error) {
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


  }


})();
