(function() {
  'use strict';

  angular
    .module('n3twork')
    .controller('ShowVotesCtrl', ShowVotesCtrl);

  ShowVotesCtrl.$inject = ['getPostID', 'APISvc', '$modalInstance'];
  function ShowVotesCtrl(getPostID, APISvc, $modalInstance) {
    var vm = this;

    vm.postID = getPostID;
    vm.dismiss = dismiss;

    init();

    function init() {
      vm.loading = true;
      APISvc.request({
        method: 'GET',
        url: '/post/votes',
        data: { 'id': vm.postID }
      }).then(function (response) {
        vm.loading = false;
        vm.voteList = response.data.voteList;
      }, function (error) {
        vm.loading = false;
      });
    }

    function dismiss () {
      $modalInstance.close();
    }

  }
})();
