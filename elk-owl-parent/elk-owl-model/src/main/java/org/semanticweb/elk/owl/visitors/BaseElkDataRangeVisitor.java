/**
 * 
 */
package org.semanticweb.elk.owl.visitors;
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

import org.semanticweb.elk.owl.interfaces.ElkDataComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataOneOf;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDataUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class BaseElkDataRangeVisitor<O> implements ElkDataRangeVisitor<O> {

	protected abstract O defaultVisit(ElkDataRange dataRange);
	
	@Override
	public O visit(ElkDataComplementOf elkDataComplementOf) {
		return defaultVisit(elkDataComplementOf);
	}

	@Override
	public O visit(ElkDataIntersectionOf elkDataIntersectionOf) {
		return defaultVisit(elkDataIntersectionOf);
	}

	@Override
	public O visit(ElkDataOneOf elkDataOneOf) {
		return defaultVisit(elkDataOneOf);
	}

	@Override
	public O visit(ElkDatatype elkDatatype) {
		return defaultVisit(elkDatatype);
	}

	@Override
	public O visit(ElkDatatypeRestriction elkDatatypeRestriction) {
		return defaultVisit(elkDatatypeRestriction);
	}

	@Override
	public O visit(ElkDataUnionOf elkDataUnionOf) {
		return defaultVisit(elkDataUnionOf);
	}

}
