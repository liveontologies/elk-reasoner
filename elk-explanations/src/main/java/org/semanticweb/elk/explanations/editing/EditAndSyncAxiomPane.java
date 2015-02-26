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

import javax.swing.JButton;
import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.VerifyingOptionPane;

/**
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class EditAndSyncAxiomPane extends EditAxiomPane {

    /**
     * 
     */
    private static final long serialVersionUID = -6308201481924625979L;
    
    public static String OK_SYNC = "Apply and synchronize reasoner";

    private static final Logger logger = Logger.getLogger(VerifyingOptionPane.class);

    private JButton okSyncButton;

    public EditAndSyncAxiomPane(JComponent c) {
        super(c, new Object[] {OK, OK_SYNC, CANCEL}, OK_SYNC);
    }

    public JButton getSyncButton() {
    	return okSyncButton;
    }
    
    public void setOKEnabled(boolean enabled){
    	super.setOKEnabled(enabled);
    	
        if (okSyncButton == null){
            okSyncButton = getButtonComponent(this, JButton.class, OK_SYNC);
        }
        
        if (okSyncButton != null){
            okSyncButton.setEnabled(enabled);
        }
        else{
            logger.warn("Cannot find OK button for this system. Please report this with details of your OS and language.");
        }
    }

}
