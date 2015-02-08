(function() {
  'use strict';

  angular
    .module('n3twork')
    .service('CacheSvc', CacheSvc);

  CacheSvc.$inject = ['APISvc', '$window', '$rootScope', '$q'];
  function CacheSvc(APISvc, $window, $rootScope, $q) {
    var service = {
      getUserData: getUserData,
      getUserList: getUserList,
      getGroupList: getGroupList,
      getGroupListOfUser: getGroupListOfUser,
      getFriendListOfUser: getFriendListOfUser,
      checkIfFriend: checkIfFriend,
      getFriendRequests: getFriendRequests,
      removeFriendCache: removeFriendCache,
      removeGroupCache: removeGroupCache
    };
    return service;

    function getUserData(username) {
      var deferred = $q.defer();
      var sessionData = getSessionData(username, 'userData');
      if (sessionData) {
        deferred.resolve(sessionData);
      } else {
        if (username) {
          // if it's my username
          if (username == $rootScope.userdata.username) {
            // it's my own profile
            deferred.resolve($rootScope.userdata);
          } else {
            getUserDataFromAPI(username).then(function (userdata) {
              setSessionData(username, 'userData', userdata);
              deferred.resolve(userdata);
            }, function (error) {
              deferred.reject(error);
            });
          }
        } else {
          // it's my own profile
          deferred.resolve($rootScope.userdata);
        }
      }
      return deferred.promise;
    }

    function getUserDataFromAPI(username) {
      var deferred = $q.defer();
      // get userdata from API
      APISvc.request({
        method: 'POST',
        url: '/user',
        data: { 'username': username },
        ignoreLoadingBar: true
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data);
        } else {
          deferred.reject(response.data);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }

    function getUserList() {
      var deferred = $q.defer();
      var sessionData = getSessionData('global', 'userList');
      if (sessionData) {
        deferred.resolve(sessionData);
      } else {
        APISvc.request({
          method: 'POST',
          url: '/user/find',
          data: {}
        }).then(function (response) {
          setSessionData('global', 'userList', response.data.userList);
          deferred.resolve(response.data.userList);
        }, function (error) {
          deferred.reject(error);
        });
      }
      return deferred.promise;
    }

    function getGroupList() {
      var deferred = $q.defer();
      var sessionData = getSessionData('global', 'groupList');
      if (sessionData) {
        deferred.resolve(sessionData);
      } else {
        APISvc.request({
          method: 'POST',
          url: '/group/find',
          data: {}
        }).then(function (response) {
          setSessionData('global', 'groupList', response.data.groupList);
          deferred.resolve(response.data.groupList);
        }, function (error) {
          deferred.reject(error);
        });
      }
      return deferred.promise;
    }

    function getGroupListOfUser(username) {
      var deferred = $q.defer();
      var sessionData = getSessionData(username, 'groupList');
      if (sessionData) {
        deferred.resolve(sessionData);
      } else {
        // get groupList from API
        APISvc.request({
          method: 'POST',
          url: '/user/groups',
          data: { 'username': username }
        }).then(function (response) {
          if (response.data.successful) {
            setSessionData(username, 'groupList', response.data.groupList);
            deferred.resolve(response.data.groupList);
          } else {
            deferred.reject(response.data.successful);
          }
        }, function (error) {
          deferred.reject(error);
        });
      }

      return deferred.promise;
    }


    function getFriendListOfUser(id) {
      var deferred = $q.defer();
      var sessionData = getSessionData(id, 'friendList');

      if (sessionData) {
        deferred.resolve(sessionData);
      } else {
        // get friendList from API
        APISvc.request({
          method: 'POST',
          url: '/user/friends',
          data: { 'id': id },
          ignoreLoadingBar: true
        }).then(function (response) {
          if (response.data.successful) {
            setSessionData(id, 'friendList', response.data.friendList);
            deferred.resolve(response.data.friendList);
          } else {
            deferred.reject(response.data.successful);
          }
        }, function (error) {
          deferred.reject(error);
        });
      }

      return deferred.promise;
    }

    function checkIfFriend(id) {
      var deferred = $q.defer();
      // get OWN friendList from API
      APISvc.request({
        method: 'POST',
        url: '/user/friends',
        data: { }
      }).then(function (response) {
        if (response.data.successful) {
          var trueFriend = false;
          var isFriend = false;
          for (var i = 0; i < response.data.friendList.length; i++) {
            if (response.data.friendList[i].id == id) {
              isFriend = true;
              trueFriend = response.data.friendList[i].trueFriend;
            }
          }
          deferred.resolve([isFriend, trueFriend]);
        } else {
          deferred.reject([response.data.successful]);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }


    function getFriendRequests() {
      var deferred = $q.defer();
      var sessionData = getSessionData('my', 'friendRequestList');

      if (sessionData) {
        deferred.resolve(sessionData);
      } else {
        // get friendList from API
        APISvc.request({
          method: 'POST',
          url: '/user/friendrequests',
          data: { }
        }).then(function (response) {
          if (response.data.successful) {
            setSessionData('my', 'friendRequestList', response.data.friendRequests);
            deferred.resolve(response.data.friendRequests);
          } else {
            deferred.reject(response.data.successful);
          }
        }, function (error) {
          deferred.reject(error);
        });
      }

      return deferred.promise;
    }

    function removeFriendCache() {
      removeSessionData($rootScope.userdata.id, 'friendList');
      removeSessionData('my', 'friendRequestList');
    }

    function removeGroupCache() {
      removeSessionData($rootScope.userdata.username, 'groupList');
    }

    function getSessionData(key, whichData) {
      var sessionData = $window.sessionStorage.getItem(JSON.stringify({
        'key': key,
        'whichData': whichData
      }));

      // if no sessionData is there, return false
      if (sessionData == null) {
        return false;
      } else {
        // parse data from sessionStorage
        var parsed = JSON.parse(sessionData);
        // if expirationTime is expired
        if (expired(parsed.expirationTime)) {
          return false;
        } else {
          return parsed[whichData];
        }
      }
    }

    function removeSessionData(key, whichData) {
      if (!key && !whichData) {
        $window.sessionStorage.clear();
      } else {
        $window.sessionStorage.removeItem(JSON.stringify({
          'key': key,
          'whichData': whichData
        }));
      }
    }

    function setSessionData(key, whichData, data) {
      var dataObj = {};
      dataObj['expirationTime'] = expirationTime();
      dataObj[whichData] = data;
      $window.sessionStorage.setItem(JSON.stringify({
        'key': key,
        'whichData': whichData
      }), JSON.stringify(dataObj));
    }

    function expired(expirationTime) {
      if (new Date(expirationTime).getTime() > (new Date().getTime())) {
        return false;
      } else {
        return true;
      }
    }

    function expirationTime() {
      // return Date object containing current time plus 1 minutes
      return new Date(new Date().getTime() + 30000);
    }

  }
})();
