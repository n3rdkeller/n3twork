(function() {
  'use strict';

  angular
    .module('n3twork.search')
    .controller('SearchCtrl', SearchCtrl);

  SearchCtrl.$inject = ['SearchSvc'];
  function SearchCtrl(SearchSvc) {
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
          SearchSvc.getUserList().then(function (response) {
            vm.userlist = response.userList;
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
      SearchSvc.getGroupList().then(function (response) {
        vm.grouplist = response.groupList;
        vm.secondLoading = false;
      });
    }
  }
})();
