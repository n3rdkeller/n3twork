(function() {
  'use strict';

  angular
    .module('n3twork.settings')
    .controller('SettingsCtrl', SettingsCtrl);

  SettingsCtrl.$inject = ['APISvc', 'UserSvc', '$rootScope', '$q'];
  function SettingsCtrl(APISvc, UserSvc, $rootScope, $q) {
    var vm = this;
    var deferred = $q.defer();

    // methods
    vm.submit = submit;
    vm.somethingChanged = somethingChanged;
    vm.resetForm = resetForm;
    vm.deleteUser = deleteUser;

    // loading state
    vm.loading = false;
    vm.deleteLoading = false;

    function submit(isValid) {
      if (isValid) {
        vm.loading = true;
        APISvc.request({
          method: 'POST',
          url: '/user/settings',
          data: changedData()
        })
        .then(function(response) {
          deferred.resolve(true);
          if (response.data.successful) {
            $rootScope.userdata = merge($rootScope.userdata, changedData());
            if (UserSvc.setUserData()) {
              resetForm();
              vm.loading = false;
            }
            vm.successful = true;
          } else {
            vm.successful = false;
            vm.loading = false;
            console.log('Damn idiot, you did something terribly wrong.');
          }
        });
      } else {
        console.log('Something in the settings form is still invalid.');
      }
    }

    function merge(firstObject, secondObject) {
      for (var key in secondObject) {
        firstObject[key] = secondObject[key];
      }
      return firstObject;
    }

    function somethingChanged() {
      // if something changed (means there's data in the object)
      // return true, else return false
      for (var data in changedData()) {
        return true;
      }
      return false;
    }

    function changedData() {
      var dataThatHasChanged = {};
      // is firstname given?
      if (vm.user.firstname && (vm.user.firstname != $rootScope.userdata.firstname)) { dataThatHasChanged.firstname = vm.user.firstname };
      // are first- and lastname given?
      if (vm.user.lastname && vm.user.firstname && (vm.user.lastname != $rootScope.userdata.lastname)) { dataThatHasChanged.lastname = vm.user.lastname };
      // is email given?
      if (vm.user.email && (vm.user.email != $rootScope.userdata.email)) { dataThatHasChanged.email = vm.user.email };
      // is password given?
      if (vm.user.pw && (vm.user.pw == vm.user.pwconfirm)) { dataThatHasChanged.pw = vm.user.pw };
      // return object with the data that has changed
      return dataThatHasChanged;
    }

    function resetForm(theForm) {
      if (theForm) { theForm.$setPristine(); };
      $rootScope.$broadcast('show-errors-reset');

      if ($rootScope.userdata.firstname) {
        vm.user.firstname = $rootScope.userdata.firstname
      } else {
        vm.user.firstname = "";
      }
      if ($rootScope.userdata.lastname) {
        vm.user.lastname = $rootScope.userdata.lastname
      } else {
        vm.user.lastname = "";
      }
      vm.user.pw = "";
      vm.user.pwconfirm = "";
      vm.user.email = $rootScope.userdata.email;
    }

    function deleteUser(doubleChecked) {
      if (doubleChecked) {
        vm.deleteLoading = true;
        APISvc.request({
          method: 'POST',
          url: '/user/remove',
          data: {}
        })
        .then(function(response) {
          deferred.resolve(true);
          if (response.data.successful) {
            vm.deleteLoading = false;
            vm.successfullyDeleted = true;
            UserSvc.localLogout();
          }
        });
      } else {
        console.log('You didn\'t double check. Nasty boy.');
      }
    }

  }
})();
