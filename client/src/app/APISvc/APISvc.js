(function() {
    'use strict';

    angular
        .module('n3twork')
        .factory('APISvc', APISvc);

    APISvc.$inject = ['$http', '$q'];

    function APISvc($http, $q) {
        var service = {
            request: request,
        };

        return service;

        function request(req) {
            var APIUrl = 'http://n3twork.n3rdkeller.de:8080/n3/';
            req.url = APIUrl + req.url;
            var promise = $http(req).then(complete).catch(failed);
            return promise;
        }

        function complete(response) {
            return response.data;
        }

        function failed(error) {
            console.log(error);
        }
    }
})();
