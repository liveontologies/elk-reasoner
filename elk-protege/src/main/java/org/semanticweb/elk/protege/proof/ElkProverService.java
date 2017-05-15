package org.semanticweb.elk.protege.proof;

/*-
 * #%L
 * ELK Reasoner Protege Plug-in
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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

/** 
 * Date: 27-02-2017
 */

import org.liveontologies.owlapi.proof.OWLProver;
import org.liveontologies.protege.explanation.justification.proof.service.ProverService;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.elk.owlapi.ElkProver;
import org.semanticweb.elk.owlapi.ElkProverFactory;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class ElkProverService extends ProverService {

	@Override
	public void initialise() throws Exception {
	}

	@Override
	public void dispose() throws Exception {
	}

	@Override
	public OWLProver getProver(OWLEditorKit ek) {
		OWLProver proverElk = null;
		OWLReasoner reasoner = ek.getOWLModelManager()
				.getOWLReasonerManager().getCurrentReasoner();
		if (reasoner instanceof ElkReasoner) {
			proverElk = new ElkProver((ElkReasoner) reasoner);
		}
		else
		{
			ElkProverFactory factory = new ElkProverFactory();
			proverElk = factory.createReasoner(ek.getModelManager().getActiveOntology());
		}
		return proverElk;
	}

	@Override
	public String getName() {
		return "Elk";
	}
}