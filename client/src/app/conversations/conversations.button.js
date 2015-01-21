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

(function() {
  'use strict';

  angular
    .module('n3twork.conversations')
    .controller('ConversationsButtonCtrl', ConversationsButtonCtrl);

  ConversationsButtonCtrl.$inject = ['ConversationSvc'];
  function ConversationsButtonCtrl(ConversationSvc) {
    var vm = this;

    init();

    function init() {
      vm.unreadLoading = true;
      ConversationSvc.getUnreadForBadge().then(function (unreadCount) {
        vm.unreadCount = unreadCount;
        vm.unreadLoading = false;
      }, function (err) {
        vm.errorOccured = true;
        vm.unreadLoading = false;
      });
    }
  }
})();

