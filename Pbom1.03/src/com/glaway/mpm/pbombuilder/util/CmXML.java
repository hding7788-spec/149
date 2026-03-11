package com.glaway.mpm.pbombuilder.util;

import java.io.Externalizable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@SuppressWarnings("unchecked")
public class CmXML implements Externalizable {
   public static class CmXmlException extends Exception {
      private static final long serialVersionUID = -3978149022040121792L;

      public CmXmlException() {}

      public CmXmlException(String s) {
         super(s);
      }

      public CmXmlException(Exception e) {
         super(e.getMessage());
      }

      public CmXmlException(Throwable t) {
         super(t.getMessage());
      }

      public CmXmlException(Exception e, String msg) {
         super(e.getMessage() + msg);
      }
   }

   public static class CmXmlProperty {
      private String name;
      private String value;

      public CmXmlProperty() {
         this(null, null);
      }

      public CmXmlProperty(String name, String value) {
         this.setName(name);
         this.setValue(value);
      }

      public String getName() {
         return name == null || name.length() == 0 ? "default" : name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public String getValue() {
         return value;
      }

      public void setValue(String value) {
         this.value = value;
      }

      public String toString() {
         return "property name=" + this.name + ", value=" + this.value;
      }
   }

   private static final long   serialVersionUID   = -1820515937361343898L;
   private static final String translateChars     = "&<>'\"";
   private static final List   translateStrings   = Arrays.asList(new String[]{"&amp;", "&lt;", "&gt;", "&apos;", "&quot;"});

   public static final int     STRING_SOURCE      = 0;
   public static final int     FILE_SOURCE        = 1;
   public static final int     URL_SOURCE         = 2;
   public static final int     OUTPUT_CONTENT     = 1;
   public static final int     OUTPUT_HTML        = 2;
   public static final int     OUTPUT_CONTENTHTML = 3;
   public static final int     OUTPUT_STRING      = 4;
   public static final int     OUTPUT_WRITE       = 5;

   private String              name;
   private Vector              attributes;
   private CmXML               parent;
   private Vector              children;
   private PrintWriter         out;
   private static String       iconName           = "default";
   public String               encoding;
   private boolean             nameOnly;

   public CmXML() {
      name = null;
      attributes = null;
      parent = null;
      children = null;
      out = null;
      encoding = "GBK";
      nameOnly = false;
   }

   public CmXML(String name) {
      this();
      this.name = name;
   }

   public static String encode(String value) {
      if (value == null)
         return null;

      StringBuffer ret = new StringBuffer();

      for (int i = 0; i < value.length(); i++) {
         char c = value.charAt(i);

         switch (c) {
            case '&':
            case '<':
            case '>':
            case '\'':
            case '"':
               int pos = translateChars.indexOf(c);
               ret.append(translateStrings.get(pos));
               break;
            default:
               ret.append(c);
         }
      }

      return ret.toString();
   }

   public static String decode(String value) {
      if (value == null)
         return null;

      StringBuffer ret = new StringBuffer();

      for (int i = 0; i < value.length(); i++) {
         char c = value.charAt(i);
         if (c == '&') {
            int index = value.indexOf(';', i + 1);
            if (index > -1) {
               String tmpStr = value.substring(i, index + 1);
               // �����XMLת���ַ��������ʽ��ת���ַ���&#34;
               int pos = translateStrings.indexOf(tmpStr);
               if (pos > -1) {
                  ret.append(translateChars.charAt(pos));
                  i = index;
                  continue;
               }
            }
         }
         ret.append(c);
      }

      return ret.toString();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeLong(serialVersionUID);
      String s = toString(false);
      out.writeObject(s);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      try {
         in.readLong();
         String s = (String) in.readObject();
         CmXML xml = read(s, 0);
         xml.copyInto(this);
      } catch (Exception exception) {
         throw new IOException(exception.getMessage());
      }
   }

   public CmXML setEncoding(String s) {
      encoding = s;
      return this;
   }

   public CmXML setName(String s) {
      name = s;
      return this;
   }

   public String getName() {
      return name;
   }

