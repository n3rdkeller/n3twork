(function() {
    'use strict';

    angular
        .module('n3twork')
        .factory('APISvc', APISvc);

    APISvc.$inject = ['$http', '$q'];

    function APISvc($http) {
        var service = {
            doRequest: doRequest
        };
        return service;

        function doRequest(req) {
          req = req ? req : '';
          $http.get('http://n3twork.n3rdkeller.de:8080/n3/' + req)
            .success(function(data) {
              vm.response = data;
            });
        }
    }
})();
