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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.junit.Test;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class LowLevelIncrementalABoxTest {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	@Test
	public void testBasicDeletion() throws ElkException, IOException {
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(loader, new PostProcessingStageExecutor());

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass A = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass B = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass C = objectFactory.getClass(new ElkFullIri(":C"));
		ElkNamedIndividual ind = objectFactory.getNamedIndividual(new ElkFullIri(":in"));
		ElkAxiom axiInstA = objectFactory.getClassAssertionAxiom(A, ind);
		ElkAxiom axASubB = objectFactory.getSubClassOfAxiom(A, B);
		ElkAxiom axCSubB = objectFactory.getSubClassOfAxiom(C, B);
		
		loader.add(axiInstA).add(axASubB).add(axCSubB);

		InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = reasoner.getInstanceTaxonomyQuietly();

		Writer writer = new OutputStreamWriter(System.out);
		
		assertTrue(taxonomy.getTypeNode(B).getAllInstanceNodes().contains(taxonomy.getInstanceNode(ind)));
		
		reasoner.setIncrementalMode(true);

		changeLoader.clear();
		changeLoader.remove(axASubB);

		taxonomy = reasoner.getInstanceTaxonomyQuietly();
		
		TaxonomyPrinter.dumpInstanceTaxomomy(taxonomy, writer, false);
		writer.flush();
		
		assertFalse(taxonomy.getTypeNode(B).getAllInstanceNodes().contains(taxonomy.getInstanceNode(ind)));
	}	
	
	@Test
	public void testInvalidTaxonomyAfterInconsistency() throws ElkException, IOException {
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(loader, new PostProcessingStageExecutor());

		reasoner.setIncrementalMode(false);
		reasoner.registerOntologyChangesLoader(changeLoader);

		ElkClass A = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass B = objectFactory.getClass(new ElkFullIri(":B"));
		ElkNamedIndividual ind = objectFactory.getNamedIndividual(new ElkFullIri(":in"));
		ElkAxiom axiInstA = objectFactory.getClassAssertionAxiom(A, ind);
		ElkAxiom axTopSubB = objectFactory.getSubClassOfAxiom(objectFactory.getOwlThing(), B);
		ElkAxiom axDisj = objectFactory.getDisjointClassesAxiom(A, B);
		
		loader.add(axiInstA).add(axTopSubB).add(axDisj);

		InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = reasoner.getInstanceTaxonomyQuietly();
		

		Writer writer = new OutputStreamWriter(System.out);
		TaxonomyPrinter.dumpInstanceTaxomomy(taxonomy, writer, false);
		writer.flush();
		
		reasoner.setIncrementalMode(true);

		changeLoader.clear();
		changeLoader.remove(axTopSubB);

		taxonomy = reasoner.getInstanceTaxonomyQuietly();
		
		TaxonomyPrinter.dumpInstanceTaxomomy(taxonomy, writer, false);
		writer.flush();
		
		changeLoader.clear();
		changeLoader.add(axTopSubB);
		
		taxonomy = reasoner.getInstanceTaxonomyQuietly();
		
		TaxonomyPrinter.dumpInstanceTaxomomy(taxonomy, writer, false);
		writer.flush();
		
		changeLoader.clear();
		changeLoader.remove(axDisj);
		
		taxonomy = reasoner.getInstanceTaxonomyQuietly();
		
		TaxonomyPrinter.dumpInstanceTaxomomy(taxonomy, writer, false);
		writer.flush();
	}		
	
	

}