   public CmXML setNameOnly(boolean flag) {
      nameOnly = flag;
      return this;
   }

   public boolean getNameOnly() {
      return nameOnly;
   }

   public CmXML setAttrs(Vector vector) {
      attributes = vector;
      return this;
   }

   public Vector getAttrs() {
      return attributes;
   }

   public CmXML setParent(CmXML xml) {
      parent = xml;
      return this;
   }

   public CmXML getParent() {
      return parent;
   }

   public CmXML setChildren(Vector vector) {
      children = vector;
      return this;
   }

   public Vector getChildren() {
      return children;
   }

   public CmXML setFOut(PrintWriter writer) {
      out = writer;
      return this;
   }

   public PrintWriter getFOut() {
      return out;
   }

   public CmXML writeString() throws CmXmlException {
      if (out == null)
         throw new CmXmlException("printWriter is null.");
      try {
         filePrintln(out, "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
         filePrintln(out, toString(true));
         return this;
      } catch (Exception exception) {
         throw new CmXmlException(exception.getMessage());
      }
   }

   public CmXML write() throws CmXmlException {
      if (out == null)
         throw new CmXmlException("printWriter is null.");
      try {
         if (name == null) {
            if (attributes != null) {
               CmXmlProperty prop = (CmXmlProperty) attributes.elementAt(0);
               filePrint(out, encode(prop.getValue()));
            }
            return this;
         }
         filePrint(out, "<" + name);
         if (attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
               CmXmlProperty prop = (CmXmlProperty) attributes.elementAt(i);
               filePrint(out, " " + prop.getName() + "=\"" + encode(prop.getValue()) + "\"");
            }

         }

         filePrintln(out, "/>");
         if (children != null) {
            writeContent();
            filePrintln(out, "</" + name + ">");
         }
         return this;
      } catch (Exception exception) {
         throw new CmXmlException(exception.getMessage());
      }
   }

   public CmXML writeContent() throws CmXmlException {
      if (children == null)
         return this;
      try {
         for (int i = 0; i < children.size(); i++) {
            CmXML xml = (CmXML) children.elementAt(i);
            xml.setFOut(out);
            xml.write();
         }

         return this;
      } catch (Exception exception) {
         throw new CmXmlException(exception.getMessage());
      }
   }

   public CmXML writeHtml() throws CmXmlException {
      try {
         if (name == null) {
            if (attributes != null) {
               CmXmlProperty prop = (CmXmlProperty) attributes.elementAt(0);
               filePrint(out, encode(prop.getValue()));
            }
            return this;
         }

         filePrint(out, "<" + name);
         if (attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
               CmXmlProperty prop = (CmXmlProperty) attributes.elementAt(i);
               filePrint(out, " " + prop.getName() + "=\"" + encode(prop.getValue()) + "\"");
            }

         }
         filePrint(out, ">");

         if (children == null) {
            // if (name.equals("p") || name.equals("a"))
            filePrint(out, "</" + name + ">");
            return this;
         }

         writeContentHtml();

         if (!name.equals("li") && !name.equals("opt"))
            filePrint(out, "</" + name + ">");

         return this;
      } catch (Exception exception) {
         throw new CmXmlException(exception.getMessage());
      }
   }

   public CmXML writeContentHtml() throws CmXmlException {
      if (children == null)
         return this;
      try {
         for (int i = 0; i < children.size(); i++) {
            CmXML xml = (CmXML) children.elementAt(i);
            xml.setFOut(out);
            xml.writeHtml();
         }

         return this;
      } catch (Exception exception) {
         throw new CmXmlException(exception.getMessage());
      }
   }

   public CmXML output(String filePath, int type) throws CmXmlException {
      try {
         FileOutputStream fos = new FileOutputStream(filePath);
         PrintWriter printwriter = new PrintWriter(fos, true);
         setFOut(printwriter);

         switch (type) {
            case OUTPUT_CONTENT: // '\001'
               writeContent();
               break;

            case OUTPUT_HTML: // '\002'
               writeHtml();
               break;

            case OUTPUT_CONTENTHTML: // '\003'
               writeContentHtml();
               break;

            case OUTPUT_STRING: // '\004'
               writeString();
               break;

            default: // OUTPUT_WRITE
               write();
               break;
         }
         fos.close();
         return this;
      } catch (Exception exception) {
         throw new CmXmlException(exception.getMessage());
      }
   }

