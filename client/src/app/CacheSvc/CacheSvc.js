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

    function getUserData(username) {
      var deferred = $q.defer();
      var sessionData = getSessionData(username, 'userData');
      if (username) {
        // if it's my username
        if (username == $rootScope.userdata.username) {
          // it's my own profile
          deferred.resolve($rootScope.userdata);
        } else {
          getUserDataFromAPI().then(function (userdata) {
            deferred.resolve(userdata);
          }, function (error) {
            deferred.reject(error);
          });
        }
      } else {
        // it's my own profile
        deferred.resolve($rootScope.userdata);
      }

      return deferred.promise;
    }

    function getUserDataFromAPI() {
      var deferred = $q.defer();
      // get userdata from API
      APISvc.request({
        method: 'POST',
        url: '/user',
        data: { 'username': $routeParams.username }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data);
        } else {
          deferred.reject(response.data.successful);
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
        deferred.resolve({ userList: sessionData.userList });
      } else {
        APISvc.request({
          method: 'POST',
          url: '/user/find',
          data: {}
        }).then(function (response) {
          setSessionData('global', 'userList', response.data.userList);
          deferred.resolve({ userList: response.data.userList });
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
        deferred.resolve({ groupList: sessionData.groupList });
      } else {
        APISvc.request({
          method: 'POST',
          url: '/group/find',
          data: {}
        }).then(function (response) {
          setSessionData('global', 'groupList', response.data.groupList);
          deferred.resolve({ 'groupList': response.data.groupList });
        }, function (error) {
          deferred.reject(error);
        });
      }
      return deferred.promise;
    }

    function getGroupListOfUser(username) {
      var deferred = $q.defer();
      // get groupList from API
      APISvc.request({
        method: 'POST',
        url: '/user/groups',
        data: { 'username': username }
      }).then(function (response) {
        if (response.data.successful) {
          deferred.resolve(response.data.groupList);
        } else {
          deferred.reject(response.data.successful);
        }
      }, function (error) {
        deferred.reject(error);
      });

      return deferred.promise;
    }



    // true if userList, false if groupList
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
          return parsed;
        }
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
      return new Date(new Date().getTime() + 1 * 60000);
    }

  }
})();
