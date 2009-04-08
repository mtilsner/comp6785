import grails.test.*

class CubanSeaTagLibTests extends TagLibUnitTestCase {

	protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testTermHighlighter() {
		def cs 		= new CubanSeaTagLib()
		def input 	= "this is the input that has to be highlighted"
		def terms 	= ["highlight","input"]
		def expOut	= "this is the <b>input</b> that has to be <b>highlighted</b>"
		assertEquals expOut, cs.termHighlighter(content: input, terms: terms).toString()
    }
}
