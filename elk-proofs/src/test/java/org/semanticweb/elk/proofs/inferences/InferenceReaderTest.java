/**
 * 
 */
package org.semanticweb.elk.proofs.inferences;

import org.junit.Test;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.proofs.inferences.mapping.InferenceMapper;
import org.semanticweb.elk.proofs.utils.InferencePrinter;
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
 * Basic tests for various {@link InferenceReader}s and {@link InferenceMapper}.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class InferenceReaderTest {
	
	private final static ElkObjectFactory factory_ = new ElkObjectFactoryImpl();
	
	@Test
	public void simpleChainMapping() throws Exception {
		Reasoner reasoner = TestReasonerUtils.loadAndClassify("PropertyCompositions.owl");
		ElkClass a = factory_.getClass(new ElkFullIri("http://example.org/A"));
		ElkClass g = factory_.getClass(new ElkFullIri("http://example.org/G"));
		
		RecursiveInferenceVisitor.visitInferences(reasoner, a, g, new AbstractInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(Inference inference, Void input) {
				System.out.println(InferencePrinter.print(inference));
				return null;
			}
			
		});
		
		reasoner.shutdown();
	}
	
}
