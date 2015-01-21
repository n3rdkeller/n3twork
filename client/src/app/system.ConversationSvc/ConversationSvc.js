(function() {
  'use strict';

  angular
    .module('n3twork')
    .service('ConversationSvc', ConversationSvc);

  ConversationSvc.$inject = ['APISvc', '$q'];
  function ConversationSvc(APISvc, $q) {
    var service = {
      getUnreadForBadge: getUnreadForBadge,
      getConversationList: getConversationList,
      getMessages: getMessages,
      archiveConversation: archiveConversation,
      readConversation: readConversation
    };

    return service;

    function getUnreadForBadge () {
      var deferred = $q.defer()

      APISvc.request({
        method: 'POST',
        url: '/conversation/unread',
        data: {}
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.unreadCount);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (err) {
        deferred.reject(err);
      });

      return deferred.promise;
    }

    function getConversationList () {
      var deferred = $q.defer()

      APISvc.request({
        method: 'POST',
        url: '/conversation',
        data: {}
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.conversationList);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (err) {
        deferred.reject(err);
      });

      return deferred.promise;
    }

    function getMessages (conversationID) {
      var deferred = $q.defer()

      APISvc.request({
        method: 'POST',
        url: '/conversation/show',
        data: {
          conversationID: conversationID
        }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.messageList);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (err) {
        deferred.reject(err);
      });

      return deferred.promise;
    }

    function archiveConversation (conversationID) {
      var deferred = $q.defer()

      APISvc.request({
        method: 'POST',
        url: '/conversation/archive',
        data: {
          conversationID: conversationID
        }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.successful);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (err) {
        deferred.reject(err);
      });

      return deferred.promise;
    }

    function readConversation (conversationID) {
      var deferred = $q.defer()

      APISvc.request({
        method: 'POST',
        url: '/conversation/read',
        data: {
          conversationID: conversationID
        }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.successful);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (err) {
        deferred.reject(err);
      });

      return deferred.promise;
    }

  }
})();
