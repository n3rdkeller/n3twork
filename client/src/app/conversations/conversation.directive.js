(function () {
  'use strict';

  angular
    .module('n3twork.conversations')
    .directive('conversationMessages', ConversationMessagesDirective);

  function ConversationMessagesDirective() {
    return {
      restrict: 'E',
      templateUrl: 'app/conversations/conversation.html',
      controller: 'ConversationCtrl',
      controllerAs: 'con',
      scope: {
        conList: '=',
        userData: '='
      }
    };
  }

})();
