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

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/03/2012
 */
public class WorkbenchManager {

    private ProofManager proofManager;

    private OWLAxiom entailment;
    
    public WorkbenchManager(ProofManager proofManager, OWLAxiom entailment) {
        this.proofManager = proofManager;
        this.entailment = entailment;
    }
    
    public WorkbenchManager(ProofManager proofManager) {
        this.proofManager = proofManager;
        this.entailment = null;
    }

    public OWLAxiom getEntailment() {
        return entailment;
    }

    public ProofManager getProofManager() {
        return proofManager;
    }
    
}
