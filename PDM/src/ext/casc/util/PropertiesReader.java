package ext.casc.util;

import java.io.UnsupportedEncodingException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class PropertiesReader {
	private ResourceBundle bundle;
	private String fromEncoding;
	private String toEncoding;

	public PropertiesReader(){

	}

	public PropertiesReader(String file, String formEncoding, String toEncoding){
		bundle = PropertyResourceBundle.getBundle(file);
		this.fromEncoding = formEncoding;
		this.toEncoding = toEncoding;;
	}

	public String getValue(String key){
		String value = null;
		try {
			key = new String(key.getBytes(fromEncoding), toEncoding);
			value = bundle.getString(key);
			value = new String(value.getBytes(toEncoding), fromEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			value = "";
		}catch (Exception e){
			e.printStackTrace();
			value = "";
		}
		return value;
	}

	public String getFromEncoding() {
		return fromEncoding;
	}
	public void setFromEncoding(String fromEncoding) {
		this.fromEncoding = fromEncoding;
	}
	public String getToEncoding() {
		return toEncoding;
	}
	public void setToEncoding(String toEncoding) {
		this.toEncoding = toEncoding;
	}

}
