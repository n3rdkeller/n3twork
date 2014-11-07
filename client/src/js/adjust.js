function adjust(event) {
  var element = $(event.target);

  // pseudo-span-element tag directly after element
  var id = btoa(Math.floor(Math.random() * Math.pow(2, 64)));
  var tag = $('<span id="' + id + '">' + element.val() + '</span>').css({
        'display': 'none',
        'font-family': element.css('font-family'),
        'font-size': element.css('font-size')
    }).insertAfter(element);

  // adjust element width on keydown
  function update() {
    // give browser time to add current letter
    setTimeout(function() {
      var factor = element.width() / tag.width();
      var formersize = 14;
      var smaller = tag.width() < element.width();
      var size = smaller ? (formersize + 'px') : ((factor * formersize) + 'px');
      element.css('font-size', size);
    }, 0);
  };
  update();
};
