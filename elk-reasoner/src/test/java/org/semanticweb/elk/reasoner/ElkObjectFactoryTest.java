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
package org.semanticweb.elk.reasoner;

import junit.framework.TestCase;

public class ElkObjectFactoryTest extends TestCase {
	   public ElkObjectFactoryTest( String testName ) {
		   super( testName );
	   }
	   
	   public void testFactory() {
		   ElkClassExpression heart = ElkClass.create("Heart");
		   ElkClassExpression organ = ElkClass.create("Organ");
		   ElkClassExpression heart2 = ElkClass.create("Heart");
		   
		   assertSame("heart == heart2", heart, heart2);
		   assertNotSame("heart != organ", organ);
		   
		   ElkClassExpression human = ElkClass.create("Human");
		   ElkObjectPropertyExpression has = ElkObjectProperty.create("has");
		   
		   ElkClassExpression hasHeart = ElkObjectSomeValuesFrom.create(has, heart);		   
		   ElkClassExpression hasOrgan = ElkObjectSomeValuesFrom.create(has, organ);
		   ElkClassExpression hasHeart2 = ElkObjectSomeValuesFrom.create(has, heart2);
		   
		   assertSame("hasHeart == hasHeart2", hasHeart, hasHeart2);
		   assertNotSame("hasHeart != hasOrgan", hasHeart, hasOrgan);
		   
		   ElkClassExpression heartAndOrgan = ElkObjectIntersectionOf.create(heart, organ);
		   ElkClassExpression organAndHeart = ElkObjectIntersectionOf.create(organ, heart);
		   ElkClassExpression heart2AndOrgan = ElkObjectIntersectionOf.create(heart2, organ);
		   
		   assertSame("heartAndOrgan == heart2AndOrgan", heartAndOrgan, heart2AndOrgan);
		   assertNotSame("heartAndOrgan == organAndHeart", heartAndOrgan, organAndHeart);
		   
		   ElkClassAxiom humanHasHeart = ElkSubClassOfAxiom.create(human, hasHeart);
		   ElkClassAxiom humanHasOrgan = ElkSubClassOfAxiom.create(human, hasOrgan);
		   ElkClassAxiom humanHasHeart2 = ElkSubClassOfAxiom.create(human, hasHeart2);
		   
		   assertSame("humanHasHeart == humanHasHeart2", humanHasHeart, humanHasHeart2);
		   assertNotSame("humanHasHeart != humanHasOrgan", humanHasHeart, humanHasOrgan);
	   }
}
