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
          case 'mail':
            symbol = 'envelope-o';
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

(function() {
  'use strict';

  angular
    .module('n3twork')
    .filter('parseDescription', ['$sce', parseDescription]);

    function parseDescription($sce) {
      return function (description) {
        if (description) {
          // remove script-tags
          description = description.replace(/<script.*>.*<\/script>/igm, '');
          // make images in image-tags fit
          // first, remove from style-tag
          // console.log(description);
          // description = description.replace(/(<img.*)(width(?::|=")\s*\d+\w{,3}(?:;|"))[^>]*>/ig, '$1>');
          // description = description.replace(/(<img.*)(width(?::|=")\s*\d+\w{,3}(?:;|"))[^>]*>/ig, '$1width="250">');
          // console.log(description);

          // build DOM element
          var el = document.createElement('div');
          el.innerHTML = description;

          var elements = el.getElementsByTagName("*");

          for (var i = 0; i < elements.length; i++) {
            var img = elements[i].querySelector('img');
            if (img) {
              if (img.width > 250) {
                img.style.width = '';
                img.removeAttribute('width');
                img.width = 250;
              }
            }
          }
          description = el.innerHTML;
          // replace newLines
          description = description.replace(/\n/g, '<br>');
          // make it safe
          description = $sce.trustAsHtml(description);
          return description;
        } else { return "" };
      };
    }
})();


(function() {
  'use strict';

  angular
    .module('n3twork')
    .filter('shortenDescription', ['$sce', shortenDescription]);

    function shortenDescription($sce) {
      return function (description) {
        if (description) {
          var maxlength = 140;
          // remove script-tags
          description = description.replace(/<script.*>.*<\/script>/igm, '');
          // remove a-tags
          description = description.replace(/<a .*>.*<\/a>/igm, '');
          // remove img-tags
          description = description.replace(/<img .*>.*<\/img>/igm, '');
          // remove span-tags
          description = description.replace(/<span .*>.*<\/span>/igm, '');
          // remove trailing newLines
          description = description.replace(/\n*/, '');
          // replace newLines
          description = description.replace(/\n/igm, '<br>');

          // shorten description and add '...' if necessary
          if (description.length > maxlength) {
            description = description.substr(0, maxlength - 1);
            description += ' ...';
          }

          // make it safe
          description = $sce.trustAsHtml(description);
          return description;
        } else { return "" };
      };
    }
})();
