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

    function loadSearchList(valid, searchString) {
      vm.firstLoading = true;
      loadUserList(searchString);
      vm.secondLoading = true;
      loadGroupList(searchString);
      vm.searchString = "";
    }

    function loadUserList(searchString) {
      if (searchString) {
        if (searchString.length > 2) {
          SearchSvc.getUserList().then(function (response) {
            vm.userlist = response.userList;
            vm.submittedSearchString = searchString;
            vm.firstLoading = false;
          });
        } else {
          vm.userlist = [];
          vm.submittedSearchString = searchString;
          vm.firstLoading = false;
        }
      } else {
        vm.userlist = [];
        vm.submittedSearchString = searchString;
        vm.firstLoading = false;
      }
    }

    function loadGroupList(searchString) {
      SearchSvc.getGroupList().then(function (response) {
        vm.grouplist = response.groupList;
        vm.submittedSearchString = searchString;
        vm.secondLoading = false;
      });
    }
  }
})();
