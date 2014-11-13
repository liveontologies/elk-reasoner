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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationProgressMonitor;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/03/2012
 */
public class JustificationGeneratorProgressDialog extends JDialog {

    private ExplanationProgressPanel panel = new ExplanationProgressPanel();

    private ExplanationProgressMonitor<OWLAxiom> progressMonitor;
    
    public JustificationGeneratorProgressDialog(Frame owner) {
        super(owner, "Computing explanations", true);
        setContentPane(panel);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dlgSize = getSize();
        setLocation(screenSize.width / 2 - dlgSize.width / 2, screenSize.height / 2 - dlgSize.height / 2);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        progressMonitor = new JustificationGeneratorProgressDialogMonitor();
    }



    public void reset() {
        panel.reset();
    }

    public ExplanationProgressMonitor<OWLAxiom> getProgressMonitor() {
        return progressMonitor;
    }

    private class JustificationGeneratorProgressDialogMonitor implements ExplanationProgressMonitor<OWLAxiom> {

        public void foundExplanation(ExplanationGenerator<OWLAxiom> owlAxiomExplanationGenerator, Explanation<OWLAxiom> explanation, Set<Explanation<OWLAxiom>> explanations) {
            panel.foundExplanation(owlAxiomExplanationGenerator, explanation, explanations);
        }

        public boolean isCancelled() {
            return panel.isCancelled();
        }
    }
}
