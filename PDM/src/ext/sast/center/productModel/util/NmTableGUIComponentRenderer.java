package ext.sast.center.productModel.util;

import java.io.PrintWriter;

import org.apache.log4j.Logger;

import wt.log4j.LogR;
import com.ptc.core.components.rendering.AbstractRenderer;
import com.ptc.core.components.rendering.RenderingContext;
import com.ptc.core.components.rendering.RenderingException;
import com.ptc.core.components.rendering.renderers.HTMLGuiComponentRenderer;

public class NmTableGUIComponentRenderer extends AbstractRenderer {
	/**
	 * This class is used to render a html componet in nmtable
	 */

	private static final Logger log;
	public NmTableGUIComponentRenderer() {
	}

	protected void renderObject(Object obj, PrintWriter printwriter,
			RenderingContext renderingcontext) throws RenderingException {
		log.trace("begin renderObject() ");
		if (obj instanceof NmTableGUIComponent) {
			NmTableGUIComponent colorString = (NmTableGUIComponent) obj;
			String value = colorString.getValue();
			StringBuffer stringbuffer = new StringBuffer();
			stringbuffer.append(value);
			printwriter.print(stringbuffer.toString());
		}
		log.trace("end renderObject() ");
	}

	private String getAttributes(NmTableGUIComponent ColorStringcomponent) {
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append((new StringBuilder()).append(
				HTMLGuiComponentRenderer.getIdString(ColorStringcomponent
						.getId())).append(" ").toString());
		stringbuffer.append((new StringBuilder()).append(
				ColorStringcomponent.getStyleClasses()).append(" ").toString());
		stringbuffer.append((new StringBuilder()).append(
				HTMLGuiComponentRenderer.getTooltipString(ColorStringcomponent
						.getTooltip())).append(" ").toString());
		stringbuffer.append((new StringBuilder())
				.append(
						HTMLGuiComponentRenderer
								.getJsActionsString(ColorStringcomponent
										.getJsActions())).append(" ")
				.toString());
		return stringbuffer.toString();
	}

	protected boolean isValidForObject(Object obj) {
		return true;
	}

	static {
		try {
			log = LogR.getLogger(NmTableGUIComponent.class.getName());
		} catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}
}
