(function() {
  'use strict';

  angular
    .module('n3twork.settings', []);
})();

(function() {
  'use strict';

  angular
    .module('n3twork.settings')
    .controller('SettingsCtrl', SettingsCtrl);

  SettingsCtrl.$inject = ['APISvc', 'UserSvc', '$rootScope'];
  function SettingsCtrl(APISvc, UserSvc, $rootScope) {
    var vm = this;

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
          method: 'PUT',
          url: '/user/settings',
          data: changedData()
        })
        .then(function (response) {
          if (response.data.successful) {
            $rootScope.userdata = merge($rootScope.userdata, changedData());
            if (UserSvc.setUserData($rootScope.userdata)) {
              vm.loading = false;
              resetForm();
            }
            vm.successful = true;
          } else {
            vm.successful = false;
            vm.loading = false;
          }
        }, function (error) {
          vm.successful = false;
          vm.loading = false;
        });
      } else {
        vm.successful = false;
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
      return !isEmpty(changedData());
    }

    function changedData() {
      var dataThatHasChanged = {};
      // is firstName given?
      if (vm.user.firstName != $rootScope.userdata.firstName) { dataThatHasChanged.firstName = vm.user.firstName };
      // are first- and lastName given?
      if (vm.user.firstName && (vm.user.lastName != $rootScope.userdata.lastName)) { dataThatHasChanged.lastName = vm.user.lastName };
      // is email given?
      if (vm.user.email && (vm.user.email != $rootScope.userdata.email)) { dataThatHasChanged.email = vm.user.email };
      // is password given?
      if (vm.user.pw && (vm.user.pw == vm.user.pwconfirm)) { dataThatHasChanged.password = vm.user.pw };

      // otherProperties
      dataThatHasChanged.otherProperties = {};
      // city
      if (vm.user.city && (vm.user.city != $rootScope.userdata.otherProperties.city)) { dataThatHasChanged.otherProperties.city = vm.user.city };

      if (isEmpty(dataThatHasChanged.otherProperties)) {
        delete dataThatHasChanged.otherProperties;
      }

      // return object with the data that has changed
      return dataThatHasChanged;
    }

    function isEmpty (obj) {
      for (var data in obj) {
        return false;
      }
      return true;
    }


    function resetForm() {
      //vm.settingsForm.$setPristine();
      $rootScope.$broadcast('show-errors-reset');

      // required fields
      vm.user.pw = "";
      vm.user.pwconfirm = "";
      vm.user.email = $rootScope.userdata.email;


      // optional fields
      if ($rootScope.userdata.firstName) {
        vm.user.firstName = $rootScope.userdata.firstName
      } else {
        vm.user.firstName = "";
      }
      if ($rootScope.userdata.lastName && $rootScope.userdata.firstName) {
        vm.user.lastName = $rootScope.userdata.lastName
      } else {
        vm.user.lastName = "";
      }

      // otherProperties
      if ($rootScope.userdata.otherProperties.city) {
        vm.user.city = $rootScope.userdata.otherProperties.city
      } else {
        vm.user.city = "";
      }
    }

    function deleteUser(doubleChecked) {
      if (doubleChecked) {
        vm.deleteLoading = true;
        APISvc.request({
          method: 'POST',
          url: '/user/remove',
          data: {}
        })
        .then(function (response) {
          if (response.data.successful) {
            vm.deleteLoading = false;
            vm.successfullyDeleted = true;
            UserSvc.localLogout();
          }
        }, function (error) {
          vm.deleteLoading = false;
          vm.successfullyDeleted = false;
        });
      } else {
        console.log('You didn\'t double check. Nasty boy.');
      }
    }

  }
})();
