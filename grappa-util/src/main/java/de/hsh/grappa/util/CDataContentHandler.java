package de.hsh.grappa.util;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import org.xml.sax.SAXException;

import com.sun.xml.txw2.output.XMLWriter;


//from: https://stackoverflow.com/questions/3136375/how-to-generate-cdata-block-using-jaxb#answer-39557646
//class XMLWriter contained in grappa-backend-plugin-api already

public class CDataContentHandler extends XMLWriter{
//public class CDataContentHandler extends com.sun.xml.txw2.output.XMLWriter{
	public CDataContentHandler(Writer writer,String encoding) throws IOException{
		super(writer,encoding);
	}

	// see http://www.w3.org/TR/xml/#syntax
	private static final Pattern XML_CHARS=Pattern.compile("[<>&]");

	public void characters(char[] ch,int start,int length) throws SAXException{
		boolean useCData=XML_CHARS.matcher(new String(ch,start,length)).find();
		if(useCData){
			super.startCDATA();
		}
		super.characters(ch,start,length);
		if(useCData){
			super.endCDATA();
		}
	}
}