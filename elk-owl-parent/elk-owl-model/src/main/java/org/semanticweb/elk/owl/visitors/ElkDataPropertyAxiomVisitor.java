/*
 * #%L
 * ELK OWL Object Interfaces
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkDisjointDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;

/**
 * Visitor pattern interface for instances of {@link ElkDataPropertyAxiom}.
 * 
 * @author Markus Kroetzsch 
 */
public interface ElkDataPropertyAxiomVisitor<O> {

	O visit(ElkDisjointDataPropertiesAxiom elkDisjointDataPropertiesAxiom);

	O visit(ElkEquivalentDataPropertiesAxiom elkEquivalentDataProperties);

	O visit(ElkFunctionalDataPropertyAxiom elkFunctionalDataPropertyAxiom);

	O visit(ElkDataPropertyDomainAxiom elkDataPropertyDomainAxiom);

	O visit(ElkDataPropertyRangeAxiom elkDataPropertyRangeAxiom);

	O visit(ElkSubDataPropertyOfAxiom elkSubDataPropertyOfAxiom);

}
