import grails.test.*

import eu.tilsner.cubansea.utilities.HTMLHelper

class HTMLHelperTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testDocumentFetch() {
    	def expectedDocument = """This is a test file
<a href="www.amazon.de">Amazon</a>
"""

		def foundDocument = HTMLHelper.fetchDocument("http://www.limeweb.de/~matthias.tilsner/webTestFile")
    	
		assert expectedDocument == foundDocument
    }

    void testLinkExtract() {
		def expectedLinks = ["http://www.google.com/",
		                     "http://www.yahoo.com",
		                     "index.php?hello"]

		def foundLinks = HTMLHelper.extractLinks("""<html>
				  <link href="doNotShowUp" />
				  </html>
				  <body>
				    This is a test document containing a link to
				    <a href="http://www.google.com/">Google</a>
				    and one to <a href="http://www.yahoo.com">Yahoo</a>

				    Also, it has a Form inside a division
				    <div class="test">
				      <form action="index.php?hello">
				      </form>
				    </div>
				  </body>""")
		
		assert expectedLinks.sort() == foundLinks.sort()
    }
}
