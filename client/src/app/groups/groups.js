(function() {
  'use strict';

  angular
    .module('n3twork.groups')
    .controller('GroupsCtrl', GroupsCtrl);

  GroupsCtrl.$inject = ['CacheSvc', '$routeParams'];
  function GroupsCtrl(CacheSvc, $routeParams) {
    var vm = this;

    init();

    function init() {
      vm.loadingGroups = true;
      CacheSvc.getUserData($routeParams.username).then(function (userdata) {
        vm.userdata = userdata;
        CacheSvc.getGroupListOfUser($routeParams.username).then(function (groupList) {
          vm.groupList = groupList;
          vm.loadingGroups = false;
        }, function (error) {
          vm.groupList = [];
          vm.loadingGroups = false;
        });
      }, function (error) {
        vm.doesntexist = true;
      });
    }



  }


})();
