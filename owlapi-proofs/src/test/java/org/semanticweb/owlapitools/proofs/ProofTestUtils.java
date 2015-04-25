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

	public static MockOWLAxiomExpression generateRandomProofGraph(Random rnd, int maxInferencesForExpression, int maxPremisesForInference, int totalExpressionLimit) {
		int exprCount = 0;
		Queue<MockOWLAxiomExpression> toDo = new ArrayDeque<MockOWLAxiomExpression>();
		MockOWLAxiomExpression root = new MockOWLAxiomExpression(FACTORY.getOWLSubClassOfAxiom(clazz("sub"), clazz("sup")), false);
		
		toDo.add(root);
		
		for (;;) {
			MockOWLAxiomExpression next = toDo.poll();
			
			if (next == null) {
				break;
			}
			// 0 inferences is allowed
			int numOfInf = rnd.nextInt(maxInferencesForExpression + 1);
			
			for (int i = 0; i < numOfInf; i++) {
				// generate new "inference"
				int numOfPremises = 1 + rnd.nextInt(maxPremisesForInference);
				List<MockOWLAxiomExpression> premises = new ArrayList<MockOWLAxiomExpression>(numOfPremises);
				
				for (int j = 0; j < numOfPremises; j++) {
					OWLClass sub = FACTORY.getOWLClass(IRI.create("http://random.org/" + randomString(rnd, 4)));
					OWLClass sup = FACTORY.getOWLClass(IRI.create("http://random.org/" + randomString(rnd, 4)));
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
	
	private static String randomString(Random rnd, int len) {
		StringBuilder builder = new StringBuilder(len);
		String alphabet = "abc1de2fg3hi4jk5lmn6opq7rs8tuv9wx0yz";
		
		for (int i = 0; i < len; i++) {
			builder.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
		}
			
		return builder.toString();
	}

	private static OWLClass clazz(String suffix) {
		return FACTORY.getOWLClass(IRI.create(PREFIX + suffix));
	}
	
	public static OWLExpression pickRandomExpression(final OWLExpression root, final Random rnd) throws ProofGenerationException {
		/*// traverses the tree twice, probably slow but should be OK for testing
		int size = getProofGraphSize(root);
		
		if (size <= 1) {
			// can't pick the root so exit
			return null;
		}
		
		// pick the i_th according to some fixed order, doesn't pick the root
		final int index = 1 + rnd.nextInt(size - 1);
		final AtomicInteger counter = new AtomicInteger(0);
		final AtomicReference<OWLExpression> ref = new AtomicReference<OWLExpression>();
		
		OWLProofUtils.visitExpressionsInProofGraph(root, new OWLExpressionVisitor<Void>() {

			@Override
			public Void visit(OWLAxiomExpression expression) {
				if (counter.get() == index) {
					ref.set((OWLExpression) expression); 
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
		
		return ref.get();*/
		
		if (!root.getInferences().iterator().hasNext()) {
			// never pick the root
			return null;
		}
		
		final AtomicInteger counter = new AtomicInteger(0);
		final AtomicReference<OWLExpression> ref = new AtomicReference<OWLExpression>();
		
		OWLProofUtils.visitExpressionsInProofGraph(root, new OWLExpressionVisitor<Void>() {

			@Override
			public Void visit(OWLAxiomExpression expression) {
				int cnt = counter.incrementAndGet();
				
				if (ref.get() == null) {
					ref.set(expression);
				}
				else {
					double chance = 1d / cnt;
					
					if (chance > rnd.nextDouble()) {
						ref.set(expression);
					}
				}
				
				return null;
			}

			@Override
			public Void visit(OWLLemmaExpression expression) {
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
