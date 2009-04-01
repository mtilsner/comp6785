var cubansea = {
	configuration : {
		OVERVIEW_CHILDREN : 20,
		MAX_TRIES :			 5
	},
	
	try : 0,
	filterValues: {},
	windows: {},
	
	addToOverview : function(clusterid, resultList) {
		var pane = $(clusterid+"_overview").down(".clusterOverview_results_content");
		while(resultList.length > 0 && cubansea.configuration.OVERVIEW_CHILDREN > pane.childElements().length) {
			pane.insert({bottom: resultList.shift().overview});
		}
	},
	
	addToCluster : function(clusterid, resultList) {
		if(!$(clusterid)) return
		var pane = $(clusterid+"_resultList");
		while(resultList.length > 0) {
			pane.insert({bottom: resultList.shift().details});
		}
	},
	
	addResults : function(resultsObject) {
		for(var cluster in resultsObject){
			cubansea.addToOverview(cluster,$A(resultsObject[cluster]));
			if($(cluster)) cubansea.addToCluster(cluster, $(resultsObject[cluster]));
			cubansea.filter(cluster);
		}
	},
	
	openCluster: function(clusterid,clustertitle) {
		if(cubansea.windows[clusterid]) {
			cubansea.windows[clusterid].show();
			cubansea.windows[clusterid].toFront();
		} else {
			var win = new Window({
				title: clustertitle,
				top: 80,
				left: (document.viewport.getWidth()-900)/2,
				height: document.viewport.getHeight()-150,
				width: 900,
				minimizable: false,
				className: 'mac_os_x',
				effectOptions: {duration: 0.5}
			});
			win.show();
			win.setZIndex(20);
			win.toFront();
			var upb = new UndefinedProgressBar({id: clusterid+'loader', width: 300});
			win.setHTMLContent(upb.toHTML());
			var dim = win.getSize();
			$(clusterid+'loader').setStyle({position: 'absolute', left: (dim.width-300)/2+'px', top: (dim.height-25)/2+'px'});
			upb.start();
			new Ajax.Request('/CubanSea/cluster/?cluster='+clusterid, {
				method: 'get',
				onComplete : (function(window,undefinedProgressBar,request) {
					upb.stop();
					win.setHTMLContent(request.responseText);
				}).curry(win).curry(upb)
			})
			cubansea.windows[clusterid] = win;
		}
	},
	
	hideCluster: function(clusterid) {
		$(clusterid).hide();
	},
	
	fetchNext : function() {
		new Ajax.Request('/CubanSea/next', {
			method: 'get',
			onComplete: function(response) { 
				try {
					var json = response.responseText.evalJSON(true);
					if(json.status == "done")
						return;
					else
						cubansea.addResults(json);
					cubansea.try = 1;
				}catch(e) {
					cubansea.try++;
					if(cubansea.try >= cubansea.configuration.MAX_TRIES) return;
				}
				cubansea.fetchNext();
			}
		});
	},
	
	matches: function(value, whole) {
		return whole.toLowerCase().indexOf(value.toLowerCase()) != -1;
	},
	
	filter: function(clusterid) {
		var filter  		= $(clusterid+"_filter").value;
		var checkSummary	= ($(clusterid+"_filter_summary").value == "on");
		if(!cubansea.filterValues[clusterid]) cubansea.filterValues[clusterid] = {filter: "", checkSummary: false}
		if(cubansea.filterValues[clusterid].filter == filter && cubansea.filterValues[clusterid].checkSummary == checkSummary) return;
		$(clusterid+"_resultList").childElements().each(function(element){
			var title 	= element.readAttribute("resultTitle")
			var summary = element.readAttribute("resultSummary")
			var url		= element.readAttribute("resultURL");
			if(cubansea.matches(filter,title) || cubansea.matches(filter,url) || (checkSummary && cubansea.matches(filter,summary)))
				element.show();
			else
				element.hide();
		});
		cubansea.filterValues[clusterid].filter = filter;
		cubansea.filterValues[clusterid].checkSummary == checkSummary;
	},
	
	checkFilter: function(pe) {
		$$(".cluster_resultList").each(function(cluster){
			var id = cluster.readAttribute("clusterId");
			if($(id+"_filter")) cubansea.filter(id);
		});
	},
	
	toggleSummary: function(resultid) {
		$(resultid+"_summary").toggle();
		$(resultid+"_relevanceSquare").toggleClassName("expanded");
		$(resultid+"_relevanceSquare").toggleClassName("collapsed");
	}
}

Event.observe(window, "load", cubansea.fetchNext);
new PeriodicalExecuter(cubansea.checkFilter,0.25);