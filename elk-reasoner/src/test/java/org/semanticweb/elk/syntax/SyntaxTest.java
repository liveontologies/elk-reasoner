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

import org.semanticweb.elk.syntax.implementation.ElkClassImpl;
import org.semanticweb.elk.syntax.implementation.ElkObjectIntersectionOfImpl;
import org.semanticweb.elk.syntax.implementation.ElkObjectPropertyImpl;
import org.semanticweb.elk.syntax.implementation.ElkObjectSomeValuesFromImpl;
import org.semanticweb.elk.syntax.implementation.ElkSubClassOfAxiomImpl;
import org.semanticweb.elk.syntax.interfaces.ElkClassAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkClassExpression;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;

import junit.framework.TestCase;

public class SyntaxTest extends TestCase {
	public SyntaxTest( String testName ) {
		super( testName );
	}
	
	public void testFactory() {
		ElkClassExpression heart = ElkClassImpl.create("Heart");
		ElkClassExpression organ = ElkClassImpl.create("Organ");
		ElkClassExpression heart2 = ElkClassImpl.create("Heart");

		assertSame("heart == heart2", heart, heart2);
		assertNotSame("heart != organ", organ);

		ElkClassExpression human = ElkClassImpl.create("Human");
		ElkObjectPropertyExpression has = ElkObjectPropertyImpl.create("has");

		ElkClassExpression hasHeart = ElkObjectSomeValuesFromImpl.create(has, heart);		   
		ElkClassExpression hasOrgan = ElkObjectSomeValuesFromImpl.create(has, organ);
		ElkClassExpression hasHeart2 = ElkObjectSomeValuesFromImpl.create(has, heart2);

		assertSame("hasHeart == hasHeart2", hasHeart, hasHeart2);
		assertNotSame("hasHeart != hasOrgan", hasHeart, hasOrgan);

		ElkClassExpression heartAndOrgan = ElkObjectIntersectionOfImpl.create(Arrays.asList(heart, organ));
		ElkClassExpression organAndHeart = ElkObjectIntersectionOfImpl.create(Arrays.asList(organ, heart));
		ElkClassExpression heart2AndOrgan = ElkObjectIntersectionOfImpl.create(Arrays.asList(heart2, organ));

		assertSame("heartAndOrgan == heart2AndOrgan", heartAndOrgan, heart2AndOrgan);
		assertNotSame("heartAndOrgan == organAndHeart", heartAndOrgan, organAndHeart);

		ElkClassAxiom humanHasHeart = ElkSubClassOfAxiomImpl.create(human, hasHeart);
		ElkClassAxiom humanHasOrgan = ElkSubClassOfAxiomImpl.create(human, hasOrgan);
		ElkClassAxiom humanHasHeart2 = ElkSubClassOfAxiomImpl.create(human, hasHeart2);

		assertSame("humanHasHeart == humanHasHeart2", humanHasHeart, humanHasHeart2);
		assertNotSame("humanHasHeart != humanHasOrgan", humanHasHeart, humanHasOrgan);
	}
}
