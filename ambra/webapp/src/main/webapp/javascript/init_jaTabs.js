function jatwResize() {
  var tw = dojo.byId('jatw');

  dojo.query('.dijitTabPane', tw).forEach( function(tabPane) {
    if (tabPane.className.indexOf("dijitVisible") > 0) {
      tabPane.style.height = '';
      var mb = dojo.marginBox(tabPane);

      mb.h += 50;
      mb.w = 892;

      dijit.byId('jatw').resize(mb);
      tabPane.style.overflow = 'hidden';
    }
  });
}

dojo.addOnLoad( function() {
  var jatw = dojo.byId('jatw');
  jatw.style.visibility = 'visible';
  dojo.connect(jatw, 'onclick', function(e) {
    jatwResize();
    return false;
  });
  jatwResize();
});
