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

  FeedCtrl.$inject = ['APISvc', 'VoteSvc', '$q', '$rootScope'];
  function FeedCtrl(APISvc, VoteSvc, $q, $rootScope) {
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
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function newPost() {
      vm.newPostLoading = true;
      APISvc.request({
        method: 'POST',
        url: '/post/add',
        data: {
          'userID': $rootScope.userdata.id,
          'post': {
            'content': vm.newPostText,
            'private': vm.newPostPrivate
          }
        }
      }).then(function (response) {
        vm.newPostLoading = false;
        if (response.data.successful) {
          resetNewPostForm();
          getFeed().then(function (postList) {
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
          getFeed().then(function (postList) {
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
