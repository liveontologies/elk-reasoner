/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.mapping;

import org.junit.Test;
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
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.utils.InferencePrinter;
import org.semanticweb.elk.proofs.utils.ProofUtils;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;

/**
 * Basic tests for {@link InferenceMapper}.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class InferenceMapperTest {
	
	@Test
	public void testSimpleChainMapping() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("PropertyCompositions.owl");
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass a = factory.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass g = factory.getClass(new ElkFullIri("http://example.org/G"));
		
		reasoner.explainSubsumption(a, g);
		// now do mapping
		ProofUtils.visitProofs(reasoner, a, g, new AbstractInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(Inference inference, Void input) {
				
				System.out.println(InferencePrinter.print(inference));
				
				return null;
			}
			
		});
	}
	
	@Test
	public void testExistentialMapping() {
		
	}

}
