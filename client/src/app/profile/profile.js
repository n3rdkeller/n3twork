(function() {
  'use strict';

  angular
    .module('n3twork.profile', []);
})();

(function() {
  'use strict';

  angular
    .module('n3twork.profile')
    .controller('ProfileCtrl', ProfileCtrl);

  ProfileCtrl.$inject = ['APISvc', '$rootScope', '$routeParams'];

  function ProfileCtrl(APISvc, $rootScope, $routeParams) {
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
            data: { 'username': $routeParams.username }
          }).then(function (response) {
            vm.loading = false;
            if (response.data.successful) {
              delete response.data.successful;
              vm.userdata = response.data;
              getDisplayName();
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

    function getDisplayName () {
      var displayName = '';
      displayName += (vm.userdata.firstname ? vm.userdata.firstname + ' ' : '')
                    + (vm.userdata.lastname ? vm.userdata.lastname + ' ' : '')
                    + ((vm.userdata.firstname || vm.userdata.lastname) ? '( ' : '')
                    + vm.userdata.username
                    + ((vm.userdata.firstname || vm.userdata.lastname) ? ' )' : '');
      vm.displayName = displayName;
    }

  }
})();
