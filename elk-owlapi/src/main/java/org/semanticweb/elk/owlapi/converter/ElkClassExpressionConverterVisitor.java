/*
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi.converter;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owlapi.wrapper.ElkClassWrap;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

/**
 * Converting instances of {@link OWLClassExpression} to the corresponding
 * instances of {@link ElkClassExpression}
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkClassExpressionConverterVisitor implements
		OWLClassExpressionVisitorEx<ElkClassExpression> {

	private static ElkClassExpressionConverterVisitor instance_ = new ElkClassExpressionConverterVisitor();

	private ElkClassExpressionConverterVisitor() {
	}

	public static ElkClassExpressionConverterVisitor getInstance() {
		return instance_;
	}

	public ElkClass visit(OWLClass ce) {
		return new ElkClassWrap<OWLClass>(ce);
	}

	public ElkClassExpression visit(OWLObjectIntersectionOf ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLObjectUnionOf ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLObjectComplementOf ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLObjectSomeValuesFrom ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLObjectAllValuesFrom ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLObjectHasValue ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLObjectMinCardinality ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLObjectExactCardinality ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLObjectMaxCardinality ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLObjectHasSelf ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLObjectOneOf ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLDataSomeValuesFrom ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLDataAllValuesFrom ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLDataHasValue ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLDataMinCardinality ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLDataExactCardinality ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(OWLDataMaxCardinality ce) {
		// TODO Auto-generated method stub
		return null;
	}

}
