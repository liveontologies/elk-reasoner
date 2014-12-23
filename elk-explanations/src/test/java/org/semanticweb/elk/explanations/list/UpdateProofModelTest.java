/**
 * 
 */
package org.semanticweb.elk.explanations.list;
/*
 * #%L
 * Explanation Workbench
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.A;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.B;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.C;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.D;
import static org.semanticweb.owlapitools.proofs.TestVocabulary.FACTORY;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;

import org.junit.Test;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.MockOWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.MockOWLInference;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.ProofTestUtils;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.CycleBlockingExpression;
import org.semanticweb.owlapitools.proofs.util.OWLInferenceGraph;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;

/**
 * Tests to verify that the {@link ProofFrameList}'s model consisting of
 * sections and rows is correctly updated when some {@link OWLInference}s are
 * added or deleted.
 * 
 * @author Pavel Klinov pavel.klinov@uni-ulm.de
 *
 */
public class UpdateProofModelTest {

	@Test
	public void noLongerEntailed() throws ProofGenerationException {
		OWLAxiom aSubC = FACTORY.getOWLSubClassOfAxiom(A, C);
		OWLAxiom aSubB = FACTORY.getOWLSubClassOfAxiom(A, B);
		OWLAxiom bSubC = FACTORY.getOWLSubClassOfAxiom(B, C);
		
		OWLExpression premise1 = new MockOWLAxiomExpression(aSubB);
		OWLExpression premise2 = new MockOWLAxiomExpression(bSubC);
		MockOWLAxiomExpression root = new MockOWLAxiomExpression(aSubC, false);
		
		root.addInference(new MockOWLInference("subsumption", root, Arrays.<OWLExpression>asList(premise1, premise2)));

		OWLInferenceGraph iGraph = OWLProofUtils.computeInferenceGraph(root);
		
		ProofFrame frame = new ProofFrame(new CycleBlockingExpression(root, iGraph), new TestOWLRenderer(), null);
		ProofFrameSection header = (ProofFrameSection) frame.getFrameSections().get(0);
		// A <= C is entailed
		assertEquals(1, header.getRows().size());
		// now emulate removal of a premise, the root expression can no longer be entailed
		frame.blockInferencesForPremise(premise1);
		
		assertEquals(0, header.getRows().size());
	}
	
	@Test
	public void alternativeInferenceRemoved() throws ProofGenerationException {
		OWLAxiom aSubC = FACTORY.getOWLSubClassOfAxiom(A, C);
		OWLAxiom aSubB = FACTORY.getOWLSubClassOfAxiom(A, B);
		OWLAxiom bSubC = FACTORY.getOWLSubClassOfAxiom(B, C);
		OWLAxiom aSubCAndD = FACTORY.getOWLSubClassOfAxiom(A, FACTORY.getOWLObjectIntersectionOf(C, D));
		
		OWLExpression premise11 = new MockOWLAxiomExpression(aSubB);
		OWLExpression premise12 = new MockOWLAxiomExpression(bSubC);
		OWLExpression premise21 = new MockOWLAxiomExpression(aSubCAndD);
		MockOWLAxiomExpression root = new MockOWLAxiomExpression(aSubC, false);
		
		root
			.addInference(new MockOWLInference("subsumption", root, Arrays.<OWLExpression>asList(premise11, premise12)))
			.addInference(new MockOWLInference("and decomp", root, Arrays.<OWLExpression>asList(premise21)));
		
		OWLInferenceGraph iGraph = OWLProofUtils.computeInferenceGraph(root);
		
		ProofFrame frame = new ProofFrame(new CycleBlockingExpression(root, iGraph), new TestOWLRenderer(), null);
		ProofFrameSection header = (ProofFrameSection) frame.getFrameSections().get(0);
		// A <= C is entailed
		assertEquals(1, header.getRows().size());
		
		ProofFrameSectionRow rootRow = (ProofFrameSectionRow) header.getRows().get(0);
		
		rootRow.refillInferenceSections();
		// there are two inference sections
		assertEquals(2, rootRow.getInferenceSections().size());
		// now emulate removal of a premise, the root expression can no longer be entailed
		frame.blockInferencesForPremise(premise11);
		
		header = (ProofFrameSection) frame.getFrameSections().get(0);
		
		// A <= C is still entailed
		assertEquals(1, header.getRows().size());
		// there's only one inference section
		rootRow = (ProofFrameSectionRow) header.getRows().get(0);
		rootRow.refillInferenceSections();
		
		assertEquals(1, rootRow.getInferenceSections().size());
	}
	
