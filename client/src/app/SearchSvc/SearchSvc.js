(function() {
  'use strict';

  angular
    .module('n3twork')
    .service('SearchSvc', SearchSvc);

  SearchSvc.$inject = ['APISvc', '$window'];
  function SearchSvc(APISvc, $window) {
    var service = {
      getUserList: getUserList
    };
    return service;

    function getUserList() {
      APISvc.request({
        method: 'POST',
        url: '/user/find',
        data: {}
      })
      .then(function(response) {
        if (response.data.successful) {

        } else {

        }
      });
    }
  }
})();
