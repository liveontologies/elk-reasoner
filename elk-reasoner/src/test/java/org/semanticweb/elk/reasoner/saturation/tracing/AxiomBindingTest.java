/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.iris.ElkAbbreviatedIri;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collections of tests checking that we can look up asserted axioms as side
 * conditions of traced inferences.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class AxiomBindingTest {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AxiomBindingTest.class);

	@Rule
	public TestName testName = new TestName();

	@Before
	public void beforeTest() {
		LOGGER_.trace("Starting test {}", testName.getMethodName());
	}

	@After
	public void afterTest() {
		LOGGER_.trace("Finishing test {}", testName.getMethodName());
	}

	@Test
	public void testBasicSubsumptionAxiomLookup() throws Exception {
		final ElkPrefix prefix = new ElkPrefix(":", new ElkFullIri("http://example.org/"));
		final Reasoner reasoner = TestReasonerUtils.loadAndClassify("tracing/DuplicateExistential.owl");
		final ElkObjectFactory factory = new ElkObjectFactoryImpl();
		final ElkClass a = factory.getClass(new ElkAbbreviatedIri(prefix, "A"));
		final ElkClass c = factory.getClass(new ElkAbbreviatedIri(prefix, "C"));
		final ElkClass d = factory.getClass(new ElkAbbreviatedIri(prefix, "D"));
		final ElkObjectProperty r = factory.getObjectProperty(new ElkAbbreviatedIri(prefix, "R"));
		final ElkClassExpression rSomeC = factory.getObjectSomeValuesFrom(r, c);
		final String expected = OwlFunctionalStylePrinter.toString(factory.getSubClassOfAxiom(rSomeC, d));

		TracingTestUtils.checkSideConditions(a, d, reasoner, new AbstractElkAxiomVisitor<Boolean>() {

			@Override
			public Boolean visit(ElkSubClassOfAxiom axiom) {
				//System.out.println(OwlFunctionalStylePrinter.toString(axiom));
				// looking up SubClassOf(ObjectSomeValuesFrom(:R :C) :D)
				// TODO create a utility method for recursive equality checking 
				return OwlFunctionalStylePrinter.toString(axiom).equals(expected);
			}
			
		});
	}

}
