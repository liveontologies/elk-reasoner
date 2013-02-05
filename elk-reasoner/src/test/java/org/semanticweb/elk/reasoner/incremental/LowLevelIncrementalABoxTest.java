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
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class LowLevelIncrementalABoxTest {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

/*	@Test
	public void testDeleteAssertionCheckTBox() throws ElkException, IOException {
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(new PostProcessingStageExecutor());
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyLoader(loader);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass A = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass B = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass C = objectFactory.getClass(new ElkFullIri(":C"));
		ElkNamedIndividual a = objectFactory.getNamedIndividual(new ElkFullIri(":a"));
		ElkAxiom axaInstB = objectFactory.getClassAssertionAxiom(B, a);
		ElkAxiom axaInstC = objectFactory.getClassAssertionAxiom(C, a);
		ElkAxiom axDisj = objectFactory.getDisjointClassesAxiom(A, B, C);
		
		loader.add(axaInstB).add(axaInstC).add(axDisj);

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomyQuietly();
		loader.clear();

		reasoner.setIncrementalMode(true);

		changeLoader.remove(axaInstC);

		taxonomy = reasoner.getTaxonomyQuietly();
		
		TaxonomyPrinter.dumpClassTaxomomy(taxonomy, new OutputStreamWriter(System.out), false);
	}*/		
	
	

}
