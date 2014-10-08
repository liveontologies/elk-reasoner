/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived.entries;
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

import static org.junit.Assert.assertEquals;
import static org.semanticweb.elk.proofs.expressions.derived.entries.TestEntities.a;
import static org.semanticweb.elk.proofs.expressions.derived.entries.TestEntities.b;
import static org.semanticweb.elk.proofs.expressions.derived.entries.TestEntities.c;
import static org.semanticweb.elk.proofs.expressions.derived.entries.TestEntities.r;
import static org.semanticweb.elk.proofs.expressions.derived.entries.TestEntities.s;

import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.proofs.expressions.derived.entries.StructuralEquivalenceHasher;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaObjectFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.impl.ElkLemmaObjectFactoryImpl;

/**
 * TODO finish
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class StructuralHasherTest {

	private static final ElkObjectFactory objFactory_ = new ElkObjectFactoryImpl();
	private static final ElkLemmaObjectFactory lemmaFactory_ = new ElkLemmaObjectFactoryImpl();	
	
	@Test
	public void entities() {
		// complex existential
		ElkObjectPropertyChain rs = objFactory_.getObjectPropertyChain(Arrays.asList(r, s));
		
		assertEquals(StructuralEquivalenceHasher.hashCode(rs), StructuralEquivalenceHasher.hashCode(rs));
		assertEquals(StructuralEquivalenceHasher.hashCode(a), StructuralEquivalenceHasher.hashCode(a));
		assertEquals(StructuralEquivalenceHasher.hashCode(lemmaFactory_.getComplexObjectSomeValuesFrom(rs, b)), 
				StructuralEquivalenceHasher.hashCode(lemmaFactory_.getComplexObjectSomeValuesFrom(rs, b)));
	}
	
	@Test
	public void axioms() {
		// disjointness
		assertEquals(StructuralEquivalenceHasher.hashCode(objFactory_.getDisjointClassesAxiom(Arrays.asList(a, b, c))), 
				StructuralEquivalenceHasher.hashCode(objFactory_.getDisjointClassesAxiom(Arrays.asList(a, b, c))));
	}
	
	@Test
	public void lemmas() {
		ElkObjectPropertyChain rs = objFactory_.getObjectPropertyChain(Arrays.asList(r, s));
		
		assertEquals(StructuralEquivalenceHasher.hashCode(lemmaFactory_.getSubClassOfLemma(a, lemmaFactory_.getComplexObjectSomeValuesFrom(rs, b))), 
				StructuralEquivalenceHasher.hashCode(lemmaFactory_.getSubClassOfLemma(a, lemmaFactory_.getComplexObjectSomeValuesFrom(rs, b))));
	}
	
}