   public String string_format(String formatStr, Vector params) {
      String ret = "";

      String format = formatStr;
      int i = 0;
      int j = 0;
      while ((i = format.indexOf('%', 0)) != -1) {
         ret = ret + format.substring(0, i);
         i++;
         format = formatStr.substring(i);
         String s5 = format.substring(0, 1);
         if (s5.equals("s")) {
            String s2 = (String) params.elementAt(j);
            ret = ret + s2;
            j++;
         } else if (s5.equals("d")) {
            Integer integer = (Integer) params.elementAt(j);
            String s3 = String.valueOf(integer);
            ret = ret + s3;
            j++;
         } else {
            ret = ret + s5;
         }
         i++;
         format = formatStr.substring(i);
      }
      ret = ret + format;
      return ret;
   }

   public String string_append(String s) {
      return string_append(s, false);
   }

   public String string_append(String s, boolean format) {
      return string_append(s, 0, format);
   }

   public String string_append(String s, int i) {
      return string_append(s, i, false);
   }

   public String string_append(String str, int i, boolean format) {
      String tab = "";
      for (int j = 0; j < i; j++)
         tab = tab + "\t";

      if (name == null) {
         if (attributes != null) {
            CmXmlProperty prop = (CmXmlProperty) attributes.elementAt(0);
            str = str + encode(prop.getValue());
         }
         return str;
      }
      if (format)
         str = str + tab + "<" + name;
      else
         str = str + "<" + name;

      if (attributes != null) {
         for (int k = 0; k < attributes.size(); k++) {
            CmXmlProperty prop = (CmXmlProperty) attributes.elementAt(k);
            str = str + " " + prop.getName() + "=\"" + encode(prop.getValue()) + "\"";
         }

      }
      if (children == null) {
         if (format)
            str = str + "/>\n";
         else
            str = str + "/>";
         return str;
      }
      if (format)
         str = str + ">\n";
      else
         str = str + ">";
      for (int l = 0; l < children.size(); l++) {
         CmXML xml = (CmXML) children.elementAt(l);
         str = xml.string_append(str, i + 1, format);
      }

      if (format)
         str = str + tab + "</" + name + ">\n";
      else
         str = str + "</" + name + ">";
      return str;
   }

   public String string() {
      return string(false);
   }

   public String string(boolean flag) {
      String s = "";
      return string_append(s, flag);
   }

   public String toString() {
      if (nameOnly)
         return getName();
      else
         return toString(false);
   }

   public String toString(boolean flag) {
      String s = "";
      return string_append(s, flag);
   }

   public String stringContent() {
      String s = "";
      if (children == null)
         return s;
      for (int i = 0; i < children.size(); i++) {
         CmXML xml = (CmXML) children.elementAt(i);
         s = xml.string_append(s);
      }

      return s;
   }

   public String string_appendHtml(String s) {
      if (name == null) {
         if (attributes != null) {
            CmXmlProperty b1 = (CmXmlProperty) attributes.elementAt(0);
            s = s + b1.getValue();
         }
         return s;
      }
      s = s + "<" + name;
      if (attributes != null) {
         for (int i = 0; i < attributes.size(); i++) {
            CmXmlProperty b2 = (CmXmlProperty) attributes.elementAt(i);
            s = s + " " + b2.getName() + "=\"" + b2.getValue() + "\"";
         }

      }
      s = s + ">";
      if (children == null) {
         if (name.equals("p") || name.equals("a"))
            s = s + "</" + name + ">";
         return s;
      }
      for (int j = 0; j < children.size(); j++) {
         CmXML xml = (CmXML) children.elementAt(j);
         s = xml.string_appendHtml(s);
      }

      if (!name.equals("li") && !name.equals("opt"))
         s = s + "</" + name + ">";
      return s;
   }

