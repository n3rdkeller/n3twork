(function() {
  'use strict';

  angular
    .module('n3twork')
    .factory('APISvc', APISvc);

  APISvc.$inject = ['$http', '$q'];

  function APISvc($http, $q) {
    var service = {
      request: request
    };

    return service;

    function request(req) {
      var APIUrl = 'http://178.62.239.25:8080/n3';
      req.url = APIUrl + req.url;
      var promise = $http(req).then(complete).catch(failed);
      // console.log(req);
      return promise;
    }

    function complete(response) {
      // console.log(response);
      return response;
    }

    function failed(error) {
      console.log(error);
    }

  }
})();
