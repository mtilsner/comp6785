import java.awt.Color

import eu.tilsner.cubansea.utilities.StemmerHelper
import eu.tilsner.cubansea.utilities.StringHelper
import eu.tilsner.cubansea.prepare.PreparedResult

import org.apache.log4j.Logger;

class CubanSeaTagLib {
	static namespace = "cs"

	static Logger logger = Logger.getLogger(CubanSeaTagLib.class.getName());	
	
	def wordCutter = {attrs ->
		if(!attrs?.content) throw new Exception("Missing attribute: 'content' required!")
		if(!attrs.length) attrs.length = 20
		attrs.length = attrs.length.toInteger()
		if (attrs.content.size() > attrs.length) 
			out << attrs.content[0..(attrs.length-3)]+"..."
		else
			out << attrs.content
	}

	def glower = {attrs,body ->
	  def classes = ["csGlower"]
	  def id = (attrs.id) ? """ id="${attrs.id}" """ : ""
	  if(attrs.classes) classes.addAll(attrs.classes)
	  out << """
<table class="${classes.join(" ")}"${id}>
  <tr class="csTop">
	<td class="csLeft"></td><td class="csCenter"></td><td class="csRight"></td>
  </tr><tr class="csMiddle">
	<td class="csLeft"></td><td class="csCenter">
	  <div class="csFull">${body()}</div>
	</td><td class="csRight"></td>
  </tr><tr class="csBottom">
	<td class="csLeft"></td><td class="csCenter"></td><td class="csRight"></td>
  </tr>
</table>"""
	}
	
	def linkColor = {attrs ->
		def baseHSB = Color.RGBtoHSB(attrs.color.getRed(), attrs.color.getGreen(), attrs.color.getBlue(), null);
		baseHSB[2] = 0.5;
		def color = Color.getHSBColor(baseHSB[0], baseHSB[1], baseHSB[2]);
		String _red 	= Integer.toHexString(color.getRed());
		String _green	= Integer.toHexString(color.getGreen());
		String _blue	= Integer.toHexString(color.getBlue());
		if(_red.length() < 2) 	_red   = "0"+_red;
		if(_green.length() < 2) _green = "0"+_green;
		if(_blue.length() < 2) 	_blue  = "0"+_blue; 
		out << _red+_green+_blue;		
	}
	
	def termHighlighter = {attrs ->
		if(attrs.content == null) {
			out << ""
		} else {
			def stems = attrs.terms?.toList().collect { StemmerHelper.stem(it).toLowerCase() }
			def content = attrs.content?.replaceAll(PreparedResult.INVALID_CHARACTER_PATTERN, " ");
			content = content.replaceAll("\\s+", " ")
			def words = StringHelper.split(content, " ")
			def result = attrs.content
			words.each {word ->
				if(stems.contains(StemmerHelper.stem(word.toLowerCase()))) {
					result = result.replaceAll(word, "<b>${word}</b>")
				} 
			}
			out << result
		}
	}
}
