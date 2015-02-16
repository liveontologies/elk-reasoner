package org.semanticweb.elk.explanations;
/*
 * #%L
 * Explanation Workbench
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.explanation.io.InconsistentOntologyPluginInstance;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 18/03/2012
 */
public class ProofBasedInconsistencyExplanationPlugin implements InconsistentOntologyPluginInstance {

    private OWLEditorKit editorKit;
    
    public void setup(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
    }

    public void explain(OWLOntology ontology) {
        final ProofWorkbenchPanel panel = new ProofWorkbenchPanel(editorKit);
        JOptionPane op = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dlg = op.createDialog("Inconsistent ontology explanation");
        
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                panel.dispose();
            }
        });
        
        dlg.setModal(false);
        dlg.setResizable(true);
        dlg.setVisible(true);
    }

    public void initialise() throws Exception {
    }

    public void dispose() throws Exception {
    }
}