   public String stringHtml() {
      String s = "";
      return string_appendHtml(s);
   }

   public String stringContentHtml() {
      String s = "";
      if (children == null)
         return s;
      for (int i = 0; i < children.size(); i++) {
         CmXML xml = (CmXML) children.elementAt(i);
         s = xml.string_appendHtml(s);
      }

      return s;
   }

   public CmXML prepend(CmXML xml) {
      xml.setParent(this);
      if (children == null) {
         children = new Vector();
         children.add(xml);
      } else {
         children.add(0, xml);
      }
      return this;
   }

   public CmXML append(CmXML xml) {
      xml.setParent(this);
      if (children == null) {
         children = new Vector();
         children.add(xml);
      } else {
         children.add(xml);
      }
      return this;
   }

   public CmXML replace(CmXML fromXml, CmXML toXml) {
      if (fromXml == null)
         return this;
      if (fromXml.parent == null)
         return this;
      if (toXml == null)
         return this;
      if (fromXml.parent.children == null)
         return this;
      int i = fromXml.parent.children.indexOf(fromXml);
      if (i == -1) {
         return this;
      } else {
         fromXml.parent.children.set(i, toXml);
         return this;
      }
   }

   public CmXML replaceContent(CmXML xml) {
      if (children != null)
         children.removeAllElements();
      prepend(xml);
      return this;
   }

   public CmXML deleteChildren() {
      if (children != null)
         children.removeAllElements();
      return this;
   }

   public int childrenNum() {
      if (children == null)
         return 0;
      else
         return children.size();
   }

   public CmXML findChildByAttr(String s, String s1) {
      for (CmXML xml = firstElem(); xml != null; xml = xml.nextElem())
         if (xml.attrval(s).equals(s1))
            return xml;

      return null;
   }

   /**
    * @param s
    * "s" ���� ��ǰ�ڵ�s�����ӽڵ�
    * ".s" ���� ��ǰ�ڵ��ӽڵ��нڵ���Ϊs�Ľڵ�
    * ".s(n)" ���� ��ǰ�ڵ��ӽڵ��нڵ���Ϊs�Ľڵ�ĵ�n���ӽڵ�,��ʼֵΪ0
    * ".s[n]" ���� ��ǰ�ڵ��ӽڵ��нڵ���Ϊs�Ľڵ��� ������ id��name����n�Ľڵ�
    * @return
    */
   public CmXML loc(String s) {
      String numStr = null;
      String tmpStr = null;
      int nameDup = 0;
      int j = 0;

      CmXML xml = this;
      if (isNull(s))
         return null;
      if (s.charAt(0) == '.')
         if (xml.first() == null)
            return null;
         else
            return xml.first().loc(s.substring(1));
      for (; xml != null && xml.getName() == null; xml = xml.next())
         ;
      if (xml == null)
         return null;
      for (; s.charAt(0) == ' '; s = s.substring(1))
         ;
      tmpStr = "";
      for (; s.length() > 0 && s.charAt(0) != '.'; s = s.substring(1))
         tmpStr = tmpStr + s.charAt(0);

      if (s.length() > 0 && s.charAt(0) == '.')
         s = s.substring(1);
      for (; s.length() > 0 && s.charAt(0) == ' '; s = s.substring(1))
         ;
      j = tmpStr.indexOf(']');
      if (j != -1)
         tmpStr = tmpStr.substring(0, j);
      j = tmpStr.indexOf('(');
      if (j != -1) {
         numStr = tmpStr.substring(j + 1);
         for (int l = 0; l < numStr.length(); l++) {
            char c = numStr.charAt(l);
            if (c < '0' || c > '9')
               numStr = numStr.substring(0, l);
         }

         nameDup = Integer.valueOf(numStr).intValue();
         tmpStr = tmpStr.substring(0, j);
         numStr = null;
      } else {
         nameDup = 0;
         int k = tmpStr.indexOf('[');
         if (k != -1) {
            numStr = tmpStr.substring(k + 1);
            tmpStr = tmpStr.substring(0, k);
         }
      }
      while (xml != null)
         if (isNull(xml.getName()))
            xml = xml.next();
         else if (!xml.getName().equals(tmpStr))
            xml = xml.next();
         else if (nameDup != 0) {
            nameDup--;
            xml = xml.next();
         } else {
            if (isNull(numStr))
               if (isNotNull(s)) {
                  if (xml.first() == null)
                     return null;
                  else
                     return xml.first().loc(s);
               } else {
                  return xml;
               }
            String s2 = xml.attrval("id");
            if (isNotNull(s2)) {
               if (!s2.equals(numStr))
                  xml = xml.next();
               else if (isNotNull(s)) {
                  if (xml.first() == null)
                     return null;
                  else
                     return xml.first().loc(s);
               } else {
                  return xml;
               }
            } else {
               String s3 = xml.attrval("name");
               if (isNotNull(s3))
                  if (!s3.equals(numStr))
                     xml = xml.next();
                  else if (isNotNull(s)) {
                     if (xml.first() == null)
                        return null;
                     else
                        return xml.first().loc(s);
                  } else {
                     return xml;
                  }
            }
         }
      return null;
   }