	@Test
	public void randomChanges() throws Exception {
		long seed = 123;
		MockOWLAxiomExpression root = ProofTestUtils.generateRandomProofGraph(seed, 3, 3, 10);
		OWLInferenceGraph iGraph = OWLProofUtils.computeInferenceGraph(root);
		
		System.err.println(OWLProofUtils.printProofTree(root));
		
		ProofFrame frame = createProofModel(root);
		
		System.err.println(printProofModel(frame.getRootSection()));
		
		//assertMatch(root, (ProofFrameSectionRow) header.getRows().get(0));
		MockOWLAxiomExpression toDelete = ProofTestUtils.pickRandomExpression(root, seed);
		
		System.err.println("Deleting " + toDelete);
		
		OWLExpression newRoot = new CycleBlockingExpression(root, iGraph).blockExpression(toDelete);
		
		System.err.println(OWLProofUtils.printProofTree(newRoot));
		
		frame.blockInferencesForPremise(toDelete);
		
		System.err.println(printProofModel(frame.getRootSection()));
		
		assertMatch(newRoot, (ProofFrameSectionRow) frame.getRootSection().getRows().get(0));
	}
	

	
	ProofFrame createProofModel(OWLExpression root) throws ProofGenerationException {
		OWLInferenceGraph iGraph = OWLProofUtils.computeInferenceGraph(root);
		ProofFrame frame = new ProofFrame(new CycleBlockingExpression(root, iGraph), new TestOWLRenderer(), null);
		ProofFrameSection header = (ProofFrameSection) frame.getFrameSections().get(0);
		Queue<ProofFrameSection> toDo = new ArrayDeque<ProofFrameSection>();
		
		toDo.add(header);
		
		for (;;) {
			ProofFrameSection next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			next.refill(null);
			
			for (OWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom> row : next.getRows()) {
				ProofFrameSectionRow premiseRow = (ProofFrameSectionRow) row;
				
				premiseRow.refillInferenceSections();
				
				for (ProofFrameSection section : premiseRow.getInferenceSections()) {
					toDo.add(section);
				}
			}
		}
		
		return frame;
	}
	
	String printProofModel(ProofFrameSection rootSection) {
		StringBuilder builder = new StringBuilder();
		
		printSection(rootSection, builder, 0);
		
		return builder.toString();
	}
	
	void printSection(ProofFrameSection section, StringBuilder builder, int depth) {
		for (int i = 0; i < depth; i++) {
			builder.append("   ");
		}
		
		builder.append(section.getLabel()).append("\n");
		
		for (OWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom> row : section.getRows()) {
			ProofFrameSectionRow premiseRow = (ProofFrameSectionRow) row;
			
			for (int i = 0; i < depth + 1; i++) {
				builder.append("   ");
			}
			
			builder.append(premiseRow.toString()).append("\n");
			
			for (ProofFrameSection infSection : premiseRow.getInferenceSections()) {
				printSection(infSection, builder, depth + 2);	
			}
		}
	}
	
	void assertMatch(OWLExpression rootExpression, ProofFrameSectionRow rootRow) throws ProofGenerationException {
		//System.err.println("Checking " + rootExpression + " and " + rootRow);
		
		assertTrue("row for " + rootRow + " does not match the expression " + rootExpression, rootRow.match(rootExpression));
		// TODO tighten the check to make sure there's 1-1 correspondence
		for (ProofFrameSection section : rootRow.getInferenceSections()) {
			OWLInference inf = findMatchingInference(section, rootExpression);
			
			assertNotNull("No matching inference for " + section, inf);
			
			Iterator<? extends OWLExpression> premiseIter = inf.getPremises().iterator();
			
			for (OWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom> r : section.getRows()) {
				ProofFrameSectionRow row = (ProofFrameSectionRow) r;
				
				if (!premiseIter.hasNext()) {
					fail("Section doesn't match the inference");
				}
				
				OWLExpression premise = premiseIter.next();
				
				assertMatch(premise, row);
			}
		}
	}

	private OWLInference findMatchingInference(ProofFrameSection section, OWLExpression expr) throws ProofGenerationException {
		for (OWLInference inf : expr.getInferences()) {
			if (section.match(inf)) {
				return inf;
			}
		}
		
		return null;
	}


	
}
