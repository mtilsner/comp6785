<div id="${cluster.id}_overview" class="clusterOverview">
	<cs:glower classes="['csFull']">
		<div class="clusterOverview_background" style="background-color:#${cluster.cssColor}">
			<div class="clusterOverview_topic">
				${cluster.topic} (${cluster.resultCountGuess})
			</div>
			<cs:glower id="${cluster.id}_results" classes="['clusterOverview_results']">
				<div class="clusterOverview_results_background"></div>
				<div class="clusterOverview_results_fader"></div>
				<div class="clusterOverview_results_content">
					<g:each var="result" in="${cluster.currentResults}">
						<g:render template="overviewElement" model="${['result':result,'cluster':cluster]}" />
					</g:each>
				</div>
			</cs:glower>
		</div> 
	</cs:glower>
	<div class="detailsFader" onclick="javascript:cubansea.openCluster('${cluster.id}','${cluster.topic}');"></div>
</div>