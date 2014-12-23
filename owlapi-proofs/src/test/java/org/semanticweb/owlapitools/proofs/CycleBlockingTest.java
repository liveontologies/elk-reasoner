/**
 * 
 */
package org.semanticweb.owlapitools.proofs;
/*
 * #%L
 * OWL API Proofs Model
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

import static org.junit.Assert.assertEquals;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.A;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.B;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.C;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.D;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.FACTORY;

import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.CycleBlockingExpression;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class CycleBlockingTest {
	
	private static final String INF_PREFIX = "inf";

	@Test
	public void blockCyclicProof() throws Exception {
		MockOWLAxiomExpression aSubB = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(A, B), false);
		MockOWLAxiomExpression aSubD = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(A, D), false);
		MockOWLAxiomExpression aSubBAndC = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(A, FACTORY.getOWLObjectIntersectionOf(B, C)));
		MockOWLAxiomExpression bSubD = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(B, D));
		MockOWLAxiomExpression dSubC = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(D, C));
		MockOWLAxiomExpression aSubC = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(A, C), false);
		MockOWLAxiomExpression cSubB = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(C, B), false);
		
		aSubB
			.addInference(new MockOWLInference(INF_PREFIX, aSubB, Arrays.<OWLExpression>asList(aSubBAndC)))
			.addInference(new MockOWLInference(INF_PREFIX, aSubB, Arrays.<OWLExpression>asList(cSubB, aSubC)));

		aSubC.addInference(new MockOWLInference(INF_PREFIX, aSubC, Arrays.<OWLExpression>asList(aSubD, dSubC)));
		aSubD.addInference(new MockOWLInference(INF_PREFIX, aSubD, Arrays.<OWLExpression>asList(aSubB, bSubD)));		
		
		CycleBlockingExpression root = new CycleBlockingExpression(aSubB, OWLProofUtils.computeInferenceGraph(aSubB));
		
		assertEquals(2, getNumberOfInferences(aSubB));
		// only one inference remains since the other is loopy
		assertEquals(1, getNumberOfInferences(root));
	}

	private Object getNumberOfInferences(OWLExpression expr) throws ProofGenerationException {
		int cnt = 0;
		
		for (@SuppressWarnings("unused") OWLInference inf : expr.getInferences()) {
			cnt++;
		}
		
		return cnt;
	}
}
