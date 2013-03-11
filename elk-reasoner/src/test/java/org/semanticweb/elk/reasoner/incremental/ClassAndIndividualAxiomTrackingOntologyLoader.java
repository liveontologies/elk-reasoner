/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.List;

import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.owl.interfaces.ElkAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

/**
 * An extension of {@link ClassAxiomTrackingOntologyLoader} which also tracks
 * ABox axioms as changing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ClassAndIndividualAxiomTrackingOntologyLoader extends
		ClassAxiomTrackingOntologyLoader {

	ClassAndIndividualAxiomTrackingOntologyLoader(OntologyLoader loader) {
		super(loader);
	}

	public ClassAndIndividualAxiomTrackingOntologyLoader(OntologyLoader loader,
			OnOffVector<ElkAxiom> trackedAxioms, List<ElkAxiom> untrackedAxioms) {
		super(loader, trackedAxioms, untrackedAxioms);
	}

	@Override
	public Loader getLoader(final ElkAxiomProcessor axiomInserter) {
		final ElkAxiomProcessor processor = new ElkAxiomProcessor() {

			@Override
			public void visit(ElkAxiom elkAxiom) {
				axiomInserter.visit(elkAxiom);

				if (elkAxiom instanceof ElkClassAxiom
						|| elkAxiom instanceof ElkAssertionAxiom) {
					changingAxioms_.add(elkAxiom);
				} else
					staticAxioms_.add(elkAxiom);
			}
		};

		return loader_.getLoader(processor);
	}

}
