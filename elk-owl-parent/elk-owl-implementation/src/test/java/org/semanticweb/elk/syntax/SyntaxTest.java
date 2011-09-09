/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.syntax;

import java.util.Arrays;

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;

import junit.framework.TestCase;

public class SyntaxTest extends TestCase {
	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	public SyntaxTest(String testName) {
		super(testName);
	}

	public void testFactory() {
		ElkClassExpression heart = objectFactory.getClass("Heart");
		ElkClassExpression organ = objectFactory.getClass("Organ");
		ElkClassExpression heart2 = objectFactory.getClass("Heart");

		assertSame("heart == heart2", heart, heart2);
		assertNotSame("heart != organ", organ);

		ElkClassExpression human = objectFactory.getClass("Human");
		ElkObjectPropertyExpression has = objectFactory
				.getObjectProperty("has");

		ElkClassExpression hasHeart = objectFactory.getObjectSomeValuesFrom(
				has, heart);
		ElkClassExpression hasOrgan = objectFactory.getObjectSomeValuesFrom(
				has, organ);
		ElkClassExpression hasHeart2 = objectFactory.getObjectSomeValuesFrom(
				has, heart2);

		assertSame("hasHeart == hasHeart2", hasHeart, hasHeart2);
		assertNotSame("hasHeart != hasOrgan", hasHeart, hasOrgan);

		ElkClassExpression heartAndOrgan = objectFactory
				.getObjectIntersectionOf(Arrays.asList(heart, organ));
		ElkClassExpression organAndHeart = objectFactory
				.getObjectIntersectionOf(Arrays.asList(organ, heart));
		ElkClassExpression heart2AndOrgan = objectFactory
				.getObjectIntersectionOf(Arrays.asList(heart2, organ));

		assertSame("heartAndOrgan == heart2AndOrgan", heartAndOrgan,
				heart2AndOrgan);
		assertNotSame("heartAndOrgan == organAndHeart", heartAndOrgan,
				organAndHeart);

		ElkClassAxiom humanHasHeart = objectFactory.getSubClassOfAxiom(human,
				hasHeart);
		ElkClassAxiom humanHasOrgan = objectFactory.getSubClassOfAxiom(human,
				hasOrgan);
		ElkClassAxiom humanHasHeart2 = objectFactory.getSubClassOfAxiom(human,
				hasHeart2);

		assertSame("humanHasHeart == humanHasHeart2", humanHasHeart,
				humanHasHeart2);
		assertNotSame("humanHasHeart != humanHasOrgan", humanHasHeart,
				humanHasOrgan);
	}
}
