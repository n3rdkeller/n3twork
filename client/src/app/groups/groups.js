(function() {
  'use strict';

  angular
    .module('n3twork.groups')
    .controller('GroupsCtrl', GroupsCtrl);

  GroupsCtrl.$inject = ['APISvc', '$routeParams', '$q', '$rootScope'];
  function GroupsCtrl(APISvc, $routeParams, $q, $rootScope) {
    var vm = this;

    init();

    function init() {
      getUserData().then(function (userdata) {
        vm.userdata = userdata;
        getGroupList().then(function (groupList) {
          vm.groupList = groupList;
        }, function (error) {
          vm.groupList = [];
        });
      }, function (error) {
        vm.doesntexist = true;
      });
    }

    function getUserData() {
      var deferred = $q.defer();
      vm.loadingGroups = true;
      if ($routeParams.username) {
        // if it's my username
        if ($routeParams.username == $rootScope.userdata.username) {
          // it's my own profile
          deferred.resolve($rootScope.userdata);
          vm.itsMe = true;
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
        vm.itsMe = true;
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

    function getGroupList() {
      var deferred = $q.defer();
      // get groupList from API
      APISvc.request({
        method: 'POST',
        url: '/user/groups',
        data: { 'username': vm.userdata.username }
      }).then(function (response) {
        vm.loadingGroups = false;
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


  }


})();
