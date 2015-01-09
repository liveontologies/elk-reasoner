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
import static org.junit.Assert.fail;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.A;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.B;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.C;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.D;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.E;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.F;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.FACTORY;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.G;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.R1;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.R2;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.R3;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.R4;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.R5;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.S;

import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;
import org.semanticweb.owlapitools.proofs.util.CycleBlockingExpression;
import org.semanticweb.owlapitools.proofs.util.LazyLemmaElimination;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;
import org.semanticweb.owlapitools.proofs.util.TransformedOWLExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class OWLExpressionTests {
	
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
	
	@Test
	public void lemmaEliminationRoleChain() throws Exception {
		MockOWLAxiomExpression aSubG = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(A, G));
		MockOWLAxiomExpression aSubR1B = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(A, FACTORY.getOWLObjectSomeValuesFrom(R1, B)));
		MockOWLAxiomExpression bSubR2C = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(B, FACTORY.getOWLObjectSomeValuesFrom(R2, C)));
		MockOWLAxiomExpression cSubR3D = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(C, FACTORY.getOWLObjectSomeValuesFrom(R3, D)));
		MockOWLAxiomExpression dSubR4E = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(D, FACTORY.getOWLObjectSomeValuesFrom(R4, E)));
		MockOWLAxiomExpression eSubR5F = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(E, FACTORY.getOWLObjectSomeValuesFrom(R5, F)));
		MockOWLAxiomExpression aSubSF = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(A, FACTORY.getOWLObjectSomeValuesFrom(S, F)));
		MockOWLAxiomExpression sFSubG = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(FACTORY.getOWLObjectSomeValuesFrom(S, F), G));
		//lemmas
		MockOWLLemmaExpression dSubR4R5F = new MockOWLLemmaExpression("D SubClassOf R4 o R5 some F");
		MockOWLLemmaExpression cSubR3R4R5F = new MockOWLLemmaExpression("C SubClassOf R3 o R4 o R5 some F");
		MockOWLLemmaExpression bSubR2R3R4R5F = new MockOWLLemmaExpression("B SubClassOf R2 o R3 o R4 o R5 some F");
		
		aSubG.addInference(new MockOWLInference(INF_PREFIX, aSubG, Arrays.<OWLExpression>asList(aSubSF, sFSubG)));
		aSubSF.addInference(new MockOWLInference(INF_PREFIX, aSubSF, Arrays.<OWLExpression>asList(aSubR1B, bSubR2R3R4R5F)));
		bSubR2R3R4R5F.addInference(new MockOWLInference(INF_PREFIX, bSubR2R3R4R5F, Arrays.<OWLExpression>asList(bSubR2C, cSubR3R4R5F)));
		cSubR3R4R5F.addInference(new MockOWLInference(INF_PREFIX, cSubR3R4R5F, Arrays.<OWLExpression>asList(cSubR3D, dSubR4R5F)));
		dSubR4R5F.addInference(new MockOWLInference(INF_PREFIX, dSubR4R5F, Arrays.<OWLExpression>asList(dSubR4E, eSubR5F)));
		
		TransformedOWLExpression<?> root = new TransformedOWLExpression<LazyLemmaElimination>(aSubG, new LazyLemmaElimination());
		
		OWLProofUtils.visitExpressionsInProofGraph(root, new OWLExpressionVisitor<Void>() {

			@Override
			public Void visit(OWLAxiomExpression expression) {
				System.err.println(expression);
				return null;
			}

			@Override
			public Void visit(OWLLemmaExpression expression) {
				fail("all lemmas should have been eliminated but got this: " + expression);
				return null;
			}
			
		});
	}

	private Object getNumberOfInferences(OWLExpression expr) throws ProofGenerationException {
		int cnt = 0;
		
		for (@SuppressWarnings("unused") OWLInference inf : expr.getInferences()) {
			cnt++;
		}
		
		return cnt;
	}
}
