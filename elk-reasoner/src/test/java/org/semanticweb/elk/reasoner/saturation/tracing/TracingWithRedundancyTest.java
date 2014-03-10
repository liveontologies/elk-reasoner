/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

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

import org.junit.Test;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.incremental.TestChangesLoader;

/**
 * Tests that the tracing is correct and complete even when the closure is
 * computed w.r.t. non-redundant rules and then incrementally expanded using
 * redundant rules.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingWithRedundancyTest {

	@Test
	public void testComplexExistential() throws Exception {
		//load and classify
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/ComplexExistential.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		//now add  A => R some (R some C) to which the decomposition rules should not be applied
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass c = factory.getClass(new ElkFullIri("http://example.org/C"));
		ElkObjectProperty r = factory.getObjectProperty(new ElkFullIri("http://example.org/R"));
		ElkClassExpression rSomeRSomeC = factory.getObjectSomeValuesFrom(r, factory.getObjectSomeValuesFrom(r, c));
		
		reasoner.setAllowIncrementalMode(true);
		TestChangesLoader changeLoader = new TestChangesLoader();
		reasoner.registerAxiomLoader(changeLoader);

		changeLoader.add(factory.getSubClassOfAxiom(a, rSomeRSomeC));
		
		reasoner.getTaxonomyQuietly();
		//now test that when we trace A, we also trace inferences in R some (R some C)
		reasoner.explainSubsumption(a, rSomeRSomeC);
		// will fail if no context for R some (R some C) has been created
		TracingTestUtils.checkNumberOfInferences(rSomeRSomeC, rSomeRSomeC, reasoner, 1);
	}
	
}
