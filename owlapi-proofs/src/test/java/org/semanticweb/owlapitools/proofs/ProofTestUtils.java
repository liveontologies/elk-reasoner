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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ProofTestUtils {
	
	private static final OWLDataFactory FACTORY = OWLManager.getOWLDataFactory();
	private static final String PREFIX = "http://example.com/";

	public static MockOWLAxiomExpression generateRandomProofGraph(long seed, int maxInferences, int maxPremises, int totalExpressionLimit) {
		int exprCount = 0;
		Queue<MockOWLAxiomExpression> toDo = new ArrayDeque<MockOWLAxiomExpression>();
		MockOWLAxiomExpression root = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(clazz("sub"), clazz("sup")), false);
		Random rnd = new Random(seed);
		
		toDo.add(root);
		
		for (;;) {
			MockOWLAxiomExpression next = toDo.poll();
			
			if (next == null) {
				break;
			}
			// 0 inferences is allowed
			int numOfInf = rnd.nextInt(maxInferences + 1);
			
			for (int i = 0; i < numOfInf; i++) {
				// generate new "inference"
				int numOfPremises = 1 + rnd.nextInt(maxPremises);
				List<MockOWLAxiomExpression> premises = new ArrayList<MockOWLAxiomExpression>(numOfPremises);
				
				for (int j = 0; j < numOfPremises; j++) {
					OWLClass sub = FACTORY.getOWLClass(IRI.create("http://random.org/" + UUID.randomUUID().toString().substring(0, 4)));
					OWLClass sup = FACTORY.getOWLClass(IRI.create("http://random.org/" + UUID.randomUUID().toString().substring(0, 4)));
					MockOWLAxiomExpression premise = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(sub, sup), false);
					
					premises.add(premise);
					toDo.add(premise);
					exprCount++;
				}
				
				MockOWLInference inf = new MockOWLInference("inference_" + i, next, premises);
				
				next.addInference(inf);
			}
			
			if (exprCount >= totalExpressionLimit) {
				break;
			}
		}
		
		return root;
	}
	
	private static OWLClass clazz(String suffix) {
		return FACTORY.getOWLClass(IRI.create(PREFIX + suffix));
	}
	
	public static MockOWLAxiomExpression pickRandomExpression(MockOWLAxiomExpression root, long seed) throws ProofGenerationException {
		// traverses the tree twice, probably slow but should be OK for testing
		Random rnd = new Random(seed);
		int size = getProofGraphSize(root);
		// pick the i_th according to some fixed order
		final int index = rnd.nextInt(size);
		final AtomicInteger counter = new AtomicInteger(0);
		final AtomicReference<MockOWLAxiomExpression> ref = new AtomicReference<MockOWLAxiomExpression>();
		
		OWLProofUtils.visitExpressionsInProofGraph(root, new OWLExpressionVisitor<Void>() {

			@Override
			public Void visit(OWLAxiomExpression expression) {
				if (counter.get() == index) {
					ref.set((MockOWLAxiomExpression) expression); 
				}
				
				counter.incrementAndGet();
				
				return null;
			}

			@Override
			public Void visit(OWLLemmaExpression expression) {
				counter.incrementAndGet();
				return null;
			}
			
		});
		
		return ref.get();
	}
	
	public static int getProofGraphSize(OWLExpression root) throws ProofGenerationException {
		final AtomicInteger counter = new AtomicInteger();
		
		OWLProofUtils.visitExpressionsInProofGraph(root, new OWLExpressionVisitor<Void>() {

			@Override
			public Void visit(OWLAxiomExpression expression) {
				counter.incrementAndGet();
				return null;
			}

			@Override
			public Void visit(OWLLemmaExpression expression) {
				counter.incrementAndGet();
				return null;
			}
			
		});
		
		return counter.get();
	}
}
