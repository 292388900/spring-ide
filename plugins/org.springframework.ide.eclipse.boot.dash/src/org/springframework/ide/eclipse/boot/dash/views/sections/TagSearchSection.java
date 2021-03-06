/*******************************************************************************
 * Copyright (c) 2015 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.boot.dash.views.sections;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.springsource.ide.eclipse.commons.livexp.core.LiveExpression;
import org.springsource.ide.eclipse.commons.livexp.core.LiveVariable;
import org.springsource.ide.eclipse.commons.livexp.core.ValidationResult;
import org.springsource.ide.eclipse.commons.livexp.core.ValueListener;
import org.springsource.ide.eclipse.commons.livexp.ui.Disposable;
import org.springsource.ide.eclipse.commons.livexp.ui.IPageWithSections;
import org.springsource.ide.eclipse.commons.livexp.ui.PageSection;

/**
 * A text box with the look and feel of a search box. The contents
 * of the searchbox text is mirrored into a LiveVariable 'model'.
 *
 * @author Alex Boyko
 * @author Kris De Volder
 */
public class TagSearchSection extends PageSection implements Disposable {

	private Text tagsSearchBox;
	private LiveVariable<String> model;


	public TagSearchSection(IPageWithSections owner, LiveVariable<String> model) {
		super(owner);
		this.model = model;
	}

	@Override
	public LiveExpression<ValidationResult> getValidator() {
		return OK_VALIDATOR;
	}

	@Override
	public void createContents(Composite page) {
		tagsSearchBox = new Text(page, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
		tagsSearchBox.setMessage("Type tags to match");
		tagsSearchBox.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		tagsSearchBox.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				model.setValue(tagsSearchBox.getText());
			}
		});
		this.model.addListener(new ValueListener<String>() {
			public void gotValue(LiveExpression<String> exp, String newText) {
				String oldText = tagsSearchBox.getText();
				if (!oldText.equals(newText)) { //Avoid cursor bug on macs.
					tagsSearchBox.setText(newText);
				}
			}
		});
	}

	@Override
	public void dispose() {
		tagsSearchBox.dispose();
	}

}
