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
/**
 * @author Yevgeny Kazakov, Apr 8, 2011
 */
package org.semanticweb.elk.syntax;

import org.semanticweb.elk.syntax.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkTransitiveObjectPropertyAxiom;

/**
 * Visitor pattern interface for instances of {@link ElkObjectPropertyAxiom}.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch 
 */
public interface ElkObjectPropertyAxiomVisitor<O> {

	O visit(ElkAsymmetricObjectPropertyAxiom elkAsymmetricObjectPropertyAxiom);

	O visit(ElkDisjointObjectPropertiesAxiom elkDisjointObjectPropertiesAxiom);

	O visit(ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties);

	O visit(ElkFunctionalObjectPropertyAxiom elkFunctionalObjectPropertyAxiom);

	O visit(ElkInverseFunctionalObjectPropertyAxiom elkInverseFunctionalObjectPropertyAxiom);

	O visit(ElkInverseObjectPropertiesAxiom elkInverseObjectPropertiesAxiom);

	O visit(ElkIrreflexiveObjectPropertyAxiom elkIrreflexiveObjectPropertyAxiom);

	O visit(ElkObjectPropertyDomainAxiom elkObjectPropertyDomainAxiom);

	O visit(ElkObjectPropertyRangeAxiom elkObjectPropertyRangeAxiom);

	O visit(ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom);

	O visit(ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom);
	
	O visit(ElkSymmetricObjectPropertyAxiom elkSymmetricObjectPropertyAxiom);

	O visit(ElkTransitiveObjectPropertyAxiom elkTransitiveObjectPropertyAxiom);

}
