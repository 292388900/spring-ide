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
package org.springframework.ide.eclipse.boot.properties.editor.completions;

import static org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2.getHTMLContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.springframework.ide.eclipse.boot.core.BootActivator;
import org.springframework.ide.eclipse.boot.properties.editor.HoverInfo;
import org.springframework.ide.eclipse.boot.properties.editor.util.HtmlBuffer;
import org.springframework.ide.eclipse.boot.properties.editor.util.Type;
import org.springframework.ide.eclipse.boot.properties.editor.util.TypeUtil;
import org.springframework.ide.eclipse.boot.util.StringUtil;


/** Example used as reference for explaingin the meaning of the instance variables:
 *
 * foo.bar[0].children.wavelen
 */
@SuppressWarnings("restriction")
public class TypeNavigationHoverInfo extends HoverInfo {


	/**
	 * Property expression that represents the full path to the point being hovered over.
	 * E.g. foo.bar[0].data.wavelen"
	 */
	private final String id;

	/**
	 * Last sgment of the path as a property name.
	 * This is only set if the naviation is accessing a property name (so, for example, will be null for navigation into
	 * indexed element in a sequence/list)
	 * <p>
	 * Example: "wavelen"
	 */
	private final String propName;

	/**
	 * The type from which we are navigating.
	 * E.g. the type of "foo.bar[0].data"
	 */
	private Type parentType;

	/**
	 * The type at which we arrive.
	 * E.g the type of "foo.bar[0].data.wavelen"
	 */
	private Type type;

	private TypeUtil typeUtil;

	public TypeNavigationHoverInfo(String id,  String propName, Type fromType, Type toType, TypeUtil typeUtil) {
		this.id = id;
		this.propName = propName;
		this.parentType = fromType;
		this.type = toType;
		this.typeUtil = typeUtil;
	}

	@Override
	public String getHtml() {
		HtmlBuffer html = new HtmlBuffer();

		html.raw("<b>");
		html.text(id);
		html.raw("</b>");
		html.raw("<br>");

		String typeStr = type.toString();
		if (typeStr==null) {
			typeStr = Object.class.getName();
		}
		html.raw("<a href=\"");
		html.url("type/"+type);
		html.raw("\">");
		html.text(typeStr);
		html.raw("</a>");

		//				String deflt = formatDefaultValue(data.getDefaultValue());
		//				if (deflt!=null) {
		//					html.raw("<br><br>");
		//					html.text("Default: ");
		//					html.raw("<i>");
		//					html.text(deflt);
		//					html.raw("</i>");
		//				}

		String description = getDescription();
		if (description!=null) {
			html.raw("<br><br>");
			html.raw(description);
		}

		return html.toString();
	}

	private String getDescription() {
		try {
			List<IJavaElement> jes = getAllJavaElements();
			if (jes!=null) {
				for (IJavaElement je : jes) {
					if (je instanceof IMember) {
						String jdoc = getHTMLContent((IMember)je, true);
						if (jdoc!=null) {
							return jdoc;
						}
					}
				}
			}
		} catch (Exception e) {
			BootActivator.log(e);
		}
		return null;
	}

	@Override
	public List<IJavaElement> getJavaElements() {
		if (propName!=null) {
			IJavaElement je;
			Type beanType = parentType;
			je = typeUtil.getSetter(beanType, propName);
			if (je!=null) {
				return Collections.singletonList(je);
			}
			je = typeUtil.getGetter(beanType, propName);
			if (je!=null) {
				return Collections.singletonList(je);
			}
			je = typeUtil.getField(beanType, propName);
			if (je!=null) {
				return Collections.singletonList(je);
			}
		}
		return Collections.emptyList();
	}

	private List<IJavaElement> getAllJavaElements() {
		if (propName!=null) {
			Type beanType = parentType;
			ArrayList<IJavaElement> elements = new ArrayList<IJavaElement>(3);
			maybeAdd(elements, typeUtil.getField(beanType, propName));
			maybeAdd(elements, typeUtil.getSetter(beanType, propName));
			maybeAdd(elements, typeUtil.getGetter(beanType, propName));
			if (!elements.isEmpty()) {
				return elements;
			}
		}
		return Collections.emptyList();
	}

	private void maybeAdd(ArrayList<IJavaElement> elements, IJavaElement e) {
		if (e!=null) {
			elements.add(e);
		}
	}
}