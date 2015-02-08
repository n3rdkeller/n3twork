(function () {
  'use strict';

  angular
    .module('n3twork.conversations')
    .directive('conversationsButton', ConversationsButtonDirective);

  function ConversationsButtonDirective() {
    return {
      restrict: 'A',
      templateUrl: 'app/conversations/conversationsButton.html',
      controller: 'ConversationsButtonCtrl',
      controllerAs: 'mb'
    };
  }

})();
