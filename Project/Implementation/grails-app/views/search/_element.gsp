<div id="${result.id}" class="result" resultTitle="${result.title}" resultSummary="${result.summary}" resultUrl="${result.url}">
	<div id="${result.id}_relevanceSquare" class="relevanceSquare ${(result.getRank(cluster) > 5) ? "collapsed" : "expanded"}" style="background-color:#${result.getCssColor(cluster)};" title="Similarity: ${Math.round(result.getRelevance(cluster)*100)}%" onclick="javascript:cubansea.toggleSummary('${result.id}')"></div>
	<a href="${result.url}" title="${result.summary}" class="resultHeader">
		${result.originalRank}: <cs:wordCutter content="${result.title}" length="40" />
	</a><br />
	<div id="${result.id}_summary" class="resultSummary"${(result.getRank(cluster) > 5) ? """ style="display:none;" """ : ""}>
		<cs:termHighlighter content="${result.summary}" terms="${terms}" /><br />
		<a href="${result.url}" style="color:#${cs.linkColor(color:cluster.color)};" class="resultLink">${result.url}</a>
	</div>
</div>