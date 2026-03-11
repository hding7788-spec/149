/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.qmIntf.viewPanel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author hywang
 */
public class XMLStringMapper extends DefaultHandler {
	private String file;
	private static Vector<List<String>> vector=new Vector<List<String>>();
	private List<String> list =null;
	private CharArrayWriter contents = new CharArrayWriter();
    
    private XMLStringMapper() { 
    }

    @Override
    public void startDocument() throws SAXException {
    	 
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String namespaceURI,
            String localName,
            String qName,
            Attributes attr) throws SAXException {
    	if("COLLECTION"==localName){
    		vector=new Vector<List<String>>();
    	}
    }
    
    public String replaceString(String str){
    	Pattern pt=Pattern.compile("\\s*|\t|\r|\n");
    	Matcher mt=pt.matcher(str);
    	return mt.replaceAll("");
    }

    @Override
    public void endElement(String namespaceURI,
            String localName,
            String qName) throws SAXException {
    	if(file!=null){
    		file=replaceString(file);
    		if("fileName" == localName){
    			list =new ArrayList<String>();
				list.add(file);
//    			if(file.endsWith(".ol")||file.endsWith(".prt")||file.endsWith(".dxf")||file.endsWith(".dwg")||file.endsWith(".jpg")){
//    				list =new ArrayList<String>();
//    				list.add(file);
//    			}
//    			else{
//    				list=null;
//    			}
    		}
    		if ("downloadURL" == localName && list !=null) {
    			list.add(file);
    			vector.add(list);
    		}
    		contents.reset();
    	}
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
    	 try {

             contents.write(ch, start, length);

         } catch (Exception e) {
             e.printStackTrace();
         }
    	file=contents.toString();
    }
    
    public static  Vector<List<String>> initData(String xmlData){
    	 try {
             XMLStringMapper ma = new XMLStringMapper();
             XMLReader xr = XMLReaderFactory.createXMLReader();
             xr.setContentHandler(ma);
             InputStream is = new ByteArrayInputStream(xmlData.getBytes("GBK"));
             xr.parse(new InputSource(is));
         } catch (Exception e) {
             e.printStackTrace();
         }
    	 return vector;
    }
}