(function() {
    'use strict';

    angular
        .module('n3twork.hw')
        .controller('HwController', HwController);

    HwController.$inject = ['APISvc', '$q'];

    function HwController(APISvc, $q) {
        var vm = this;
        var deferred = $q.defer();

        getData();

        function getData() {
            APISvc.request({
              method: 'GET',
              url: ''
            }).then(function(data) {
                deferred.resolve(true);
                vm.d = data;
            })
        }
    }
})();
