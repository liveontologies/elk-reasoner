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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;
import org.semanticweb.owlapitools.proofs.util.CycleBlocking;
import org.semanticweb.owlapitools.proofs.util.GenericLemmaElimination;
import org.semanticweb.owlapitools.proofs.util.OWLInferenceGraph;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;
import org.semanticweb.owlapitools.proofs.util.TransformedOWLAxiomExpression;

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
		
		OWLExpression root = new TransformedOWLAxiomExpression<CycleBlocking>(aSubB, new CycleBlocking(aSubB, OWLProofUtils.computeInferenceGraph(aSubB))); 
				
		assertEquals(2, getNumberOfInferences(aSubB));
		// only one inference remains since the other is cyclic
		assertEquals(1, getNumberOfInferences(root));
	}
	
	@Test
	public void blockCyclicProof2() throws Exception {
		MockOWLAxiomExpression root = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(A, B), false);		
		MockOWLAxiomExpression inf1P1 = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(B, C));
		MockOWLAxiomExpression inf1P2 = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(C, D), false);
		MockOWLAxiomExpression inf2P1 = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(D, E));
		MockOWLAxiomExpression inf2P2 = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(C, E));
		
		root
			.addInference(new MockOWLInference(INF_PREFIX, root, Arrays.<OWLExpression>asList(inf1P1, inf1P2)))
			.addInference(new MockOWLInference(INF_PREFIX, root, Arrays.<OWLExpression>asList(inf2P1, inf2P2)));

		inf1P2.addInference(new MockOWLInference(INF_PREFIX, inf1P2, Arrays.<OWLExpression>asList(root, root)));
		
		OWLInferenceGraph graph = OWLProofUtils.computeInferenceGraph(root);
		
		//System.err.println(graph);
		
		OWLExpression blocked = new TransformedOWLAxiomExpression<CycleBlocking>(root, new CycleBlocking(root, graph)); 
				
		assertEquals(2, getNumberOfInferences(root));
		// only one inference remains since the other is cyclic
		assertEquals(1, getNumberOfInferences(blocked));
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
		
		OWLExpression root = new TransformedOWLAxiomExpression<GenericLemmaElimination>(aSubG, new GenericLemmaElimination());
		
		OWLProofUtils.visitExpressionsInProofGraph(root, new OWLExpressionVisitor<Void>() {

			@Override
			public Void visit(OWLAxiomExpression expression) {
				//System.err.println(expression);
				return null;
			}

			@Override
			public Void visit(OWLLemmaExpression expression) {
				fail("all lemmas should have been eliminated but got this: " + expression);
				return null;
			}
			
		});
	}
	
	@Test
	public void lemmaEliminationMultipleInferences() throws Exception {
		// auxiliary classes
		OWLClass B1 = FACTORY.getOWLClass(IRI.create(TestVocabulary.PREFIX + "B1"));
		OWLClass B2 = FACTORY.getOWLClass(IRI.create(TestVocabulary.PREFIX + "B2"));
		OWLClass D1 = FACTORY.getOWLClass(IRI.create(TestVocabulary.PREFIX + "D1"));
		OWLClass D2 = FACTORY.getOWLClass(IRI.create(TestVocabulary.PREFIX + "D2"));
		// lemmas
		MockOWLLemmaExpression aSubR1R2B = new MockOWLLemmaExpression("A SubClassOf R1 o R2 some B");
		MockOWLLemmaExpression cSubR4R5D = new MockOWLLemmaExpression("C SubClassOf R4 o R5 some D");
		// axioms
		MockOWLAxiomExpression aSubSE = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(A, FACTORY.getOWLObjectSomeValuesFrom(S, B)));
		MockOWLAxiomExpression bSubR3C = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(B, FACTORY.getOWLObjectSomeValuesFrom(R3, C)));
		// two pairs of premises for A => R1 o R2 some B
		MockOWLAxiomExpression aSubR1B1 = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(A, FACTORY.getOWLObjectSomeValuesFrom(R1, B1)));
		MockOWLAxiomExpression b1SubR2B = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(B1, FACTORY.getOWLObjectSomeValuesFrom(R2, B)));
		MockOWLAxiomExpression aSubR1B2 = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(A, FACTORY.getOWLObjectSomeValuesFrom(R1, B2)));
		MockOWLAxiomExpression b2SubR2B = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(B2, FACTORY.getOWLObjectSomeValuesFrom(R2, B)));
		// two pairs of premises for C => R4 o R5 some D
		MockOWLAxiomExpression cSubR4D1 = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(C, FACTORY.getOWLObjectSomeValuesFrom(R4, D1)));
		MockOWLAxiomExpression d1SubR5D = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(D1, FACTORY.getOWLObjectSomeValuesFrom(R5, D)));
		MockOWLAxiomExpression cSubR4D2 = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(C, FACTORY.getOWLObjectSomeValuesFrom(R4, D2)));
		MockOWLAxiomExpression d2SubR5D = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(D2, FACTORY.getOWLObjectSomeValuesFrom(R5, D)));
		// premises = two lemmas and one axiom
		aSubSE.addInference(new MockOWLInference(INF_PREFIX, aSubSE, Arrays.<OWLExpression>asList(aSubR1R2B, bSubR3C, cSubR4R5D)));
		// now the inferences for the lemmas
		aSubR1R2B
			.addInference(new MockOWLInference(INF_PREFIX, aSubR1R2B, Arrays.<OWLExpression>asList(aSubR1B1, b1SubR2B)))
			.addInference(new MockOWLInference(INF_PREFIX, aSubR1R2B, Arrays.<OWLExpression>asList(aSubR1B2, b2SubR2B)));
		cSubR4R5D
			.addInference(new MockOWLInference(INF_PREFIX, cSubR4R5D, Arrays.<OWLExpression>asList(cSubR4D1, d1SubR5D)))
			.addInference(new MockOWLInference(INF_PREFIX, cSubR4R5D, Arrays.<OWLExpression>asList(cSubR4D2, d2SubR5D)));
		
		OWLExpression root = new TransformedOWLAxiomExpression<GenericLemmaElimination>(aSubSE, new GenericLemmaElimination());
		
		OWLProofUtils.visitExpressionsInProofGraph(root, new OWLExpressionVisitor<Void>() {

			@Override
			public Void visit(OWLAxiomExpression expression) {
				//System.err.println(expression);
				return null;
			}

			@Override
			public Void visit(OWLLemmaExpression lemma) {
				fail("all lemmas should have been eliminated but got this: " + lemma);
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
