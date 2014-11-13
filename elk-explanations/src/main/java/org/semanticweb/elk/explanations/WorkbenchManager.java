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

import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/03/2012
 */
public class WorkbenchManager {

    private WorkbenchSettings workbenchSettings = new WorkbenchSettings();

    private ProofManager proofManager;

    private OWLAxiom entailment;
    
    public WorkbenchManager(ProofManager justificationManager, OWLAxiom entailment) {
        this.proofManager = justificationManager;
        this.entailment = entailment;
    }

    public WorkbenchSettings getWorkbenchSettings() {
        return workbenchSettings;
    }

    public OWLAxiom getEntailment() {
        return entailment;
    }

/*    public Set<Explanation<OWLAxiom>> getJustifications(OWLAxiom entailment) {
        JustificationType justificationType = workbenchSettings.getJustificationType();
        return proofManager.getJustifications(entailment, justificationType);
    }

    public int getJustificationCount(OWLAxiom entailment) {
        JustificationType justificationType = workbenchSettings.getJustificationType();
        return proofManager.getComputedExplanationCount(entailment, justificationType);
    }


*/    public ProofManager getProofManager() {
        return proofManager;
    }
    
/*    public int getPopularity(OWLAxiom axiom) {
        int count = 0;
        Set<Explanation<OWLAxiom>> justifications = proofManager.getJustifications(entailment, workbenchSettings.getJustificationType());
        for(Explanation<OWLAxiom> justification : justifications) {
            if(justification.contains(axiom)) {
                count++;
            }
        }
        return count;
    }*/
    
}
