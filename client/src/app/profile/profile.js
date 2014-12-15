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

  ProfileCtrl.$inject = ['APISvc', 'CacheSvc', 'VoteSvc', '$q', '$rootScope', '$routeParams'];

  function ProfileCtrl(APISvc, CacheSvc, VoteSvc, $q, $rootScope, $routeParams) {
    var vm = this;

    vm.friendAction = friendAction;
    vm.newPost = newPost;
    vm.removePost = removePost;
    vm.showVotes = showVotes;
    vm.voteAction = voteAction;

    vm.newPostPrivate = false;
    vm.removePostButtonLoading = {};
    vm.removeButtonConfirmation = {};
    vm.voteButtonLoading = {};

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
            vm.trueFriend = friendInfoArray[1];
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
        vm.loadingPosts = true;
        getPostList(vm.userdata.id).then(function (postList) {
          vm.postlist = postList;
          vm.loadingPosts = false;
        }, function (error) {
          vm.loadingPosts = false;
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
          CacheSvc.checkIfFriend(vm.userdata.id).then(function (friendInfoArray) {
            vm.friendButtonLoading = false;
            vm.isFriend = friendInfoArray[0];
            vm.trueFriend = friendInfoArray[1];
          }, function (error) {
            // error
          });
          vm.loadingPosts = true;
          getPostList(vm.userdata.id).then(function (postList) {
            vm.postlist = postList;
            vm.loadingPosts = false;
          }, function (error) {
            // error
            vm.loadingPosts = false;
          });
        } else {
          // error changing friend status
        }
      }, function (error) {
        // error changing friend status
      });
    }

    function newPost() {
      vm.newPostLoading = true;
      APISvc.request({
        method: 'POST',
        url: '/post/add',
        data: {
          'userID': vm.userdata.id,
          'post': {
            'content': vm.newPostText,
            'private': vm.newPostPrivate
          }
        }
      }).then(function (response) {
        vm.newPostLoading = false;
        if (response.data.successful) {
          resetNewPostForm();
          getPostList(vm.userdata.id).then(function (postList) {
            vm.postlist = postList;
          }, function (error) {
            // error
          });
        } else {
          // error
        }
      }, function (error) {
        // error
        vm.newPostLoading = false;
      });
    }

    function resetNewPostForm () {
      vm.newPostText = "";
      vm.newPostPrivate = false;
    }

    function removePost (postID) {
      vm.removePostButtonLoading[postID] = true;
      APISvc.request({
        method: 'POST',
        url: '/post/delete',
        data: { 'id': postID }
      }).then(function (response) {
        if (response.data.successful) {
          getPostList(vm.userdata.id).then(function (postList) {
            vm.postlist = postList;
            vm.removePostButtonLoading[postID] = false;
            vm.removeButtonConfirmation[postID] = false;
          }, function (error) {
            // error
          });
        } else {
          vm.removePostButtonLoading[postID] = false;
          vm.removeButtonConfirmation[postID] = false;
          // error deleting the post
        }
      }, function (error) {
        // error deleting the post
        vm.removePostButtonLoading[postID] = false;
        vm.removeButtonConfirmation[postID] = false;
      });
    }

    function getPostList(userID) {
      var deferred = $q.defer();

      APISvc.request({
        method: 'GET',
        url: '/post',
        data: { 'userID': userID }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.postList);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function showVotes(postID) {
      VoteSvc.showVotes(postID);
    }

    function voteAction (postID, didIVote) {
      vm.voteButtonLoading[postID] = true;
      VoteSvc.voteAction(postID, didIVote).then(function (successful) {
        getPostList(vm.userdata.id).then(function (postList) {
          vm.postlist = postList;
          vm.voteButtonLoading[postID] = false;
        }, function (error) {
          // error
        });
      }, function (error) {
        // error
      });

    }

  }
})();
