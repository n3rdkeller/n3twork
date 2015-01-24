(function() {
  'use strict';

  angular
    .module('n3twork.conversations')
    .controller('ConversationsCtrl', ConversationsCtrl);

  ConversationsCtrl.$inject = ['ConversationSvc', '$routeParams'];
  function ConversationsCtrl(ConversationSvc, $routeParams) {
    var vm = this;

    vm.conversationList = [
      {
        "receiverList": [
          {
            "username":"johannes",
            "firstName":"Johannes",
            "lastName":"Wolf",
            "email":"johannes@n3rdkeller.de",
            "emailhash":"675edbe61a0b962781df97de9d76996c"
          }
        ],
        "unreadCount": 42,
        "name":"n3rdkeller",
        "id":1337
      },
      {
        "receiverList": [
          {
            "username":"johannes",
            "firstName":"Johannes",
            "lastName":"Wolf",
            "email":"johannes@n3rdkeller.de",
            "emailhash":"675edbe61a0b962781df97de9d76996c"
          },
          {
            "username":"dieter",
            "firstName":"Dieter",
            "lastName":"",
            "email":"dieter",
            "emailhash":"760a8e7d2bcacc5b55afdc9b23816925"
          },
        ],
        "unreadCount": 7,
        "name":"",
        "id":1338
      },
      {
        "receiverList": [
          {
            "username":"dieter",
            "firstName":"Dieter",
            "lastName":"",
            "email":"dieter",
            "emailhash":"760a8e7d2bcacc5b55afdc9b23816925"
          },
        ],
        "unreadCount": 0,
        "name":"LoL",
        "id":1339
      }
    ];


    init();

    function init() {
      // TODO: get conversations from ConversationSvc
    }

  }
})();
