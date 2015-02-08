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
      newConversation: newConversation,
      archiveConversation: archiveConversation,
      sendMessage: sendMessage
    };

    return service;

    function getUnreadForBadge () {
      var deferred = $q.defer()

      APISvc.request({
        method: 'POST',
        url: '/conversation/unread',
        data: {},
        ignoreLoadingBar: true
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.unread);
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
        data: {},
        ignoreLoadingBar: true
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
        },
        ignoreLoadingBar: true
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
        },
        ignoreLoadingBar: true
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

    function newConversation (receiverList, name) {
      var deferred = $q.defer()

      APISvc.request({
        method: 'POST',
        url: '/conversation/new',
        data: {
          receiverList: receiverList,
          name: name
        },
        ignoreLoadingBar: true
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.conversationID);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (err) {
        deferred.reject(err);
      });

      return deferred.promise;
    }

    function sendMessage (conversationID, content) {
      var deferred = $q.defer()

      APISvc.request({
        method: 'POST',
        url: '/conversation/send',
        data: {
          conversationID: conversationID,
          content: content
        }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.messageID);
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
