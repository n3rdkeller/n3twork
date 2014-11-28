(function() {
  'use strict';

  angular
    .module('n3twork')
    .service('CacheSvc', CacheSvc);

  CacheSvc.$inject = ['APISvc', '$window', '$q'];
  function CacheSvc(APISvc, $window, $q) {
    var service = {
      getUserList: getUserList,
      getGroupList: getGroupList
    };
    return service;

    function getUserList() {
      var deferred = $q.defer();
      var sessionData = getSessionData(true);
      if (sessionData) {
        deferred.resolve({ userList: sessionData });
      } else {
        APISvc.request({
          method: 'POST',
          url: '/user/find',
          data: {}
        }).then(function (response) {
          setSessionData(response.data);
          deferred.resolve(response.data);
        }, function (error) {
          deferred.reject(error);
        });
      }
      return deferred.promise;
    }

    function getGroupList() {
      var deferred = $q.defer();
      var sessionData = getSessionData(false);
      if (sessionData) {
        deferred.resolve({ groupList: sessionData });
      } else {
        APISvc.request({
          method: 'POST',
          url: '/group/find',
          data: {}
        }).then(function (response) {
          setSessionData(response.data);
          deferred.resolve({ groupList: response.data.groupList });
        }, function (error) {
          deferred.reject(error);
        });
      }
      return deferred.promise;
    }

    // true if userList, false if groupList
    function getSessionData(userOrGroup) {
      var sessionData = $window.sessionStorage.getItem(userOrGroupToString(userOrGroup));

      // if no sessionData is there, return false
      if (sessionData == null) {
        return false;
      } else {
        // parse data from sessionStorage
        var parsed = JSON.parse(sessionData);
        // if userList
        if (userOrGroup) {
          // if users.expirationTime is expired
          if (expired(parsed.expirationTime)) {
            return false;
          } else {
            return parsed.userList;
          }
        // if groupList
        } else {
          // if users.expirationTime is expired
          if (expired(parsed.expirationTime)) {
            return false;
          } else {
            return parsed.groupList;
          }
        }
      }
    }

    function userOrGroupToString (userOrGroup) {
      if (userOrGroup) {
        return 'userList';
      } else {
        return 'groupList';
      }
    }

    function setSessionData(data) {
      if (data.userList) {
        $window.sessionStorage.setItem('userList', JSON.stringify({
          expirationTime: expirationTime(),
          userList: data.userList
        }));
      } else if (data.groupList) {
        $window.sessionStorage.setItem('groupList', JSON.stringify({
          expirationTime: expirationTime(),
          groupList: data.groupList
        }));
      }
    }

    function expired(expirationTime) {
      if (new Date(expirationTime).getTime() > (new Date().getTime())) {
        return false;
      } else {
        return true;
      }
    }

    function expirationTime() {
      // return Date object containing current time plus 2 minutes
      return new Date(new Date().getTime() + 2 * 60000);
    }

  }
})();
