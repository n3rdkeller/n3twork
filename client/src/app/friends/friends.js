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

    init(true);

    function init(loadingStates) {
      if (loadingStates) vm.loadingFriends = true;
      var username = $routeParams.username;
      vm.itsMe = (username == $rootScope.userdata.username);
      CacheSvc.getUserData(username).then(function (userdata) {
        vm.userdata = userdata;
        if (vm.itsMe) {
          if (loadingStates) {
            vm.loadingFriendRequests = true;
            vm.loadingNetworkSuggestions = true;
            vm.loadingPostSuggestions = true;
          }
          CacheSvc.getFriendRequests().then(function (friendRequestList) {
            vm.friendRequestList = friendRequestList;
            vm.loadingFriendRequests = false;
          }, function (error) {
            vm.loadingFriendRequests = false;
            vm.errorOccured = true;
          });
          CacheSvc.getFriendNetworkSuggestions().then(function (networkSuggestions) {
            vm.networkSuggestions = networkSuggestions;
            vm.loadingNetworkSuggestions = false;
          }, function (error) {
            vm.loadingNetworkSuggestions = false;
            vm.errorOccured = true;
          });
          CacheSvc.getFriendPostSuggestions().then(function (postSuggestions) {
            vm.postSuggestions = postSuggestions;
            vm.loadingPostSuggestions = false;
          }, function (error) {
            vm.loadingPostSuggestions = false;
            vm.errorOccured = true;
          });
        }
        CacheSvc.getFriendListOfUser(vm.userdata.id).then(function (friendList) {
          vm.friendList = friendList;
          vm.loadingFriends = false;
        }, function (error) {
          vm.loadingFriends = false;
          vm.errorOccured = true;
        });
      }, function (error) {
        vm.loadingFriends = false;
        vm.doesntexist = true;
      });
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
          init(false);
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
