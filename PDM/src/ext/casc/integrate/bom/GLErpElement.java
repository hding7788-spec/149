package ext.casc.integrate.bom;


import org.dom4j.Element;

public class GLErpElement {
    private String docoid = "";
    private Element element= null;

    public GLErpElement(String docoid, Element element) {
        this.docoid = docoid;
        this.element = element;
    }

    public String getDocoid() {
        return docoid;
    }

    public void setDocoid(String docoid) {
        this.docoid = docoid;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
