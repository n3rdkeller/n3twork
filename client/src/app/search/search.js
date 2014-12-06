(function() {
  'use strict';

  angular
    .module('n3twork.search', []);
})();

(function() {
  'use strict';

  angular
    .module('n3twork.search')
    .controller('SearchCtrl', SearchCtrl);

  SearchCtrl.$inject = ['CacheSvc'];
  function SearchCtrl(CacheSvc) {
    var vm = this;

    // functions
    vm.loadSearchList = loadSearchList;
    vm.loadUserList = loadUserList;

    vm.submittedSearchString = '';
    vm.firstSearch = true;

    function loadSearchList(valid, searchString) {
      vm.firstSearch = false;
      vm.firstLoading = true;
      loadUserList(searchString);
      vm.secondLoading = true;
      loadGroupList(searchString);
      vm.searchString = '';
    }

    function loadUserList(searchString) {
      vm.submittedSearchString = searchString;
      if (searchString) {
        if (searchString.length > 2) {
          CacheSvc.getUserList().then(function (userList) {
            vm.userlist = userList;
            vm.firstLoading = false;
          });
        } else {
          vm.userlist = [];
          vm.firstLoading = false;
        }
      } else {
        vm.userlist = [];
        vm.firstLoading = false;
      }
    }

    function loadGroupList(searchString) {
      vm.submittedSearchString = searchString;
      CacheSvc.getGroupList().then(function (groupList) {
        vm.grouplist = groupList;
        vm.secondLoading = false;
      });
    }
  }
})();
