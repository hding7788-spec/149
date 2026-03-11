package ext.sast.center.productModel.util;

import com.ptc.core.components.rendering.guicomponents.HTMLGuiComponent;
import com.ptc.core.components.rendering.guicomponents.PrintableComponent;

public class NmTableGUIComponent  extends HTMLGuiComponent
    implements PrintableComponent
{
	/**
	 * this class is used to generate a html component in nmtable
	 */
   	private Integer percent;
    private String barcolor;
    private Comparable internalValue;
    private boolean isStyle;
    private String value; 
    private String bgcolor; 
    private String align;
    
    public static NmTableGUIComponent getColorStringComponent(String value)
    {
    	NmTableGUIComponent  colorstringcomponent = new NmTableGUIComponent(value);
        return colorstringcomponent;
    }

    public NmTableGUIComponent(String value)
    {
        setValue(value);
        setValueHidden(false);
        setBgcolor("#000000");
        setBarcolor("#000000");
        setRenderer(new NmTableGUIComponentRenderer());
    }
    
    public String getValue(){
    	return value;
    }
    public void setValue(String s){
    	value = s;
    }

     public Integer getPercent()
    {
        return percent;
    }

    public void setPercent(Integer integer)
    {
        percent = integer;
    }
    public String getBarcolor()
    {
        return barcolor;
    }
    public void setBarcolor(String scolor)
    {
        barcolor =  scolor;
    }

    public boolean isStyle()
    {
        return isStyle;
    }

    public void setIsStyle(boolean flag)
    {
        isStyle = flag;
    }

    public Comparable getInternalValue()
    {
        if(internalValue == null)
        {
            return getTooltip();
        } else
        {
            return internalValue;
        }
    }

    public void setInternalValue(Comparable comparable)
    {
        internalValue = comparable;
    }

    public String getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public void setStyle(boolean isStyle) {
        this.isStyle = isStyle;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer("ColorStringComponent ");
        stringbuffer.append((new StringBuilder()).append("\n id:").append(getId()).toString());
        stringbuffer.append((new StringBuilder()).append("\n percent:").append(getPercent()).toString());
        stringbuffer.append((new StringBuilder()).append("\n color:").append(getBarcolor()).toString());
        stringbuffer.append((new StringBuilder()).append("\n tooltip:").append(getTooltip()).toString());
        stringbuffer.append((new StringBuilder()).append("\n styleClass:").append(getStyleClasses()).toString());
        stringbuffer.append((new StringBuilder()).append("\n isStyle:").append(isStyle()).append(" (if true renders as <SPAN> and neither src nor url is used)").toString());
        return stringbuffer.toString();
    }

    public String getPrintableValue()
    {
        return getTooltip();
    }
}
