(function() {
  'use strict';

  angular
    .module('n3twork')
    .factory('CommentSvc', CommentSvc);

  CommentSvc.$inject = ['APISvc', '$q'];
  function CommentSvc (APISvc, $q) {
    var service = {
      getCommentList: getCommentList,
      newComment: newComment,
      removeComment: removeComment
    };
    return service;

    function getCommentList (postID) {
      var deferred = $q.defer();

      APISvc.request({
        method: 'POST',
        url: '/post/comments/',
        data: { 'id': postID }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.commentList);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function newComment (postID, content) {
      var deferred = $q.defer();

      APISvc.request({
        method: 'POST',
        url: '/post/comment/add',
        data: {
          'id': postID,
          'content': content
        }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.successful);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function removeComment (commentID) {
      var deferred = $q.defer();

      APISvc.request({
        method: 'POST',
        url: '/post/comment/remove',
        data: {
          'id': commentID,
        }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.successful);
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
