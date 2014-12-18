(function() {
  'use strict';

  angular
    .module('n3twork')
    .factory('PostSvc', PostSvc);

  function PostSvc(APISvc, $q) {
    var service = {
      getPostList: getPostList,
      newPost: newPost,
      removePost: removePost
    };
    return service;

    function newPost(idString, id, text, privateOrPublic) {
      var deferred = $q.defer();

      var dataObj = {}
      dataObj['post'] = {
        'content': text,
        'private': privateOrPublic
      }
      dataObj[idString] = id;

      APISvc.request({
        method: 'POST',
        url: '/post/add',
        data: dataObj
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(true);
        } else {
          // error
          deferred.reject(false);
        }
      }, function (error) {
        // error
        deferred.reject(false);
      });

      return deferred.promise;
    }

    function removePost (postID) {
      var deferred = $q.defer();

      APISvc.request({
        method: 'POST',
        url: '/post/delete',
        data: { 'id': postID }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.successful);
        } else {
          // error deleting the post
          deferred.reject(response.data.successful);
        }
      }, function (error) {
        // error deleting the post
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function getPostList(data) {
      var deferred = $q.defer();

      APISvc.request({
        method: 'POST',
        url: '/post',
        data: data
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
  }
})();
