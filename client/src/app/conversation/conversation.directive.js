(function () {
  'use strict';

  angular
    .module('n3twork.conversation')
    .directive('conversationMessages', ConversationMessagesDirective);

  function ConversationMessagesDirective() {
    return {
      restrict: 'E',
      templateUrl: 'app/conversation/conversation.html',
      controller: 'ConversationCtrl',
      controllerAs: 'con',
    };
  }

})();
