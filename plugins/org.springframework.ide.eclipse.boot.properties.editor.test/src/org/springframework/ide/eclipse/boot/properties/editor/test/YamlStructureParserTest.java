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
package org.springframework.ide.eclipse.boot.properties.editor.test;

import org.springframework.ide.eclipse.yaml.editor.completions.YamlStructureParser.SChildBearingNode;
import org.springframework.ide.eclipse.yaml.editor.completions.YamlStructureParser.SKeyNode;
import org.springframework.ide.eclipse.yaml.editor.completions.YamlStructureParser.SNode;
import org.springframework.ide.eclipse.yaml.editor.completions.YamlStructureParser.SRootNode;


public class YamlStructureParserTest extends YamlEditorTestHarness {

	public void testSimple() throws Exception {
		YamlEditor editor = new YamlEditor(
				"hello:\n"+
				"  world:\n" +
				"    message\n"
		);

		assertParse(editor,
				"ROOT(0): ",
				"  KEY(0): hello:",
				"    KEY(2): world:",
				"      RAW(4): message",
				"      RAW(-1): "
		);
	}

	public void assertParse(YamlEditor editor, String... expectDumpLines) throws Exception {
		StringBuilder expected = new StringBuilder();
		for (String line : expectDumpLines) {
			expected.append(line);
			expected.append("\n");
		}
		assertEquals(expected.toString().trim(),  editor.parseStructure().toString().trim());
	}

	public void testComments() throws Exception {
		YamlEditor editor = new YamlEditor(
				"#A comment\n" +
				"hello:\n"+
				"  #Another comment\n" +
				"  world:\n" +
				"    message\n"
		);
		assertParse(editor,
				"ROOT(0): ",
				"  RAW(-1): #A comment",
				"  KEY(0): hello:",
				"    RAW(-1):   #Another comment",
				"    KEY(2): world:",
				"      RAW(4): message",
				"      RAW(-1): "
		);
	}

	public void testSiblings() throws Exception {
		YamlEditor editor = new YamlEditor(
				"world:\n" +
				"  europe:\n" +
				"    france:\n" +
				"      cheese\n" +
				"    belgium:\n" +
				"    beer\n" + //At same level as key, technically this is a syntax error but we tolerate it
				"  canada:\n" +
				"    montreal: poutine\n" +
				"    vancouver:\n" +
				"      salmon\n" +
				"moon:\n" +
				"  moonbase-alfa:\n" +
				"    moonstone\n"
		);
		assertParse(editor,
				"ROOT(0): ",
				"  KEY(0): world:",
				"    KEY(2): europe:",
				"      KEY(4): france:",
				"        RAW(6): cheese",
				"      KEY(4): belgium:",
				"        RAW(4): beer",
				"    KEY(2): canada:",
				"      KEY(4): montreal: poutine",
				"      KEY(4): vancouver:",
				"        RAW(6): salmon",
				"  KEY(0): moon:",
				"    KEY(2): moonbase-alfa:",
				"      RAW(4): moonstone",
				"      RAW(-1): "
		);
	}

	public void testTreeEnd() throws Exception {
		YamlEditor editor = new YamlEditor(
				"world:\n" +
				"  europe:\n" +
				"    france:\n" +
				"      cheese\n" +
				"    belgium:\n" +
				"    beer\n" + //At same level as key, technically this is a syntax error but we tolerate it
				"  canada:\n" +
				"    montreal: poutine\n" +
				"    vancouver:\n" +
				"      salmon\n" +
				"moon:\n" +
				"  moonbase-alfa:\n" +
				"    moonstone\n"
		);
		SRootNode root = editor.parseStructure();
		SNode node = getNodeAtPath(root, 0, 1);
		assertTreeText(editor, node,
				"  canada:\n" +
				"    montreal: poutine\n" +
				"    vancouver:\n" +
				"      salmon\n"
		);

		node = getNodeAtPath(root, 0, 0, 1, 0);
		assertTreeText(editor, node,
				"beer"
		);
	}

