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

  FeedCtrl.$inject = ['APISvc', 'PostSvc', 'VoteSvc', '$q', '$rootScope'];
  function FeedCtrl(APISvc, PostSvc, VoteSvc, $q, $rootScope) {
    var vm = this;

    vm.showVotes = showVotes;
    vm.voteAction = voteAction;
    vm.removePost = removePost;
    vm.newPost = newPost;

    vm.newPostPrivate = false;
    vm.removePostButtonLoading = {};
    vm.removeButtonConfirmation = {};
    vm.voteButtonLoading = {};

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

  }
})();
