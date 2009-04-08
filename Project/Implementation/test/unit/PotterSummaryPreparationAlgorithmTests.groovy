import eu.tilsner.cubansea.prepare.PreparedResult
import eu.tilsner.cubansea.prepare.pottersummary.PotterSummaryPreparationAlgorithm
import eu.tilsner.cubansea.search.SearchResult
import eu.tilsner.cubansea.utilities.StemmerHelper

import grails.test.*

class PotterSummaryPreparationAlgorithmTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {
    	def pages = [
    	   [getRank: {1}, url: null, content: null, links: [],
    	    getTitle: {"Wikipedia: Banana Republic"},
    	    getSummary: {"""
    	    	beautiful republic of Banana 
    	    	Banana island costa rica
    	    """}] as SearchResult,
        	[getRank: {9}, url: null, content: null, links: [],
        	 getTitle: {"Harvesting Bananas"},
        	 getSummary: {"""
        	  	banana harvest south america
        	  	taste golden
        	 """}] as SearchResult
    	   ]
    	def expected = [
    	   [(StemmerHelper.stem("banana")):2.0,
    	    (StemmerHelper.stem("beautiful")):1.0,
    	    (StemmerHelper.stem("costa")):1.0,
    	    (StemmerHelper.stem("republic")):1.0,
    	    (StemmerHelper.stem("rica")):1.0,
    	    (StemmerHelper.stem("island")):1.0],
    	    
    	   [(StemmerHelper.stem("banana")):1.0,
    	    (StemmerHelper.stem("america")):1.0,
    	    (StemmerHelper.stem("south")):1.0,
    	    (StemmerHelper.stem("harvest")):1.0,
    	    (StemmerHelper.stem("taste")):1.0,
    	    (StemmerHelper.stem("golden")):1.0]
    	]
    	def algorithm = new PotterSummaryPreparationAlgorithm()
    	def results   = algorithm.prepareResults(pages).collect { it.allFrequencies }
    	expected.eachWithIndex {exp,index ->
    		assertEquals exp, results[index]
    	}
    	println "test: ${expected == results}"
    	assertEquals expected, results
    }
}