	public void testTreeEndKeyNodeNoChildren() throws Exception {
		YamlEditor editor = new YamlEditor(
				"world:\n" +
				"  europe:\n" +
				"  canada:\n" +
				"    montreal: poutine\n" +
				"    vancouver:\n" +
				"      salmon\n" +
				"moon:\n" +
				"  moonbase-alfa:\n" +
				"    moonstone\n"
		);
		SRootNode root = editor.parseStructure();
		SNode node = getNodeAtPath(root, 0, 0);
		assertTreeText(editor, node,
				"  europe:"
		);
	}

	public void testFind() throws Exception {
		YamlEditor editor = new YamlEditor(
				"world:\n" +
				"  europe:\n" +
				"    france:\n" +
				"      cheese\n" +
				"    belgium:\n" +
				"    beer\n" + //At same level as key, technically this is a syntax error but we tolerate it
				"  canada:\n" +
				"    montreal: poutine\n" +
				"    vancouver:\n" +
				"      salmon\n" +
				"moon:\n" +
				"  moonbase-alfa:\n" +
				"    moonstone\n"
		);
		SRootNode root = editor.parseStructure();

		assertFind(editor, root, "world:", 					0);
		assertFind(editor, root, "  europe:", 				0, 0);
		assertFind(editor, root, "    france:",				0, 0, 0);
		assertFind(editor, root, "      cheese",			0, 0, 0, 0);
		assertFind(editor, root, "    belgium:",			0, 0, 1);
		assertFind(editor, root, "    beer",				0, 0, 1, 0);
		assertFind(editor, root, "  canada:",				0, 1);
		assertFind(editor, root, "    montreal: poutine",	0, 1, 0);
		assertFind(editor, root, "    vancouver:",			0, 1, 1);
		assertFind(editor, root, "      salmon",			0, 1, 1, 0);
		assertFind(editor, root, "moon:",					1);
		assertFind(editor, root, "  moonbase-alfa:",		1, 0);
		assertFind(editor, root, "    moonstone",			1, 0, 0);
	}

	public void testGetKey() throws Exception {
		YamlEditor editor = new YamlEditor(
				"world:\n" +
				"  europe:\n" +
				"    france:\n" +
				"      cheese\n" +
				"    belgium:\n" +
				"    beer\n" + //At same level as key, technically this is a syntax error but we tolerate it
				"  canada:\n" +
				"    montreal: poutine\n" +
				"    vancouver:\n" +
				"      salmon\n" +
				"moon:\n" +
				"  moonbase-alfa:\n" +
				"    moonstone\n"
		);
		SRootNode root = editor.parseStructure();
		assertKey(editor, root, "world:", 					"world");
		assertKey(editor, root, "  europe:", 				"europe");
		assertKey(editor, root, "    montreal: poutine",	"montreal");
	}


	private void assertKey(YamlEditor editor, SRootNode root, String nodeText, String expectedKey) throws Exception {
		int start = editor.getText().indexOf(nodeText);
		SKeyNode node = (SKeyNode) root.find(start);
		assertEquals(expectedKey, node.getKey());
	}

	private void assertFind(YamlEditor editor, SRootNode root, String snippet, int... expectPath) {
		int start = editor.getText().indexOf(snippet);
		int end = start+snippet.length();
		int middle = (start+end) / 2;

		SNode expectNode = getNodeAtPath(root, expectPath);

		assertEquals(expectNode, root.find(start));
		assertEquals(expectNode, root.find(middle));
		assertEquals(expectNode, root.find(end));
	}


	private void assertTreeText(YamlEditor editor, SNode node, String expected) throws Exception {
		String actual = editor.textBetween(node.getStart(), node.getTreeEnd());
		assertEquals(expected.trim(), actual.trim());
	}

	private SNode getNodeAtPath(SNode node, int... childindices) {
		int i = 0;
		while (i<childindices.length) {
			int child = childindices[i];
			node = ((SChildBearingNode)node).getChildren().get(child);
			i++;
		}
		return node;
	}

}