(function() {
  'use strict';

  angular
    .module('n3twork')
    .factory('APISvc', APISvc);

  APISvc.$inject = ['$http', '$rootScope'];
  function APISvc($http, $rootScope) {
    var factory = {
      request: request
    };

    return factory;

    function request(req) {
      var APIUrl = 'http://178.62.239.25:8080/n3';
      req.url = APIUrl + req.url;

      // if user has a session, add it to request
      if ($rootScope.userdata) {
        if ($rootScope.userdata.session) {
          req.data.session = $rootScope.userdata.session;
        }
      }

      var promise = $http(req).success(complete).error(failed);
      // console.log(req);
      return promise;
    }

    function complete(data, status, headers, config) {
      return data;
    }

    function failed(data, status, headers, config) {
      return data;
    }

  }
})();
