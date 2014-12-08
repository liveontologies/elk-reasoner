/**
 * 
 */
package org.semanticweb.elk.proofs.inferences;

import org.junit.Test;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.proofs.utils.InferencePrinter;
import org.semanticweb.elk.proofs.utils.RecursiveInferenceVisitor;
import org.semanticweb.elk.proofs.utils.TestUtils;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
/*
 * #%L
 * ELK Proofs Package
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

/**
 * Basic low-level tests for reading inferences.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class InferenceReaderTest {
	
	private final static ElkObjectFactory factory_ = new ElkObjectFactoryImpl();
	
	@Test
	public void simpleChainMapping() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("ontologies/PropertyCompositions.owl");
		ElkClass a = factory_.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass g = factory_.getClass(new ElkFullIri("http://example.org/G"));
		
		TestUtils.provabilityTest(reasoner, a, g);
		
		reasoner.shutdown();
	}
	
	@Test(expected=AssertionError.class)
	public void noProof() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("ontologies/PropertyCompositions.owl");
		ElkClass a = factory_.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass notDerived = factory_.getClass(new ElkFullIri("http://example.org/Invalid"));
		
		try {
			TestUtils.provabilityTest(reasoner, a, notDerived);
		}
		finally {
			reasoner.shutdown();
		}
	}
	
	@Test
	public void basicTest() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/DeepPropertyHierarchy.owl");
		ElkClass sub = factory_.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass sup = factory_.getClass(new ElkFullIri("http://example.org/D"));
		// print inferences 
		RecursiveInferenceVisitor.visitInferences(reasoner, sub, sup, new AbstractInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(Inference inference, Void input) {
				System.out.println(InferencePrinter.print(inference));
				return null;
			}
			
		});
		
		TestUtils.provabilityTest(reasoner, sub, sup);
		
		reasoner.shutdown();
	}
	
}
