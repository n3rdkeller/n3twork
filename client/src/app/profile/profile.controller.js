(function() {
  'use strict';

  angular
    .module('n3twork.profile')
    .controller('ProfileCtrl', ProfileCtrl);

  ProfileCtrl.$inject = ['$rootScope', '$routeParams', '$location'];

  function ProfileCtrl($rootScope, $routeParams, $location) {
    var vm = this;

    init();

    function init () {
      // if username is given
      if ($routeParams.username) {
        // if it's my username
        if ($routeParams.username == $rootScope.userdata.username) {
          vm.userdata = $rootScope.userdata;
        } else {
          // get userdata from API
          APISvc.request({
            method: 'POST',
            url: '/login',
            data: { 'id': getUserID(username) }
          }).then(function (response) {
            if response.data.successful {
              delete response.data.successful;
              delete response.data.session
              vm.userdata = response.data;
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
