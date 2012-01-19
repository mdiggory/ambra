dependencies = {
  layers: [
    {
      name: "ambra.js",
      layerDependencies: [
      ],
      dependencies: [
        "dojo.cookie",
        "dojo.io.script",
        "dojox.fx",
        "dojox.gfx.fx",
        "dojox.gfx.svg",
        "dojox.gfx.shape",
        "dojox.gfx.path",
        "dojox.charting.Chart2D",
        "dojox.charting.plot2d.Lines",
        "dojox.charting.action2d.Magnify",
        "dojox.charting.action2d.Tooltip",
        "dojox.charting.themes.Grasshopper",
        "dojox.charting.plot2d.Grid",
        "dijit.layout.ContentPane",
        "dijit.Dialog",
        "dijit.layout.TabContainer",
        "dojox.data.dom",
        "ambra.general",
        "ambra.domUtil",
        "ambra.htmlUtil",
        "ambra.formUtil",
        "ambra.widget.LoadingCycle",
        "ambra.widget.RegionalDialog",
        "ambra.navigation",
        "ambra.horizontalTabs",
        "ambra.floatMenu",
        "ambra.annotation",
        "ambra.corrections",
        "ambra.displayAnnotationContext",
        "ambra.displayComment",
        "ambra.responsePanel",
        "ambra.rating",
        "ambra.slideshow"
      ]
    }
  ],

  prefixes: [
    [ "dijit", "../dijit" ],
    [ "dojox", "../dojox" ],
    [ "ambra", "../../../src/main/scripts/ambra" ]
  ]
}