package com.koobest.m.toolkits.parsexml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Bundle;
import android.util.Log;

public class ParseXml {
	public static boolean turnXmlToHtmlByXSL(InputStream xml_is, OutputStream htmlStream,
			InputStream xsl,Bundle params) throws TransformerException {

		Source source = new StreamSource(xml_is);
		StreamResult result = new StreamResult(htmlStream);
		TransformerFactory tFac = TransformerFactory.newInstance();
		Source xslSource = new StreamSource(xsl);
		Transformer t = tFac.newTransformer(xslSource);
		if(params!=null){
			for(String key:params.keySet()){
//				Log.e("","t null:"+(t==null)+",params key:"+params.get(key)+(params.get(key)==null));
				t.setParameter(key, params.get(key));
			}
		}
		t.transform(source, result);
		// myWebView.loadUrl(htmlFile.toURL().toString());
		return true;
	}
	
	public static void handleXmlInSAX(byte[] xml_buffer,DefaultHandler handler)
	         throws ParserConfigurationException, SAXException, IOException{
    	InputSource is=new InputSource(new ByteArrayInputStream(xml_buffer));
		SAXParserFactory spf = SAXParserFactory.newInstance(); 
		SAXParser saxParser = spf.newSAXParser();
		XMLReader xmlReader=saxParser.getXMLReader();
		xmlReader.setContentHandler(handler);
		xmlReader.parse(is);
    }
}
