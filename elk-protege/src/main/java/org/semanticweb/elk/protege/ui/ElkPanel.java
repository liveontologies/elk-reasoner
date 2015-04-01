package org.semanticweb.elk.protege.ui;

import javax.swing.JPanel;

public abstract class ElkPanel extends JPanel {

	private static final long serialVersionUID = -354051899719180000L;

	public abstract ElkPanel initialize();

	public abstract ElkPanel applyChanges();

}
