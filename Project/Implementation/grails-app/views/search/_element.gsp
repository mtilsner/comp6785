<div id="${result.id}" class="result" resultTitle="${result.title.encodeAsHTML()}" resultSummary="${result.summary.encodeAsHTML()}" resultUrl="${result.url}">
	<div class="relevanceSquare_Background" style="background-color:#${cluster.cssColor}; opacity:${Math.pow(result.getRelevance(cluster),2)};"></div>
	<div id="${result.id}_relevanceSquare" class="relevanceSquare ${(result.getRank(cluster) > 5) ? "collapsed" : "expanded"}"" title="Similarity: ${Math.round(result.getRelevance(cluster)*100)}%" onclick="javascript:cubansea.toggleSummary('${result.id}')"></div>
	<a href="${result.url}" title="${result.summary.encodeAsHTML()}" class="resultHeader">
		${result.originalRank}: <cs:wordCutter content="${result.title}" length="50" />
	</a><br />
	<div id="${result.id}_summary" class="resultSummary"${(result.getRank(cluster) > 5) ? """ style="display:none;" """ : ""}>
		<cs:termHighlighter content="${result.summary}" terms="${terms}" /><br />
		<a href="${result.url}" style="color:#${cs.linkColor(color:cluster.color)};" class="resultLink">${result.url}</a>
	</div>
</div>