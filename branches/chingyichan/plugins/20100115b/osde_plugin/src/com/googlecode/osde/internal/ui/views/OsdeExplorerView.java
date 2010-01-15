package com.googlecode.osde.internal.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

public class OsdeExplorerView extends CommonNavigator {

	static class OsdeView extends CommonViewer {

		private OsdeExplorerView explorerView;

		public OsdeView(String aViewerId, Composite aParent, int aStyle,
				OsdeExplorerView osdeExplorerView) {
			super(aViewerId, aParent, aStyle);
			this.explorerView = osdeExplorerView;

		}
	}

	@Override
	protected CommonViewer createCommonViewer(Composite aParent) {
		return new OsdeView(getViewSite().getId(), aParent, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL, this);
	}

}
