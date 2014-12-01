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

  FriendsCtrl.$inject = ['APISvc', '$routeParams', '$q', '$rootScope'];
  function FriendsCtrl(APISvc, $routeParams, $q, $rootScope) {
    var vm = this;

    init();

    function init() {
      getUserData().then(function (userdata) {
        vm.userdata = userdata;
        getFriendList().then(function (friendList) {
          vm.friendList = friendList;
        }, function (error) {
          vm.friendList = [];
        });
        getFriendRequests().then(function (friendRequestList) {
          vm.friendRequestList = friendRequestList;
        }, function (error) {
          vm.friendRequestList = [];
        });
      }, function (error) {
        vm.doesntexist = true;
      });
    }

    function getUserData() {
      var deferred = $q.defer();
      vm.loadingFriends = true;
      vm.loadingFriendRequests = true;
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


    function getFriendList() {
      var deferred = $q.defer();
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

    function getFriendRequests() {
      var deferred = $q.defer();
      // get friendList from API
      APISvc.request({
        method: 'POST',
        url: '/user/friendrequests',
        data: { 'id': vm.userdata.id }
      }).then(function (response) {
        vm.loadingFriendRequests = false;
        if (response.data.successful) {
          deferred.resolve(response.data.friendRequests);
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
