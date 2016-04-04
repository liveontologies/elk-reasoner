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
package org.semanticweb.elk.owl.parsing;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;

import junit.framework.TestCase;

public class SyntaxTest extends TestCase {
	final ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();

	public SyntaxTest(String testName) {
		super(testName);
	}

	public void testFactory() {
		ElkClassExpression heart = objectFactory.getClass(new ElkFullIri("Heart"));
		ElkClassExpression organ = objectFactory.getClass(new ElkFullIri("Organ"));
		ElkClassExpression heart2 = objectFactory.getClass(new ElkFullIri("Heart"));

		assertSame("heart == heart2", heart, heart2);
		assertNotSame("heart != organ", organ);

	}
}
