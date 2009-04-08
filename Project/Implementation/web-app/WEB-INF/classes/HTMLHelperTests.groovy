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
<a href="www.amazon.de">Amazon</a>"""
		def foundDocument = HTMLHelper.fetchDocument(new URL("http://www.limeweb.eu/~matthias.tilsner/webTestFile"))    	

		assertEquals expectedDocument.trim(), foundDocument.trim()
    }

    void testLinkExtract() {
		def expectedLinks = ["http://www.google.com/",
		                     "http://www.yahoo.com",
		                     "index.php"]

		def foundLinks = HTMLHelper.extractLinks("""<html>
				  <head>
				    <link href="doNotShowUp" />
				  </head>
				  <body>
				    This is a test document containing a link to
				    <a href="http://www.google.com/">Google</a>
				    and one to <a href="http://www.yahoo.com">Yahoo</a>

				    Also, it has a Form inside a division
				    <div class="test">
				      <form action="index.php?hello">
				      </form>
				    </div>
				  </body>
</html>""")
		
		assertEquals expectedLinks.sort(), foundLinks.sort()
    }
}
