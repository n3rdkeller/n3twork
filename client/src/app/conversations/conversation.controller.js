(function() {
  'use strict';

  angular
    .module('n3twork.conversations')
    .controller('ConversationCtrl', ConversationCtrl);

  ConversationCtrl.$inject = ['ConversationSvc', 'CacheSvc', '$routeParams', '$scope', '$q', 'filterFilter'];
  function ConversationCtrl(ConversationSvc, CacheSvc, $routeParams, $scope, $q, filterFilter) {
    var vm = this;

    // methods
    vm.sendMessage = sendMessage;
    vm.getFriends = getFriends;
    vm.addReceiver = addReceiver;

    // vars
    vm.messageList = [];
    vm.newReceiver = undefined;

    // vm.messageList = [
    //   {
    //     "content":"alt",
    //     "senderDate":456456465462,
    //     "senderID":47
    //   },
    //   {
    //     "content":"rofl",
    //     "senderDate":456456465465,
    //     "senderID":45
    //   },
    //   {
    //     "content":"wtf",
    //     "senderDate":456456465465,
    //     "senderID":47
    //   },
    //   {
    //     "content":"test",
    //     "senderDate":456456465465,
    //     "senderID":45
    //   },
    //   {
    //     "content":"omg",
    //     "senderDate":456456465465,
    //     "senderID":47
    //   },
    //   {
    //     "content":"rofl",
    //     "senderDate":456456465465,
    //     "senderID":45
    //   },
    //   {
    //     "content":"wtf",
    //     "senderDate":456456465465,
    //     "senderID":47
    //   },
    //   {
    //     "content":"test",
    //     "senderDate":456456465465,
    //     "senderID":47
    //   },
    //   {
    //     "content":"omg",
    //     "senderDate":456456465465,
    //     "senderID":45
    //   },
    //   {
    //     "content":"rofl",
    //     "senderDate":456456465465,
    //     "senderID":45
    //   },
    //   {
    //     "content":"wtf",
    //     "senderDate":456456465465,
    //     "senderID":47
    //   },
    //   {
    //     "content":"test",
    //     "senderDate":456456465465,
    //     "senderID":47
    //   },
    //   {
    //     "content":"omg",
    //     "senderDate":456456465465,
    //     "senderID":45
    //   },
    //   {
    //     "content":"rofl",
    //     "senderDate":456456465465,
    //     "senderID":45
    //   },
    //   {
    //     "content":"wtf",
    //     "senderDate":456456465465,
    //     "senderID":47
    //   },
    //   {
    //     "content":"test",
    //     "senderDate":456456465465,
    //     "senderID":47
    //   },
    //   {
    //     "content":"omg",
    //     "senderDate":456456465465,
    //     "senderID":45
    //   },
    //   {
    //     "content":"rofl",
    //     "senderDate":456456465465,
    //     "senderID":45
    //   },
    //   {
    //     "content":"wtf",
    //     "senderDate":456456465465,
    //     "senderID":47
    //   },
    //   {
    //     "content":"test",
    //     "senderDate":456456465465,
    //     "senderID":47
    //   },
    //   {
    //     "content":"omg",
    //     "senderDate":456456465465,
    //     "senderID":45
    //   },
    //   {
    //     "content":"rofl",
    //     "senderDate":456456465465,
    //     "senderID":45
    //   },
    //   {
    //     "content":"neu",
    //     "senderDate":456456465468,
    //     "senderID":47
    //   }
    // ];

    init();

    function init () {
      vm.historyLoading = true;
      vm.currentConversation = getConInfo($routeParams.id);
      if (vm.currentConversation) {
        if (vm.currentConversation.id == 'new') {
          // new conversation-frame
          vm.newConversation = true;
          vm.currentConversation.receiverList = [
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
          ];
        } else {
          // TODO: get messages
          // if error, hide conversation
        }
      } else {
        // no conversation
        vm.hideConversation = true;
      }

      vm.historyLoading = false;
    }

    function getConInfo (id) {
      if ($routeParams.id == 'new') return { id: 'new' };
      for (var i = 0; i < $scope.conList.length; i++) {
        if ($scope.conList[i].id == $routeParams.id) return $scope.conList[i];
      };
      return undefined;
    }

    function sendMessage () {
      vm.newMessageLoading = true;
      vm.messageList.push({
        "content": vm.newMessageText,
        "senderDate": new Date(),
        "senderID": $scope.userData.id
      });
      vm.newMessageText = "";
      vm.newMessageLoading = false;
    }

    function getFriends (val) {
      var deferred = $q.defer();

      // CacheSvc.getFriendListOfUser($scope.userData.id).then(function (friendList) {
      //   if (vm.currentConversation.receiverList) {
      //     friendList = friendList.filter(function (element) {
      //       for (var i = 0; i < vm.currentConversation.receiverList.length; i++) {
      //         if (vm.currentConversation.receiverList[i].username == element.username) return false;
      //       };
      //       return true;
      //     });
      //   }
      //   deferred.resolve(friendList);
      // }, function (error) {
      //   deferred.reject(error);
      // });

      CacheSvc.getUserList().then(function (userList) {
        if (vm.currentConversation.receiverList) {
          userList = filterFilter(userList, val);
          userList = userList.filter(function (element) {
            for (var i = 0; i < vm.currentConversation.receiverList.length; i++) {
              if (vm.currentConversation.receiverList[i].username == element.username) return false;
            };
            return true;
          });
        }
        deferred.resolve(userList);
      }, function (error) {
        deferred.reject(error);
      });



      return deferred.promise;
    }

    function addReceiver () {
      if (!vm.currentConversation.receiverList) {
        vm.currentConversation.receiverList = []
      }
      vm.currentConversation.receiverList.push(vm.newReceiver);
      vm.newReceiver = undefined;
    }
  }
})();
