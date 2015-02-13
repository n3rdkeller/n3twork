(function() {
  'use strict';

  angular
    .module('n3twork.conversation')
    .controller('ConversationCtrl', ConversationCtrl);

  ConversationCtrl.$inject = ['ConversationSvc', 'CacheSvc', '$routeParams', '$rootScope', '$q', '$location', 'filterFilter'];
  function ConversationCtrl(ConversationSvc, CacheSvc, $routeParams, $rootScope, $q, $location, filterFilter) {
    var vm = this;

    // methods
    vm.sendMessage = sendMessage;
    vm.getFriends = getFriends;
    vm.addReceiver = addReceiver;
    vm.deleteReceiver = deleteReceiver;
    vm.getUserForID = getUserForID;

    // vars
    vm.newReceiver = undefined;

    init();

    function init () {
      vm.currentConversation = getConInfo($routeParams.id);
      if (vm.currentConversation) {
        if (vm.currentConversation.id == 'new') {
          // new conversation-frame
          vm.newConversation = true;
        } else {
          getMessagesFromAPI(true);
          $rootScope.$on('reload-messages', function () {
            return getMessagesFromAPI(false);
          });
        }
      } else {
        // no conversation
        vm.hideConversation = true;
      }
    }

    function getMessagesFromAPI (loadingState) {
      console.log('get messages from api');
      if (loadingState) vm.historyLoading = true;
      ConversationSvc.getMessages(vm.currentConversation.id).then(function (messageList) {
        vm.messageList = messageList;
        $rootScope.$emit('opened-message', vm.currentConversation.id);
        if (loadingState) vm.historyLoading = false;
      }, function (err) {
        vm.hideConversation = true;
        vm.errorOccured = true;
        if (loadingState) vm.historyLoading = false;
      })
    }

    function getConInfo (id) {
      var newConString = 'new';
      if ($routeParams.id == newConString) {
        var newConInfo = {
          id: newConString
        }
        if ($routeParams.username) {
          // check existing conversations with only that user
          for (var i = 0; i < $rootScope.conversationList.length; i++) {
            if (($rootScope.conversationList[i].receiverList[0].username == $routeParams.username) && ($rootScope.conversationList[i].receiverList.length == 1)) $location.url('/conversations/' + $rootScope.conversationList[i].id);
          }
          // TODO: compare usernames of receiverList to all receivers of conversationsList, same as above but with more than 1 users

          // let the magic happen
          CacheSvc.getUserData($routeParams.username).then(function (userData) {
            newConInfo.receiverList = [];
            newConInfo.receiverList.push(userData);
          }, function (error) {
            // error
          });
        }
        vm.newReceiverAllowed = true;
        return newConInfo;
      }
      for (var i = 0; i < $rootScope.conversationList.length; i++) {
        if ($rootScope.conversationList[i].id == parseInt($routeParams.id)) return $rootScope.conversationList[i];
      };
      return undefined;
    }

    function sendMessage () {
      vm.newMessageLoading = true;
      if (vm.newConversation) {
        vm.newConversation = false;
        vm.wasNewConversation = true;
        ConversationSvc.newConversation(vm.currentConversation.receiverList, vm.newConversationName).then(function (conversationID) {
          $rootScope.somethingNewThere = true;
          vm.currentConversation.id = conversationID;
          sendActualMessage();
        }, function (error) {
          // error
        })
      } else {
        sendActualMessage();
      }
    }

    function sendActualMessage () {
      ConversationSvc.sendMessage(vm.currentConversation.id, vm.newMessageText).then(function (messageID) {
        if (vm.wasNewConversation) {
          $location.url('/conversations/' + vm.currentConversation.id);
        }
        if (!vm.messageList) vm.messageList = [];
        vm.messageList.push({
          "content": vm.newMessageText,
          "sendDate": Date.now(),
          "senderID": $rootScope.userdata.id
        });
        vm.newMessageText = "";
        vm.newMessageLoading = false;
      }, function (err) {
        // error
        vm.newMessageLoading = false;
      });
    }


    // TODO: hide form by vm.newReceiverAllowed = false;
    // if whole friendList is in receiverList
    function getFriends (val) {
      var deferred = $q.defer();

      // with FriendList of User
      CacheSvc.getFriendListOfUser($rootScope.userdata.id).then(function (friendList) {
        // lets use the filterFilter, to filterFilter the entries
        friendList = filterFilter(friendList, val);
        // if theres already someone in the receiverList, delete
        // them from the gotten friendList
        if (vm.currentConversation.receiverList) {
          friendList = friendList.filter(function (element) {
            for (var i = 0; i < vm.currentConversation.receiverList.length; i++) {
              if (vm.currentConversation.receiverList[i].username == element.username) return false;
            };
            return true;
          });
        }
        deferred.resolve(friendList);
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function addReceiver () {
      if (!vm.currentConversation.receiverList) vm.currentConversation.receiverList = [];
      vm.currentConversation.receiverList.push(vm.newReceiver);
      vm.newReceiver = undefined;
    }

    function deleteReceiver (receiver) {
      vm.currentConversation.receiverList.splice(receiver, 1);
    }

    function getUserForID (senderID) {
      for (var i = 0; i < vm.currentConversation.receiverList.length; i++) {
        if (vm.currentConversation.receiverList[i].id == senderID) return vm.currentConversation.receiverList[i];
      };
      return $rootScope.userdata;
    }
  }
})();
