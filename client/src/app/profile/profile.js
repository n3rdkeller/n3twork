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

  ProfileCtrl.$inject = ['APISvc', 'CacheSvc', 'PostSvc', 'VoteSvc', '$q', '$rootScope', '$routeParams'];

  function ProfileCtrl(APISvc, CacheSvc, PostSvc, VoteSvc, $q, $rootScope, $routeParams) {
    var vm = this;

    // friends
    vm.friendAction = friendAction;
    // post
    vm.newPost = newPost;
    vm.removePost = removePost;
    // vote
    vm.showVotes = showVotes;
    vm.voteAction = voteAction;
    // comment
    vm.commentAction = commentAction;
    vm.newComment = newComment;
    vm.removeComment = removeComment;

    // post
    vm.newPostPrivate = false;
    vm.removePostButtonLoading = {};
    vm.removeButtonConfirmation = {};
    // vote
    vm.voteButtonLoading = {};
    // comments
    vm.commentsLoading = {};
    vm.showComments = {};
    vm.newCommentForm = {};
    vm.newCommentText = {};
    vm.newCommentLoading = {};
    vm.removeCommentButtonConfirmation = {};
    vm.removeCommentButtonLoading = {};

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
        PostSvc.getPostList({ 'userID': vm.userdata.id }).then(function (postList) {
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
          PostSvc.getPostList({ 'userID': vm.userdata.id }).then(function (postList) {
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
      PostSvc.newPost('userID', $rootScope.userdata.id, vm.newPostText, vm.newPostPrivate).then(function (successful) {
        vm.newPostLoading = false;
        resetNewPostForm();
        PostSvc.getPostList({ 'userID': vm.userdata.id }).then(function (postList) {
          vm.postlist = postList;
        }, function (error) {
          // error
        });
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
          PostSvc.getPostList({ 'userID': vm.userdata.id }).then(function (postList) {
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
        method: 'POST',
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
        PostSvc.getPostList({ 'userID': vm.userdata.id }).then(function (postList) {
          vm.postlist = postList;
          vm.voteButtonLoading[postID] = false;
        }, function (error) {
          // error
        });
      }, function (error) {
        // error
      });

    }

    function commentAction (postID, numberOfComments) {
      vm.showComments[postID] = !vm.showComments[postID];
      if (numberOfComments != 0) {
        vm.commentsLoading[postID] = true;
        if (vm.showComments[postID]) {
          CommentSvc.getCommentList(postID).then(function (commentList) {
              vm.commentsLoading[postID] = false;
              addCommentsToPost(commentList, postID);
            }, function (error) {
              vm.commentsLoading[postID] = false;
            });
        } else {
          vm.commentsLoading[postID] = false;
        }
      }
    }

    function addCommentsToPost (commentList, postID) {
      for (var i = 0; i < vm.postlist.length; i++) {
        if (vm.postlist[i].id == postID) {
          vm.postlist[i].comments = commentList;
          vm.postlist[i].numberOfComments = commentList.length;
        }
      };
    }

    function newComment (postID) {
      vm.newCommentLoading[postID] = true;
      CommentSvc.newComment(postID, vm.newCommentText[postID]).then(function (successful) {
        CommentSvc.getCommentList(postID).then(function (commentList) {
            addCommentsToPost(commentList, postID);
            vm.newCommentLoading[postID] = false;
            resetNewCommentForm(postID);
          }, function (error) {
            vm.newCommentLoading[postID] = false;
          });
      }, function (error) {
        vm.newCommentLoading[postID] = false;
      });
    }

    function resetNewCommentForm (postID) {
      vm.newCommentText[postID] = "";
    }

    function removeComment (commentID, postID) {
      vm.removeCommentButtonLoading[commentID] = true;
      CommentSvc.removeComment(commentID, postID).then(function (successful) {
        CommentSvc.getCommentList(postID).then(function (commentList) {
          addCommentsToPost(commentList, postID);
          vm.removeCommentButtonConfirmation[commentID] = false;
          vm.removeCommentButtonLoading[commentID] = false;
        }, function (error) {
          vm.removeCommentButtonLoading[commentID] = false;
        });
      }, function (error) {
        // error deleting the comment
        vm.removeCommentButtonConfirmation[commentID] = false;
        vm.removeCommentButtonLoading[commentID] = false;
      });
    }

  }
})();
