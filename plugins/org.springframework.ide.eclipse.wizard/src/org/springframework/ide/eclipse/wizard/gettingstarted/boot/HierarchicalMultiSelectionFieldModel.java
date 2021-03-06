/*******************************************************************************
 * Copyright (c) 2015 GoPivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     GoPivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.wizard.gettingstarted.boot;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.springsource.ide.eclipse.commons.livexp.core.LiveExpression;

/**
 * Model for a UI widget that offers multiple choices. Similar to MultiSelectionFieldModel, however
 * the choices are organized into categories instead of a flat list of choices.
 *
 * @author Kris De Volder
 */

public class HierarchicalMultiSelectionFieldModel<T> {

	private Map<String, MultiSelectionFieldModel<T>> categories = new TreeMap<String, MultiSelectionFieldModel<T>>();

	private String name;
	private String label;
	private Class<T> type;

	public HierarchicalMultiSelectionFieldModel(Class<T> type, String name) {
		this.name = name;
		this.label = name;
		this.type = type;
	}

	public Collection<String> getCategories() {
		return Collections.unmodifiableCollection(categories.keySet());
	}

	public MultiSelectionFieldModel<T> getContents(String category) {
		return categories.get(category);
	}

	public HierarchicalMultiSelectionFieldModel<T> label(String label) {
		this.label = label;
		return this;
	}

	private MultiSelectionFieldModel<T> ensureCategory(String categoryName) {
		MultiSelectionFieldModel<T> existing = categories.get(categoryName);
		if (existing==null) {
			categories.put(categoryName, existing = new MultiSelectionFieldModel<T>(type, name)
					.label(categoryName));
		}
		return existing;
	}

	public void sort() {
		for (String cat : getCategories()) {
			getContents(cat).sort();
		}
	}

	/**
	 * Add a choice to a category, create the category if it doesn't exist yet.
	 */
	public void choice(String catName, String name, T dep, String tooltipText, LiveExpression<Boolean> enablement) {
		MultiSelectionFieldModel<T> cat = ensureCategory(catName);
		cat.choice(name, dep, tooltipText, enablement);
	}

	public String getLabel() {
		return label;
	}

}