   public CmXML locf(String s, Vector vector) {
      String s1 = string_format(s, vector);
      return loc(s1);
   }

   /**
    * ���ص�ǰ�ڵ������xml�ṹ�е�loc�ַ�������loc�����Ĳ���
    * @return
    */
   public String getLoc() {
      String s = "";
      if (parent != null)
         s = parent.getLoc();
      else
         s = "";
      if (s.length() > 0)
         s = s + ".";
      s = s + name;
      if (parent == null)
         return s;
      CmXML xml = parent.first();
      int i = 0;
      for (; xml != null && this != xml; xml = xml.next())
         if (xml.name != null && xml.name.equals(name))
            i++;

      if (i > 0)
         s = s + "(" + i + ")";
      return s;
   }

   public String getPureLoc() {
      String s = getLoc();
      s = s.substring(s.indexOf("."));
      return s;
   }

   public String getLocBuf_buf(String s) {
      if (parent != null) {
         s = s + parent.getLocBuf_buf(s);
         s = s + ".";
      }
      s = s + name;
      if (parent == null)
         return s;
      CmXML xml = parent.first();
      int i = 0;
      for (; xml != null && xml != this; xml = xml.next())
         if (xml.name != null && xml.name.equals(name))
            i++;

      if (i > 0)
         s = s + "(" + i + ")";
      return s;
   }

   public String getLocBuf() {
      String s = "";
      return getLocBuf_buf(s);
   }

   public CmXML set(CmXmlProperty prop) {
      if (attributes == null) {
         attributes = new Vector();
         attributes.add(prop);
         return this;
      }

      boolean find = false;
      String name = prop.getName();
      for (int i = 0; i < attributes.size(); i++) {
         CmXmlProperty b2 = (CmXmlProperty) attributes.elementAt(i);
         if (!b2.getName().equals(name))
            continue;
         attributes.set(i, prop);
         find = true;
         break;
      }

      if (!find)
         attributes.addElement(prop);
      return this;
   }

   public CmXML set(String name, String value) {
      CmXmlProperty prop = new CmXmlProperty();
      prop.setName(name);
      prop.setValue(value);

      return set(prop);
   }

   public CmXML set(String name, int value) {
      return set(name, String.valueOf(value));
   }

   public CmXML set(String name, boolean value) {
      return set(name, String.valueOf(value));
   }

   public CmXML set(String name, float value) {
      return set(name, String.valueOf(value));
   }

   public CmXML set(String name, double value) {
      return set(name, String.valueOf(value));
   }

   public CmXML set(String name, char values[]) {
      return set(name, String.valueOf(values));
   }

   public CmXML set(String name, long value) {
      return set(name, String.valueOf(value));
   }

   public CmXML set(String name, Object value) {
      return set(name, String.valueOf(value));
   }

   public CmXML setFormatStr(String name, String format, Vector params) {
      return set(name, string_format(format, params));
   }

