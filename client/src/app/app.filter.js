(function() {
  'use strict';

  angular
    .module('n3twork')
    .filter('capitalize', capitalize);

    function capitalize() {
      return function (input, all) {
        return (!!input) ? input.replace(/([^\W_]+[^\s-]*) */g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();}) : '';
      };
    }
})();

(function() {
  'use strict';

  angular
    .module('n3twork')
    .filter('symbolForKey', symbolForKey);

    function symbolForKey() {
      return function (input) {
        var symbol = '';
        switch (input) {
          case 'city':
            symbol = 'location-arrow';
            break;
          default:
            symbol = 'cog';
            break;
        }
        return symbol;
      };
    }
})();

(function() {
  'use strict';

  angular
    .module('n3twork')
    .filter('isEmpty', isEmpty);

    function isEmpty() {
      return function (object) {
        for (var key in object) {
          if (object.hasOwnProperty(key)) {
            return false;
          }
        }
        return true;
      };
    }
})();

(function() {
  'use strict';

  angular
    .module('n3twork')
    .filter('md5Hash', md5Hash);

    function md5Hash() {
      return function (value) {
        return md5(value);
      };
    }
})();
