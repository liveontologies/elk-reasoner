/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class LowLevelIncrementalReasoningTest {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();
	
	@Test
	public void testBasicDeletion() throws ElkException {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(new LoggingStageExecutor(), 1);
		TestChangesLoader loader = new TestChangesLoader();
		
		reasoner.registerOntologyLoader(loader);
		
		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri("R"));
		
		loader.add(objectFactory.getSubClassOfAxiom(b, d))
			.add(objectFactory.getSubClassOfAxiom(a, objectFactory.getObjectSomeValuesFrom(r, b)))
			.add(objectFactory.getSubClassOfAxiom(objectFactory.getObjectSomeValuesFrom(r, d), c));

		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomy();
		
		assertTrue(taxonomy.getNode(a).getDirectSuperNodes().contains(taxonomy.getNode(c)));
		// now delete B [= D, should retract A [= C
		loader.clear();
		
		reasoner.setIncrementalMode(true);
		reasoner.registerOntologyChangesLoader(loader);
		
		loader.remove(objectFactory.getSubClassOfAxiom(b, d));
		
		taxonomy = reasoner.getTaxonomy();
		
		assertFalse(taxonomy.getNode(a).getDirectSuperNodes().contains(taxonomy.getNode(c)));
	}
}

