package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import java.io.Serializable;

import com.google.gadgets.ViewName;
import com.google.gadgets.ViewType;

@SuppressWarnings("serial")
public class GadgetViewData implements Serializable {

	private ViewName viewName;
	
	private ViewType type;
	
	private boolean createExternalJavaScript;
	
	private boolean createInitFunction;
	
	private String href;

	/**
	 * viewNameを返します。
	 * @return viewName viewName
	 */
	public ViewName getViewName() {
		return viewName;
	}

	/**
	 * viewNameをセットします。
	 * @param viewName viewName
	 */
	public void setViewName(ViewName viewName) {
		this.viewName = viewName;
	}

	/**
	 * typeを返します。
	 * @return type type
	 */
	public ViewType getType() {
		return type;
	}

	/**
	 * typeをセットします。
	 * @param type type
	 */
	public void setType(ViewType type) {
		this.type = type;
	}

	/**
	 * createExternalJavaScriptを返します。
	 * @return createExternalJavaScript createExternalJavaScript
	 */
	public boolean isCreateExternalJavaScript() {
		return createExternalJavaScript;
	}

	/**
	 * createExternalJavaScriptをセットします。
	 * @param createExternalJavaScript createExternalJavaScript
	 */
	public void setCreateExternalJavaScript(boolean createExternalJavaScript) {
		this.createExternalJavaScript = createExternalJavaScript;
	}

	/**
	 * createInitFunctionを返します。
	 * @return createInitFunction createInitFunction
	 */
	public boolean isCreateInitFunction() {
		return createInitFunction;
	}

	/**
	 * createInitFunctionをセットします。
	 * @param createInitFunction createInitFunction
	 */
	public void setCreateInitFunction(boolean createInitFunction) {
		this.createInitFunction = createInitFunction;
	}

	/**
	 * hrefを返します。
	 * @return href href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * hrefをセットします。
	 * @param href href
	 */
	public void setHref(String href) {
		this.href = href;
	}
	
}
