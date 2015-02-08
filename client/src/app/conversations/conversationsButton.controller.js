(function() {
  'use strict';

  angular
    .module('n3twork.conversations')
    .controller('ConversationsButtonCtrl', ConversationsButtonCtrl);

  ConversationsButtonCtrl.$inject = ['ConversationSvc', '$interval', '$rootScope'];
  function ConversationsButtonCtrl(ConversationSvc, $interval, $rootScope) {
    var vm = this;

    init();

    function init() {
      getUnreadFromAPI(true);
      $interval(function () {
        getUnreadFromAPI(false)
      }, 10000);
    }

    function getUnreadFromAPI (loadingState) {
      if (loadingState) vm.unreadLoading = true;
      ConversationSvc.getUnreadForBadge().then(function (unreadCount) {
        if (vm.unreadCount != unreadCount) $rootScope.somethingNewThere = true;
        vm.unreadCount = unreadCount;
        vm.unreadLoading = false;
      }, function (err) {
        vm.errorOccured = true;
        vm.unreadLoading = false;
      });
    }
  }
})();