   /**
    * @param name
    * @param catValue
    * <br>�޸�����ֵ sΪ������  s1ΪҪ���ӵ�ֵ��ִ�к������ֵΪ:  ԭֵ+si
    * @return
    */
   public CmXML attrcat(String name, String catValue) {
      if (attributes == null) {
         set(name, catValue);
         return this;
      }
      boolean flag = false;
      for (int i = 0; i < attributes.size(); i++) {
         CmXmlProperty b1 = (CmXmlProperty) attributes.elementAt(i);
         if (!b1.getName().equals(name))
            continue;
         b1.setValue(b1.getValue() + catValue);
         attributes.set(i, b1);
         flag = true;
         break;
      }

      if (!flag)
         set(name, catValue);
      return this;
   }

   public CmXML attrncat(String s, String s1, int i) {
      return attrcat(s, s1.substring(0, i));
   }

   /**
    * @param s
    * <br>���ص�ǰ�ڵ���������Ϊ s������ֵ
    * @return
    */
   public String attrval(String s) {
      if (attributes == null)
         return "";
      for (int i = 0; i < attributes.size(); i++) {
         CmXmlProperty b1 = (CmXmlProperty) attributes.elementAt(i);
         if (b1.getName() != null && b1.getName().equals(s))
            if (b1.getValue() == null)
               return "";
            else
               return b1.getValue();
      }

      return "";
   }

   /**
    * @param s
    * <br>���ص�ǰ�ڵ���������Ϊs�����Զ���
    * @return
    */
   public CmXmlProperty attrfind(String s) {
      if (attributes == null)
         return null;
      for (int i = 0; i < attributes.size(); i++) {
         CmXmlProperty b1 = (CmXmlProperty) attributes.elementAt(i);
         if (b1.getName() != null && b1.getName().equals(s))
            return b1;
      }

      return null;
   }

   public int attrvalnum(String s) {
      if (isNull(attrval(s)))
         return 0;
      else
         return Integer.valueOf(attrval(s)).intValue();
   }

   public static CmXML createText(String s) {
      CmXML xml = new CmXML();
      CmXmlProperty b1 = new CmXmlProperty();
      b1.setName(null);
      b1.setValue(s);
      Vector vector = new Vector();
      vector.addElement(b1);
      xml.setAttrs(vector);
      return xml;
   }

   public CmXML createTextLen(String s, int i) {
      return createText(s.substring(0, i));
   }

   public CmXML createTextF(String s, Vector vector) {
      return createText(string_format(s, vector));
   }

   public CmXML textCat(String s) {
      if (is_element()) {
         return this;
      } else {
         CmXmlProperty b1 = null;
         b1 = (CmXmlProperty) attributes.elementAt(0);
         b1.setValue(b1.getValue() + s);
         attributes.set(0, b1);
         return this;
      }
   }

   public CmXML textNCat(String s, int i) {
      return textCat(s.substring(0, i));
   }

   public void delete(CmXML xml) {
      if (xml == null)
         return;
      if (xml.parent != null && xml.parent.children != null)
         xml.parent.children.remove(xml);
   }

   public void delete() {
      if (parent != null && parent.children != null)
         parent.children.remove(this);
   }

   public CmXML first() {
      if (children == null)
         return null;
      if (children.size() > 0)
         return (CmXML) children.elementAt(0);
      else
         return null;
   }

   public CmXML firstElem() {
      if (children == null)
         return null;
      for (int i = 0; i < children.size(); i++) {
         CmXML xml = (CmXML) children.elementAt(i);
         if (xml.name != null)
            return xml;
      }

      return null;
   }

   public CmXML last() {
      if (children == null)
         return null;
      else
         return (CmXML) children.elementAt(children.size() - 1);
   }

   public CmXML lastElem() {
      if (children == null)
         return null;
      for (int i = children.size() - 1; i >= 0; i--) {
         CmXML xml = (CmXML) children.elementAt(i);
         if (xml.name != null)
            return xml;
      }

      return null;
   }

