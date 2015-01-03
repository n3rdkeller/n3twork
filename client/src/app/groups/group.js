(function() {
  'use strict';

  angular
    .module('n3twork.groups', []);
})();

(function() {
  'use strict';

  angular
    .module('n3twork.groups')
    .controller('GroupCtrl', GroupCtrl);

  GroupCtrl.$inject = ['APISvc','CacheSvc', 'PostSvc', 'VoteSvc', 'CommentSvc', '$routeParams', '$q', '$rootScope', '$modal', '$timeout', '$window'];
  function GroupCtrl(APISvc, CacheSvc, PostSvc, VoteSvc, CommentSvc, $routeParams, $q, $rootScope, $modal, $timeout, $window) {
    var vm = this;

    // group
    vm.groupAction = groupAction;
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
      vm.loadingGroup = true;
      vm.loadingMembers = true;
      vm.statusButtonLoading = true;
      getGroupData().then(function (groupData) {
        vm.loadingGroup = false;
        vm.groupData = groupData;
        checkIfMember().then(function (isMember) {
          vm.isMember = isMember;
          vm.loadingMembers = false;
          vm.statusButtonLoading = false;
        });
        vm.loadingPosts = true;
        PostSvc.getPostList({ 'groupID': vm.groupData.id }).then(function (postList) {
          vm.postlist = postList;
          vm.loadingPosts = false;
        }, function (error) {
          // vm.loadingPosts = false;
        });
      }, function (error) {
        vm.loadingGroup = false;
        vm.errorOccured = true;
        vm.doesntexist = true;
      });
    }

    function getGroupData() {
      var deferred = $q.defer();
      // get groupList from API
      APISvc.request({
        method: 'POST',
        url: '/group/show',
        data: { 'group': parseInt($routeParams.id) }
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

    function getMemberList() {
      var deferred = $q.defer();
      // get groupList from API
      APISvc.request({
        method: 'POST',
        url: '/group/members',
        data: { 'group': parseInt($routeParams.id) }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.memberList);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function groupAction() {
      vm.statusButtonLoading = true;
      var lastOne = (vm.memberList.length == 1) && vm.isMember;
      if (lastOne) {
        confirmGroupLeave().then(function (successful) {
          vm.loadingMembers = true;
          vm.loadingGroup = true;
          changeGroupStatus(lastOne).then(function (successful) {
            $timeout(function() {
              // go back one in history
              $window.history.back();
            }, 1000);
          }, function (error) {
            vm.statusButtonLoading = false;
          });
        }, function (error) {
          vm.statusButtonLoading = false;
        });
      } else {
        changeGroupStatus().then(function (successful) {
          vm.statusButtonLoading = false;
        }, function (error) {
          vm.statusButtonLoading = false;
        }) ;
      }
    }

    function changeGroupStatus (lastOne) {
      var deferred = $q.defer();

      APISvc.request({
          method: 'POST',
          url: '/user/group/' + (vm.isMember ? 'leave' : 'join'),
          data: { 'group': parseInt($routeParams.id) }
        }).then(function (response) {
          if (response.data.successful) {
            // remove cache
            CacheSvc.removeGroupCache();
            if (lastOne) {
              deferred.resolve(true);
            } else {
              // change member status
              vm.isMember = !vm.isMember;
              // check member status again
              checkIfMember().then(function (isMember) {
                vm.isMember = isMember;
                deferred.resolve(true);
              }, function (error) {
                deferred.reject(false);
                // error
              });
            }
          } else {
            deferred.reject(false);
            // error changing member status
          }
        }, function (error) {
          deferred.reject(false);
          // error changing member status
        });

      return deferred.promise;
    }

    function confirmGroupLeave () {
      var deferred = $q.defer();

      // confirmation if i was the last one
      var modalInstance = $modal.open({
        templateUrl: 'groupLeaveConfirmation.html',
        controller: 'LeaveConfirmationCtrl',
        controllerAs: 'confirm',
        size: 'sm'
      }).result.then(function (confirmed) {
        vm.confirmed = confirmed;
        if (confirmed) {
          deferred.resolve(confirmed);
        }
      }, function () {
        vm.confirmed = false;
        deferred.reject(vm.confirmed);
      });

      return deferred.promise;
    }

    function checkIfMember() {
      var deferred = $q.defer();

      getMemberList().then(function (memberList) {
        vm.memberList = memberList;
        for (var i = 0; i < memberList.length; i++) {
          if (memberList[i].id == $rootScope.userdata.id) {
            deferred.resolve(true);
          }
        }
        deferred.resolve(false);
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function newPost() {
      vm.newPostLoading = true;
      PostSvc.newPost('groupID', vm.groupData.id, vm.newPostText, vm.newPostPrivate).then(function (successful) {
        PostSvc.getPostList({ 'groupID': vm.groupData.id }).then(function (postList) {
          resetNewPostForm();
          console.log('no error');
          vm.postlist = postList;
          vm.newPostLoading = false;
        }, function (error) {
          vm.newPostLoading = false;
        });
      }, function (error) {
        vm.newPostLoading = false;
      });
    }

    function resetNewPostForm () {
      vm.newPostText = "";
      vm.newPostPrivate = false;
    }

    function showVotes(postID) {
      VoteSvc.showVotes(postID);
    }

    function voteAction (postID, didIVote) {
      vm.voteButtonLoading[postID] = true;
      VoteSvc.voteAction(postID, didIVote).then(function (successful) {
        PostSvc.getPostList({ 'groupID': vm.groupData.id }).then(function (postList) {
          vm.postlist = postList;
          vm.voteButtonLoading[postID] = false;
        }, function (error) {
          // error
        });
      }, function (error) {
        // error
      });
    }

    function removePost (postID) {
      vm.removePostButtonLoading[postID] = true;

      PostSvc.removePost(postID).then(function (successful) {
        if (successful) {
          PostSvc.getPostList({ 'groupID': vm.groupData.id }).then(function (postList) {
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


(function() {
  'use strict';

  angular
    .module('n3twork.groups')
    .controller('LeaveConfirmationCtrl', LeaveConfirmationCtrl);

  LeaveConfirmationCtrl.$inject = ['$modalInstance'];
  function LeaveConfirmationCtrl($modalInstance) {
    var vm = this;

    vm.yes = yesButtonPressed;
    vm.no = noButtonPressed;


    function yesButtonPressed () {
      vm.loading = true;
      $modalInstance.close(true);
    }

    function noButtonPressed () {
      vm.loading = true;
      $modalInstance.dismiss(false);
    }

  }
})();
