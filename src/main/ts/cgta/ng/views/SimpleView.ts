//////////////////////////////////////////////////////////////
// Copyright (c) 2013 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 11/20/13 12:00 AM
//////////////////////////////////////////////////////////////

module Cgta.Views {
  export module SimpleView {
    var mod = angular.module("views.SimpleView", []);
    import CharacterInfo = Cgta.Services.CharacterInfo
    import FlatItem = Cgta.Services.FlatItem
    import ModParsers = Cgta.Services.ModParsers
    import ModParser = ModParsers.ModParser

    export class SimpleCtrl {
      stuff = "Nothing to see here"

      constructor($gameStateService:Cgta.Services.GameStateService) {

        $gameStateService.loadAllFromStorage()
          .then(() => $gameStateService.downloadCharacters())
          .then(() => $gameStateService.loopDownloadInventories())
          .then(() => $gameStateService.loopDownloadStashTabs())
          .finally(() => console.log("Characters", $gameStateService.getCharacters()))
          .finally(() => console.log("Inventories", $gameStateService.getInventories()))
          .finally(() => console.log("Stash Tabsxx", $gameStateService.getStashTabs()))
          .finally(() => $gameStateService.reFlattenAll())
          .done(() => render($gameStateService.getFlatItems()))


        function render(items:Array<FlatItem>) {
          var columns = ModParsers.all.map(function (mp:ModParser) {
            return {id: mp.name, name: mp.title, field: mp.name, toolTip: mp.name, sortable: true}
          })

          var options = {
            enableCellNavigation: true,
            enableColumnReorder: false,
            multiColumnSort: true
          };

          var grid = new Slick.Grid("#myGrid", items, columns, options)
          grid.render();

          grid.onSort.subscribe(function (e:any, args:any) {
            var cols = args.sortCols;

            items.sort(function (dataRow1:any, dataRow2:any) {
              for (var i = 0, l = cols.length; i < l; i++) {
                var field = cols[i].sortCol.field;
                var sign = cols[i].sortAsc ? 1 : -1;
                var value1 = dataRow1[field] || 0
                var value2 = dataRow2[field] || 0
                if (value1 != null && value2 != null) {
                  var result = (value1 == value2 ? 0 : (value1 > value2 ? 1 : -1)) * sign;
                  if (result != 0) {
                    return result;
                  }
                }
              }
              return 0;
            });
            grid.invalidate();
            grid.render();
          });

          function resize() {
            $('#myGrid').css('height', window.innerHeight - 50);
            grid.resizeCanvas();
          }

          resize();

          $(window).resize(function () {
            resize()
          });

        }

      }

    }

    mod.controller("SimpleCtrl", SimpleCtrl)

  }
}