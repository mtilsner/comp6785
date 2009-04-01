<div class="cluster_topbar">
	<span class="filter_message">search results:</span>
	<input type="text" name="filter" id="${cluster.id}_filter" class="filter" />
	<input type="checkbox" name="summary" valign="bottom" id="${cluster.id}_filter_summary" class="filter_summary" />
	<span class="filter_message filter_summary_message">include summary in search</span>
</div>
<div class="cluster_scroll">
	<div id="${cluster.id}_resultList" class="cluster_resultList" clusterId="${cluster.id}">
		<g:each var="result" in="${cluster.currentResults}">
			<g:render template="element" model="${['result':result,'cluster':cluster,'terms':terms]}" />
		</g:each>		
	</div>
</div>
<div class="cluster_background" style="background-color:#${cluster.cssColor};"></div>