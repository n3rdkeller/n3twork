(function() {
  'use strict';

  angular
    .module('n3twork.conversations')
    .controller('ConversationsCtrl', ConversationsCtrl);

  ConversationsCtrl.$inject = ['ConversationSvc', '$routeParams', '$rootScope', '$location'];
  function ConversationsCtrl(ConversationSvc, $routeParams, $rootScope, $location) {
    var vm = this;

    vm.archiveConversation = archiveConversation;

    vm.loadingArchive = {};

    init();

    function init() {
      getConversationList(true);
      $rootScope.$watch(function () {
        return $rootScope.somethingNewThere;
      }, function () {
        console.log('something happened');
        getConversationList(false);
        $rootScope.somethingNewThere = false;
      });
    }

    function getConversationList (loadingState) {
      ConversationSvc.getConversationList().then(function (conversationList) {
        vm.conversationList = conversationList;
        $rootScope.conversationList = conversationList;
      }, function (err) {
        vm.errorOccured = true;
      })
    }

    function archiveConversation (conversationID) {
      vm.loadingArchive[conversationID] = true;
      ConversationSvc.archiveConversation(conversationID).then(function (success) {
        getConversationList(false);
        vm.loadingArchive[conversationID] = false;
        if ($routeParams.id == conversationID) {
          $location.url('/conversations');
        }
      }, function (err) {
        // error handling
      });
    }

  }
})();
