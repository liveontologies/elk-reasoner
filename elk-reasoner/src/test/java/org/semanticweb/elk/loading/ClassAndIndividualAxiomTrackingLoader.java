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
package org.semanticweb.elk.loading;

import java.util.List;

import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.owl.interfaces.ElkAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.incremental.OnOffVector;

/**
 * An extension of {@link ClassAxiomTrackingLoader} which also tracks ABox
 * axioms as changing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ClassAndIndividualAxiomTrackingLoader extends
		ClassAxiomTrackingLoader {

	ClassAndIndividualAxiomTrackingLoader(AxiomLoader loader) {
		super(loader);
	}

	public ClassAndIndividualAxiomTrackingLoader(AxiomLoader loader,
			OnOffVector<ElkAxiom> trackedAxioms, List<ElkAxiom> untrackedAxioms) {
		super(loader, trackedAxioms, untrackedAxioms);
	}

	@Override
	public void load(final ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) throws ElkLoadingException {

		final ElkAxiomProcessor wrappedAxiomInserter = new ElkAxiomProcessor() {

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

		loader_.load(wrappedAxiomInserter, axiomDeleter);

	}
}
