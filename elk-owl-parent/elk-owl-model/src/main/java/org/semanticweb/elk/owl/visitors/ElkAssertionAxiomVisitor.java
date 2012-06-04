/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkClassAxiomVisitor.java 282 2011-08-08 17:06:32Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkClassAxiomVisitor.java $
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
package org.semanticweb.elk.owl.visitors;

import org.semanticweb.elk.owl.interfaces.ElkAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;

/**
 * Visitor pattern interface for instances of {@link ElkAssertionAxiom}.
 *
 * @author Markus Kroetzsch 
 */
public interface ElkAssertionAxiomVisitor<O> {
	
	O visit(ElkClassAssertionAxiom elkClassAssertionAxiom);
	
	O visit(ElkDataPropertyAssertionAxiom elkDataPropertyAssertionAxiom);
	
	O visit(ElkDifferentIndividualsAxiom elkDifferentIndividualsAxiom);
	
	O visit(ElkNegativeDataPropertyAssertionAxiom elkNegativeDataPropertyAssertion);
	
	O visit(ElkNegativeObjectPropertyAssertionAxiom elkNegativeObjectPropertyAssertion);
	
	O visit(ElkObjectPropertyAssertionAxiom elkObjectPropertyAssertionAxiom);
	
	O visit(ElkSameIndividualAxiom elkSameIndividualAxiom);

}
