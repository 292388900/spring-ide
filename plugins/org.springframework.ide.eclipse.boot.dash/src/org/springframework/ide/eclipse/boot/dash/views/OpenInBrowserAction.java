/*******************************************************************************
 * Copyright (c) 2015 Pivotal Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pivotal Software, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.boot.dash.views;

import org.eclipse.swt.widgets.Display;
import org.springframework.ide.eclipse.boot.dash.BootDashActivator;
import org.springframework.ide.eclipse.boot.dash.livexp.MultiSelection;
import org.springframework.ide.eclipse.boot.dash.model.BootDashElement;
import org.springframework.ide.eclipse.boot.dash.model.BootDashElementUtil;
import org.springframework.ide.eclipse.boot.dash.model.BootDashModel;
import org.springframework.ide.eclipse.boot.dash.model.BootDashModel.ElementStateListener;
import org.springframework.ide.eclipse.boot.dash.model.UserInteractions;

public class OpenInBrowserAction extends AbstractBootDashAction {

	private ElementStateListener listener;
	private BootDashModel model;

	public OpenInBrowserAction(BootDashModel model, MultiSelection<BootDashElement> selection, UserInteractions ui) {
		super(selection, ui);
		this.model = model;
		this.setText("Open Web Browser");
		this.setToolTipText("Open a Web Browser on the default URL");
		this.setImageDescriptor(BootDashActivator.getImageDescriptor("icons/open_browser.gif"));
		this.setDisabledImageDescriptor(BootDashActivator.getImageDescriptor("icons/open_browser_disabled.gif"));
		if (model!=null) {
			model.addElementStateListener(listener = new ElementStateListener() {
				public void stateChanged(BootDashElement e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							updateEnablement();
						}
					});
				}
			});
		}
	}

	@Override
	public void updateEnablement() {
		String url = getUrl();
		setEnabled(url!=null);
	}

	private String getUrl() {
		BootDashElement el = getSingleSelectedElement();
		if (el!=null) {
			return BootDashElementUtil.getUrl(el);
		}
		return null;
	}

	@Override
	public void run() {
		String url = getUrl();
		if (url!=null) {
			ui.openUrl(url);
		}
	}

	@Override
	public void dispose() {
		if (listener==null) {
			model.removeElementStateListener(listener);
			listener = null;
		}
		super.dispose();
	}

}