   public CmXML next() {
      if (parent == null)
         return null;
      if (parent.children == null)
         return null;
      int i = parent.children.indexOf(this);
      if (i == -1)
         return null;
      if (i + 1 < parent.children.size())
         return (CmXML) parent.children.elementAt(i + 1);
      else
         return null;
   }

   public CmXML nextElem() {
      if (parent == null)
         return null;
      if (parent.children == null)
         return null;
      int i = parent.children.indexOf(this);
      if (i == -1)
         return null;
      for (int j = i + 1; j < parent.children.size(); j++) {
         CmXML xml = (CmXML) parent.children.elementAt(j);
         if (xml.name != null)
            return xml;
      }

      return null;
   }

   public CmXML prev() {
      if (parent == null)
         return null;
      if (parent.children == null)
         return null;
      int i = parent.children.indexOf(this);
      if (i == -1)
         return null;
      if (i - 1 < 0)
         return null;
      else
         return (CmXML) parent.children.elementAt(i - 1);
   }

   public CmXML prevElem() {
      if (parent == null)
         return null;
      if (parent.children == null)
         return null;
      int i = parent.children.indexOf(this);
      if (i == -1)
         return null;
      for (int j = i; j >= 0; j--) {
         CmXML xml = (CmXML) children.elementAt(j);
         if (xml.name != null)
            return xml;
      }

      return null;
   }

   public CmXML copy() {
      if (name == null) {
         String s = string();
         CmXML xml = createText(s);
         return xml;
      }
      CmXML xml1 = new CmXML(name);
      if (attributes != null) {
         for (int i = 0; i < attributes.size(); i++) {
            CmXmlProperty b1 = (CmXmlProperty) attributes.elementAt(i);
            xml1.set(b1.getName(), b1.getValue());
         }

      }
      for (CmXML xml2 = first(); xml2 != null; xml2 = xml2.next())
         xml1.append(xml2.copy());

      return xml1;
   }

   public CmXML copyInto(CmXML xml) {
      xml.setName(getName());
      xml.setAttrs(getAttrs());
      xml.setParent(getParent());
      xml.setChildren(getChildren());
      return xml;
   }

   /**
    * ���ص�ǰ�ڵ�ĵ�һ�����Զ���
    * @return
    */
   public CmXmlProperty attrFirst() {
      if (attributes == null)
         return null;
      if (attributes.size() == 0)
         return null;
      else
         return (CmXmlProperty) attributes.elementAt(0);
   }

   public CmXmlProperty attrNext(CmXmlProperty b1) {
      if (b1 == null)
         return null;
      if (attributes == null)
         return null;
      if (attributes.size() == 0)
         return null;
      for (int i = 0; i < attributes.size(); i++)
         if (attributes.elementAt(i) == b1)
            if (i + 1 < attributes.size())
               return (CmXmlProperty) attributes.elementAt(i + 1);
            else
               return null;

      return null;
   }

   public boolean is(String s) {
      if (s == null)
         return false;
      if (name == null)
         return false;
      else
         return name.equals(s);
   }

   public boolean is_element() {
      return name != null;
   }

   public static CmXML read(String s) throws CmXmlException {
      return read(s, FILE_SOURCE);
   }

   public static CmXML read(InputStream inputstream) throws CmXmlException {
      InputSource inputsource = null;
      inputsource = new InputSource(inputstream);
      return read(inputsource);
   }

   public static CmXML read(String s, int type) throws CmXmlException {
      FileInputStream fileinputstream = null;
      StringReader stringreader = null;
      InputSource inputsource = null;
      try {
         switch (type) {
            case FILE_SOURCE: // '\001'
               try {
                  fileinputstream = new FileInputStream(s);
                  inputsource = new InputSource(fileinputstream);
               } catch (Exception exception) {
                  exception.printStackTrace();
               }
               break;

            case URL_SOURCE: // '\002'
               inputsource = new InputSource(s);
               break;

            case STRING_SOURCE: // '\0'
               stringreader = new StringReader(s);
               inputsource = new InputSource(stringreader);
               break;

            default:
               return null;
         }
         CmXML xml = read(inputsource);
         if (fileinputstream != null)
            try {
               fileinputstream.close();
            } catch (Exception exception2) {
            }
         if (stringreader != null)
            stringreader.close();
         return xml;
      } catch (Exception e) {
         e.printStackTrace();
         throw new CmXmlException(e.getMessage());
      }
   }

