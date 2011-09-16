/*
 * #%L
 * ELK OWL Data Interfaces
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

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataOneOf;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataUnionOf;

/**
 * Visitor pattern interface for instances of {@link ElkDataRange}.
 *
 * @author Markus Kroetzsch
 * 
 */
public interface ElkDataRangeVisitor<O> {

	O visit(ElkDatatype elkDatatype);

	O visit(ElkDataAllValuesFrom elkDataAllValuesFrom);
	
	O visit(ElkDataComplementOf elkDataComplementOf);

	O visit(ElkDataExactCardinality elkDataExactCardinality);

	O visit(ElkDataHasValue elkDataHasValue);

	O visit(ElkDataIntersectionOf elkDataIntersectionOf);

	O visit(ElkDataMaxCardinality elkDataMaxCardinality);

	O visit(ElkDataMinCardinality elkDataMaxCardinality);

	O visit(ElkDataOneOf elkDataOneOf);

	O visit(ElkDataSomeValuesFrom elkDataSomeValuesFrom);

	O visit(ElkDataUnionOf elkDataUnionOf);

}
