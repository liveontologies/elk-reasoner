/**
 * 
 */
package org.semanticweb.elk.explanations.editing;
/*
 * #%L
 * Explanation Workbench
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.Component;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.VerifyingOptionPane;

/**
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class EditAxiomPane extends JOptionPane {

    /**
     * 
     */
    private static final long serialVersionUID = -6308201481924625979L;
    
    public static String OK = "Apply";
    
    public static String CANCEL = "Cancel";

    private static final Logger logger = Logger.getLogger(VerifyingOptionPane.class);

    private JButton okButton;
    
    private ComponentListener editorHandler_;
    
    public EditAxiomPane(JComponent c) {
        this(c, new Object[] {OK, CANCEL}, OK);
    }
    
    protected EditAxiomPane(JComponent parent, Object[] options, Object defaultOption) {
        super(parent, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, defaultOption);
    }

    public void setOKEnabled(boolean enabled){
        if (okButton == null){
            okButton = getButtonComponent(this, JButton.class, OK);
        }
        
        if (okButton != null) {
            okButton.setEnabled(enabled);
        }
        else{
            logger.warn("Cannot find OK button for this system. Please report this with details of your OS and language.");
        }
    }

    protected <T extends JComponent> T getButtonComponent(JComponent parent, Class<T> type, String name) {
        if (type.isAssignableFrom(parent.getClass())){
            if (parent instanceof JButton){
                if (name.equals(((JButton)parent).getText())){
                    return (T) parent;
                }
            }
        }
        for (Component c : parent.getComponents()){
            if (c instanceof JComponent){
                T target = getButtonComponent((JComponent)c, type, name);
                if (target != null){
                    return target;
                }
            }
        }
        return null;
    }
    
    public void selectInitialValue() {
        // This is overriden so that the option pane dialog default
        // button doesn't immediately get the focus.
    }

	public void setEditorHandler(ComponentListener handler) {
		editorHandler_ = handler;
	}
	
	public JDialog createEditorDialog(JComponent parent) {
		JDialog dlg = createDialog(parent, "Class axiom editor");
		
		dlg.setModal(false);
        dlg.setResizable(true);
        dlg.pack();
        dlg.setLocationRelativeTo(parent);
        dlg.addComponentListener(editorHandler_);
        
        return dlg;
	}
}