   public static CmXML read(InputSource inputsource) throws CmXmlException {
      boolean validate = false;
      boolean ignoringElementContentWhitespace = false;
      boolean ignoreingComments = true;
      boolean coalescing = false;
      boolean notExpandEntityReferences = false;
      try {
         DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
         docBuilderFactory.setNamespaceAware(true);
         docBuilderFactory.setValidating(validate);
         docBuilderFactory.setIgnoringComments(ignoreingComments);
         docBuilderFactory.setIgnoringElementContentWhitespace(ignoringElementContentWhitespace);
         docBuilderFactory.setCoalescing(coalescing);
         docBuilderFactory.setExpandEntityReferences(!notExpandEntityReferences);
         DocumentBuilder docBuilder = null;
         try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
         } catch (ParserConfigurationException pce) {
            System.err.println(pce);
            return null;
         }

         Document document;
         try {
            document = docBuilder.parse(inputsource);
         } catch (SAXException saxexception) {
            System.err.println(saxexception.getMessage());
            return null;
         } catch (IOException ioexception) {
            System.err.println(ioexception);
            return null;
         }

         CmXML xml = traverse(null, document);
         return xml;
      } catch (Exception exception) {
         exception.printStackTrace();
         throw new CmXmlException(exception.getMessage());
      }
   }

   public CmXML read_error(String s) {
      return null;
   }

   public static CmXML traverse(CmXML xml, Node node) {
      if (node == null)
         return null;
      switch (node.getNodeType()) {
         default:
            break;

         case 9: // '\t'
            xml = traverse(xml, ((Node) (((Document) node).getDocumentElement())));
            break;

         case 1: // '\001'
         case 3: // '\003'
            CmXML childXML;
            if (node.getNodeType() == 1)
               childXML = createFromDomElement(node);
            else
               childXML = createFromDomText(node);
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
               Node childNode = childNodes.item(i);
               childXML = traverse(childXML, childNode);
            }

            if (xml != null)
               xml.append(childXML);
            else
               xml = childXML;
            break;
      }
      return xml;
   }

   public static CmXML createFromDomElement(Node node) {
      CmXML xml = new CmXML(node.getNodeName());
      NamedNodeMap attributes = node.getAttributes();
      for (int i = 0; i < attributes.getLength(); i++) {
         Node attrNode = attributes.item(i);
         xml.set(attrNode.getNodeName(), attrNode.getNodeValue());
      }

      return xml;
   }

   /**
    * ADD:
    * 2011-2-14 Alex_Huang ����
    *��ݲ�ѯ��ǰCmXml�����ָ����Ƶ�CmXml���� 
    */
   public CmXML findForName(String xmlName) {
      if (xmlName.equals(getName())) {
         return this;
      }
      CmXML nextXML = nextElem();
      if (nextXML != null) {
         CmXML next = nextXML.findForName(xmlName);
         if (next != null)
            return next;
      }
      CmXML childXML = firstElem();
      if (childXML != null) {
         CmXML child = childXML.findForName(xmlName);
         if (child != null)
            return child;
      }
      return null;
   }

   public static CmXML createFromDomText(Node node) {
      CmXML xml = createText(node.getNodeValue());
      return xml;
   }

   public void filePrint(PrintWriter printwriter, String s) {
      printwriter.print(s);
      printwriter.flush();
   }

   public void filePrintln(PrintWriter printwriter, String s) {
      printwriter.println(s);
      printwriter.flush();
   }

   public String getIconName() {
      return iconName;
   }

   public static boolean isNull(String s) {
      if (s == null)
         return true;
      return s.length() == 0;
   }

   public static boolean isNotNull(String s) {
      return !isNull(s);
   }
}
