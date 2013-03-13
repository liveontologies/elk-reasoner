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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * A collections of low-level tests to check dynamic disallowing of the
 * incremental reasoning mode.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalModeSwitchTest {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();
	
	@Test
	public void testAddedTransitivity() throws ElkException {
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(loader, new PostProcessingStageExecutor());

		reasoner.setAllowIncrementalMode(false);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass A = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass B = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass C = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass D = objectFactory.getClass(new ElkFullIri(":D"));
		ElkObjectProperty R = objectFactory.getObjectProperty(new ElkFullIri(":R"));
		ElkAxiom axASubRB = objectFactory.getSubClassOfAxiom(A, objectFactory.getObjectSomeValuesFrom(R, B));
		ElkAxiom axBSubRC = objectFactory.getSubClassOfAxiom(B, objectFactory.getObjectSomeValuesFrom(R, C));
		ElkAxiom axRCSubD = objectFactory.getSubClassOfAxiom(objectFactory.getObjectSomeValuesFrom(R, C), D);
		ElkAxiom axTransR = objectFactory.getTransitiveObjectPropertyAxiom(R);
		
		loader.add(axASubRB).add(axBSubRC).add(axRCSubD);

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomyQuietly();

		assertFalse(taxonomy.getNode(A).getAllSuperNodes().contains(taxonomy.getNode(D)));
		
		reasoner.setAllowIncrementalMode(true);
		changeLoader.add(axTransR);

		taxonomy = reasoner.getTaxonomyQuietly();
		// Now A should be a subclass of D since R is transitive
		assertTrue(taxonomy.getNode(A).getAllSuperNodes().contains(taxonomy.getNode(D)));
	}

}
