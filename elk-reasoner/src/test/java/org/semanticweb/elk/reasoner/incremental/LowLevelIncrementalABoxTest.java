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
package org.semanticweb.elk.reasoner.incremental;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.loading.TestAxiomLoaderFactory;
import org.semanticweb.elk.loading.TestChangesLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class LowLevelIncrementalABoxTest {

	final ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();

	@Test
	public void testBasicDeletion() throws ElkException, IOException {
		TestChangesLoader loader = new TestChangesLoader();		
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(loader, new PostProcessingStageExecutor());

		reasoner.setAllowIncrementalMode(false);		

		ElkClass A = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass B = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass C = objectFactory.getClass(new ElkFullIri(":C"));
		ElkNamedIndividual ind = objectFactory.getNamedIndividual(new ElkFullIri(":in"));
		ElkAxiom axiInstA = objectFactory.getClassAssertionAxiom(A, ind);
		ElkAxiom axASubB = objectFactory.getSubClassOfAxiom(A, B);
		ElkAxiom axCSubB = objectFactory.getSubClassOfAxiom(C, B);
		
		loader.add(axiInstA).add(axASubB).add(axCSubB);

		InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = reasoner.getInstanceTaxonomyQuietly();

		//Writer writer = new OutputStreamWriter(System.out);
		
		assertTrue(taxonomy.getNode(B).getAllInstanceNodes().contains(taxonomy.getInstanceNode(ind)));
		
		reasoner.setAllowIncrementalMode(true);
		
		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		//changeLoader.clear();
		changeLoader.remove(axASubB);

		taxonomy = reasoner.getInstanceTaxonomyQuietly();
		
		//TaxonomyPrinter.dumpInstanceTaxomomy(taxonomy, writer, false);
		//writer.flush();
		
		assertFalse(taxonomy.getNode(B).getAllInstanceNodes().contains(taxonomy.getInstanceNode(ind)));
	}	
	
	@Test
	public void testInvalidTaxonomyAfterInconsistency() throws ElkException, IOException {
		TestChangesLoader loader = new TestChangesLoader();
		TestChangesLoader changeLoader = new TestChangesLoader();
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(loader, new PostProcessingStageExecutor());

		reasoner.setAllowIncrementalMode(false);
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		ElkClass A = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass B = objectFactory.getClass(new ElkFullIri(":B"));
		ElkNamedIndividual ind = objectFactory.getNamedIndividual(new ElkFullIri(":in"));
		ElkAxiom axiInstA = objectFactory.getClassAssertionAxiom(A, ind);
		ElkAxiom axTopSubB = objectFactory.getSubClassOfAxiom(objectFactory.getOwlThing(), B);
		ElkAxiom axDisj = objectFactory.getDisjointClassesAxiom(A, B);
		
		loader.add(axiInstA).add(axTopSubB).add(axDisj);

		reasoner.getInstanceTaxonomyQuietly();
		
		reasoner.setAllowIncrementalMode(true);
		changeLoader.remove(axTopSubB);
		
		reasoner.getInstanceTaxonomyQuietly();
		
		changeLoader.add(axTopSubB);
		
		reasoner.getInstanceTaxonomyQuietly();
		changeLoader.remove(axDisj);
		
		reasoner.getInstanceTaxonomyQuietly();
	}		
	
	
	@Test
	public void testRemoveIndividual() throws ElkException, IOException {
		TestChangesLoader loader = new TestChangesLoader();		
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(loader, new PostProcessingStageExecutor());

		reasoner.setAllowIncrementalMode(false);		

		ElkClass A = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass B = objectFactory.getClass(new ElkFullIri(":B"));
		ElkNamedIndividual ind = objectFactory.getNamedIndividual(new ElkFullIri(":in"));
		ElkAxiom axiInstA = objectFactory.getClassAssertionAxiom(A, ind);
		ElkAxiom axASubB = objectFactory.getSubClassOfAxiom(A, B);
		
		loader.add(axiInstA).add(axASubB);

		InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = reasoner.getInstanceTaxonomyQuietly();
		
		assertTrue(taxonomy.getNode(A).getDirectInstanceNodes().size() == 1);
		assertTrue(taxonomy.getNode(B).getAllInstanceNodes().size() == 1);
		
		reasoner.setAllowIncrementalMode(true);

		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));
		
		//changeLoader.clear();
		changeLoader.remove(axiInstA);

		taxonomy = reasoner.getInstanceTaxonomyQuietly();
		//the individual should be gone
		assertTrue(taxonomy.getNode(A).getDirectInstanceNodes().isEmpty());
		assertTrue(taxonomy.getNode(B).getAllInstanceNodes().isEmpty());
		//check that the individual properly re-appears
		//changeLoader.clear();
		
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));
		
		changeLoader.add(axiInstA);
		
		taxonomy = reasoner.getInstanceTaxonomyQuietly();
		
		assertTrue(taxonomy.getNode(A).getDirectInstanceNodes().contains(taxonomy.getInstanceNode(ind)));
		assertTrue(taxonomy.getNode(B).getAllInstanceNodes().contains(taxonomy.getInstanceNode(ind)));
	}	
	
	@Test
	public void testNewIndividual() throws ElkException, IOException {
		TestChangesLoader loader = new TestChangesLoader();		
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(loader, new PostProcessingStageExecutor());

		reasoner.setAllowIncrementalMode(false);
		

		ElkClass A = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass B = objectFactory.getClass(new ElkFullIri(":B"));
		ElkNamedIndividual ind = objectFactory.getNamedIndividual(new ElkFullIri(":in"));
		ElkNamedIndividual newInd = objectFactory.getNamedIndividual(new ElkFullIri(":new"));
		ElkAxiom axiInstA = objectFactory.getClassAssertionAxiom(A, ind);
		ElkAxiom axNewInstB = objectFactory.getClassAssertionAxiom(B, newInd);
		ElkAxiom axASubB = objectFactory.getSubClassOfAxiom(A, B);
		
		loader.add(axiInstA).add(axASubB);

		InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = reasoner.getInstanceTaxonomyQuietly();
		
		assertTrue(taxonomy.getNode(A).getDirectInstanceNodes().size() == 1);
		assertTrue(taxonomy.getNode(B).getAllInstanceNodes().size() == 1);
		
		reasoner.setAllowIncrementalMode(true);		
		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(changeLoader));

		//changeLoader.clear();
		changeLoader.add(axNewInstB);

		taxonomy = reasoner.getInstanceTaxonomyQuietly();
		
		assertTrue(taxonomy.getNode(A).getDirectInstanceNodes().size() == 1);
		assertTrue(taxonomy.getNode(B).getAllInstanceNodes().contains(taxonomy.getInstanceNode(ind)));
		assertTrue(taxonomy.getNode(B).getDirectInstanceNodes().contains(taxonomy.getInstanceNode(newInd)));
	}	
}
