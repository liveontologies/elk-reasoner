/*
 * #%L
 * ELK OWL API Binding
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi.parsing;

import java.io.InputStream;
import java.io.Reader;

import org.junit.Ignore;
import org.semanticweb.elk.owl.parsing.AbstractOwl2FunctionalSyntaxParseTest;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class OWLAPIFunctionalSyntaxParseTest extends
		AbstractOwl2FunctionalSyntaxParseTest {

	@Override
	protected Owl2Parser instantiateParser(InputStream stream) {
		return new OWLAPIFunctionalSyntaxParser(stream);
	}

	@Override
	protected Owl2Parser instantiateParser(Reader reader) {
		return new OWLAPIFunctionalSyntaxParser(reader);
	}

	@Override
	@Ignore("Ignored because the OWL API cannot parse such axioms")
	public void testNaryDataSomeValuesFrom() throws Owl2ParseException {
	}
	
	@Override
	@Ignore("The OWL API can't skip over FSS comments")
	public void testComments() throws Owl2ParseException {
	}	
}