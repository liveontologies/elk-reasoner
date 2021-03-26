/**
 * 
 */
package org.semanticweb.elk.owl.visitors;
/*
 * #%L
 * ELK OWL Object Interfaces
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;

/**
 * A skeleton implementation.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 * @param <O>
 *            the type of the output of this visitor
 */
public abstract class AbstractElkClassExpressionVisitor<O> implements
		ElkClassExpressionVisitor<O> {

	protected abstract O defaultVisit(ElkClassExpression ce);
	
	@Override
	public O visit(ElkClass elkClass) {
		return defaultVisit(elkClass);
	}

	@Override
	public O visit(ElkDataAllValuesFrom ce) {
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataExactCardinalityUnqualified ce) {
		return defaultVisit(ce);
	}

	@Override
	public O visit(
			ElkDataExactCardinalityQualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataHasValue ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataMaxCardinalityUnqualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataMaxCardinalityQualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataMinCardinalityUnqualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataMinCardinalityQualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataSomeValuesFrom ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectAllValuesFrom ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectComplementOf ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectExactCardinalityUnqualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(
			ElkObjectExactCardinalityQualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectHasSelf ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectHasValue ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectIntersectionOf ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectMaxCardinalityUnqualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(
			ElkObjectMaxCardinalityQualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectMinCardinalityUnqualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(
			ElkObjectMinCardinalityQualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectOneOf ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectSomeValuesFrom ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectUnionOf ce) {
		
		return defaultVisit(ce);
	}

}
