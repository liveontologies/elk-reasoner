/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkIri;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class StructuralHasherTest {

	@Test
	public void axioms() {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		ElkClass a = factory.getClass(getIri("A"));
		ElkClass b = factory.getClass(getIri("B"));
		ElkClass c = factory.getClass(getIri("C"));
		//ElkClass d = factory.getClass(getIri("D"));
		//ElkClass e = factory.getClass(getIri("E"));
		// disjointness
		assertEquals(StructuralEquivalenceHasher.hashCode(factory.getDisjointClassesAxiom(Arrays.asList(a, b, c))), 
				StructuralEquivalenceHasher.hashCode(factory.getDisjointClassesAxiom(Arrays.asList(a, b, c))));
		
		
	}
	
	private ElkIri getIri(String fragment) {
		return new ElkFullIri(TestEntities.prefix_.getIri().getFullIriAsString() + fragment);
	}
}
