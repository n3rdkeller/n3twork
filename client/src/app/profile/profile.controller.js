(function() {
  'use strict';

  angular
    .module('n3twork.profile')
    .controller('ProfileCtrl', ProfileCtrl);

  ProfileCtrl.$inject = ['$rootScope', '$routeParams'];

  function ProfileCtrl($rootScope, $routeParams) {
    var vm = this;

    init();

    function init () {
      if ($routeParams.username) {
        // if it's my username
        if ($routeParams.username == $rootScope.userdata.username) {
          // it's my own profile
          vm.userdata = $rootScope.userdata;
        } else {
          vm.loading = true;
          // get userdata from API
          APISvc.request({
            method: 'POST',
            url: '/user',
            data: { 'username': username }
          }).then(function (response) {
            vm.loading = false;
            if (response.data.successful) {
              delete response.data.successful;
              vm.userdata = response.data;
            } else {
              // user doesn't exist, or whatever
              vm.doesntexist = true;
            }
          }, function (error) {
            // user doesn't exist, or whatever
            vm.doesntexist = true;
          });
        }
      } else {
        // it's my own profile
        vm.userdata = $rootScope.userdata;
      }
    }
  }
})();
