(function() {
    'use strict';

    angular
        .module('n3twork.hw')
        .controller('HwController', HwController);

    function HwController(APISvc) {
        var vm = this;

        getData();

        function getData() {
          return APISvc.doRequest();
        }
    }
})();
