package eu.tilsner.cubansea.api.simple;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import eu.tilsner.cubansea.api.Configuration;
import eu.tilsner.cubansea.cluster.ClusteringAlgorithm;
import eu.tilsner.cubansea.cluster.simplefuzzykmeans.SimpleFuzzyKMeansClusteringAlgorithm;
import eu.tilsner.cubansea.prepare.PreparationAlgorithm;
import eu.tilsner.cubansea.prepare.pottersummary.PotterSummaryPreparationAlgorithm;
import eu.tilsner.cubansea.search.SearchEngine;
import eu.tilsner.cubansea.search.yahoo.YahooSearchEngine;

/**
 * A simple implementation of the configuration interface.
 * 
 * @author Matthias Tilsner
 */
public class SimpleConfiguration implements Configuration {

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.api.Configuration#getBlockSize()
	 */
	@Override
	public int getBlockSize() {
		return 25;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.api.Configuration#getClusterColors()
	 */
	@Override
	public List<Color> getClusterColors() {
		List<Color> _colors = new ArrayList<Color>();
		_colors.add(Color.RED);
		_colors.add(Color.BLUE);
		_colors.add(Color.GREEN);
		return _colors;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.api.Configuration#getClusteringAlgorithm()
	 */
	@Override
	public ClusteringAlgorithm getClusteringAlgorithm() {
		return new SimpleFuzzyKMeansClusteringAlgorithm(1.0);
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.api.Configuration#getClusteringBase()
	 */
	@Override
	public int getClusteringBase() {
		return 100;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.api.Configuration#getNumberOfClusters()
	 */
	@Override
	public int getNumberOfClusters() {
		return 4;
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.api.Configuration#getPrepareAlgorithm()
	 */
	@Override
	public PreparationAlgorithm getPrepareAlgorithm() {
		return new PotterSummaryPreparationAlgorithm();
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.api.Configuration#getSearchEngine()
	 */
	@Override
	public SearchEngine getSearchEngine() {
		return new YahooSearchEngine();
	}

	/* (non-Javadoc)
	 * @see eu.tilsner.cubansea.api.Configuration#getTopicSize()
	 */
	@Override
	public int getTopicSize() {
		return 3;
	}

}
