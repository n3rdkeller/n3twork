(function() {
  'use strict';

  angular
    .module('n3twork.feed', []);
})();

(function() {
  'use strict';

  angular
    .module('n3twork.feed')
    .controller('FeedCtrl', FeedCtrl);

  FeedCtrl.$inject = ['APISvc', 'PostSvc', 'VoteSvc', 'CommentSvc', '$q', '$rootScope'];
  function FeedCtrl(APISvc, PostSvc, VoteSvc, CommentSvc, $q, $rootScope) {
    var vm = this;

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
      vm.loadingFeed = true;
      getFeed().then(function (postlist) {
        vm.postlist = postlist;
        vm.loadingFeed = false;
      }, function (error) {
        vm.loadingFeed = false;
      });
    }

    function getFeed() {
      var deferred = $q.defer();

      APISvc.request({
        method: 'POST',
        url: '/post/newsfeed/',
        data: { }
      }).then(function (response) {
        deferred.resolve(response.data.postList);
      }, function (error) {
        vm.errorOccured = true;
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function newPost() {
      vm.newPostLoading = true;
      PostSvc.newPost('userID', $rootScope.userdata.id, vm.newPostText, vm.newPostPrivate).then(function (successful) {
        resetNewPostForm();
        getFeed().then(function (postList) {
          vm.postlist = postList;
          vm.newPostLoading = false;
        }, function (error) {
          // error
          vm.newPostLoading = false;
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
      PostSvc.removePost(postID).then(function (successful) {
        getFeed().then(function (postList) {
          vm.postlist = postList;
          vm.removePostButtonLoading[postID] = false;
          vm.removeButtonConfirmation[postID] = false;
        }, function (error) {
          // error
        });
      }, function (error) {
        // error deleting the post
        vm.removePostButtonLoading[postID] = false;
        vm.removeButtonConfirmation[postID] = false;
      });
    }

    function showVotes(postID) {
      VoteSvc.showVotes(postID);
    }

    function voteAction (postID, voted) {
      vm.voteButtonLoading[postID] = true;
      VoteSvc.voteAction(postID, voted).then(function (successful) {
        getFeed().then(function (postlist) {
          vm.postlist = postlist;
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
      }
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
