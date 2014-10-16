/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;
/*
 * #%L
 * ELK OWL API Binding
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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class ClassExpressionConverter implements ElkClassExpressionVisitor<OWLClassExpression> {

	private final OWLDataFactory factory_;
	
	ClassExpressionConverter(OWLDataFactory f) {
		factory_ = f;
	}
	
	@Override
	public OWLClassExpression visit(ElkClass elkClass) {
		return factory_.getOWLClass(IRI.create(elkClass.getIri().getFullIriAsString()));
	}

	@Override
	public OWLClassExpression visit(ElkDataAllValuesFrom ce) {
		if (ce.getDataPropertyExpressions().size() > 1) {
			throw new IllegalArgumentException("N-ary data ranges cannot be represented in the OWL API");
		}

		DataPropertyExpressionConverter dpConverter = new DataPropertyExpressionConverter(factory_);
		DataRangeAndLiteralConverter drConverter = new DataRangeAndLiteralConverter(factory_);
		
		return factory_.getOWLDataAllValuesFrom(ce.getDataPropertyExpressions().get(0).accept(dpConverter), ce.getDataRange().accept(drConverter));
	}

	@Override
	public OWLClassExpression visit(ElkDataExactCardinality dr) {
		DataPropertyExpressionConverter dpConverter = new DataPropertyExpressionConverter(factory_);
		
		return factory_.getOWLDataExactCardinality(dr.getCardinality(), dr.getProperty().accept(dpConverter));
	}

	@Override
	public OWLClassExpression visit(ElkDataExactCardinalityQualified ce) {
		DataPropertyExpressionConverter dpConverter = new DataPropertyExpressionConverter(factory_);
		DataRangeAndLiteralConverter drConverter = new DataRangeAndLiteralConverter(factory_);
		
		return factory_.getOWLDataExactCardinality(ce.getCardinality(), ce.getProperty().accept(dpConverter), ce.getFiller().accept(drConverter));
	}

	@Override
	public OWLClassExpression visit(ElkDataHasValue ce) {
		return factory_.getOWLDataHasValue(ce.getProperty().accept(new DataPropertyExpressionConverter(factory_)), ce.getFiller().accept(new DataRangeAndLiteralConverter(factory_)));
	}

	@Override
	public OWLClassExpression visit(ElkDataMaxCardinality ce) {
		return factory_.getOWLDataMaxCardinality(ce.getCardinality(), ce.getProperty().accept(new DataPropertyExpressionConverter(factory_)));
	}

	@Override
	public OWLClassExpression visit(ElkDataMaxCardinalityQualified ce) {
		return factory_.getOWLDataMaxCardinality(ce.getCardinality(), ce.getProperty().accept(new DataPropertyExpressionConverter(factory_)), ce.getFiller().accept(new DataRangeAndLiteralConverter(factory_)));
	}

	@Override
	public OWLClassExpression visit(ElkDataMinCardinality ce) {
		return factory_.getOWLDataMinCardinality(ce.getCardinality(), ce.getProperty().accept(new DataPropertyExpressionConverter(factory_)));
	}

	@Override
	public OWLClassExpression visit(ElkDataMinCardinalityQualified ce) {
		return factory_.getOWLDataMinCardinality(ce.getCardinality(), ce.getProperty().accept(new DataPropertyExpressionConverter(factory_)), ce.getFiller().accept(new DataRangeAndLiteralConverter(factory_)));
	}

	@Override
	public OWLClassExpression visit(ElkDataSomeValuesFrom ce) {
		if (ce.getDataPropertyExpressions().size() > 1) {
			throw new IllegalArgumentException("N-ary data ranges cannot be represented in the OWL API");
		}

		DataPropertyExpressionConverter dpConverter = new DataPropertyExpressionConverter(factory_);
		DataRangeAndLiteralConverter drConverter = new DataRangeAndLiteralConverter(factory_);
		
		return factory_.getOWLDataSomeValuesFrom(ce.getDataPropertyExpressions().get(0).accept(dpConverter), ce.getDataRange().accept(drConverter));
	}

	@Override
	public OWLClassExpression visit(ElkObjectAllValuesFrom ce) {
		return factory_.getOWLObjectAllValuesFrom(ce.getProperty().accept(new ObjectPropertyExpressionConverter(factory_)), ce.getFiller().accept(this));
	}

	@Override
	public OWLClassExpression visit(ElkObjectComplementOf ce) {
		return factory_.getOWLObjectComplementOf(ce.getClassExpression().accept(this));
	}

	@Override
	public OWLClassExpression visit(ElkObjectExactCardinality ce) {
		return factory_.getOWLObjectExactCardinality(ce.getCardinality(), ce.getProperty().accept(new ObjectPropertyExpressionConverter(factory_)));
	}

	@Override
	public OWLClassExpression visit(ElkObjectExactCardinalityQualified ce) {
		return factory_.getOWLObjectExactCardinality(ce.getCardinality(), ce.getProperty().accept(new ObjectPropertyExpressionConverter(factory_)), ce.getFiller().accept(this));
	}

	@Override
	public OWLClassExpression visit(ElkObjectHasSelf ce) {
		return factory_.getOWLObjectHasSelf(ce.getProperty().accept(new ObjectPropertyExpressionConverter(factory_)));
	}

	@Override
	public OWLClassExpression visit(ElkObjectHasValue ce) {
		return factory_.getOWLObjectHasValue(ce.getProperty().accept(new ObjectPropertyExpressionConverter(factory_)), ce.getFiller().accept(new IndividualConverter(factory_)));
	}

	static OWLClassExpression[] convertClasses(List<? extends ElkClassExpression> ces, ClassExpressionConverter converter) {
		OWLClassExpression[] classes = new OWLClassExpression[ces.size()];
		
		for (int i = 0; i < classes.length; i++) {
			classes[i] = ces.get(i).accept(converter);
		}
		
		return classes;
	}
	
	@Override
	public OWLClassExpression visit(ElkObjectIntersectionOf ce) {
		return factory_.getOWLObjectIntersectionOf(convertClasses(ce.getClassExpressions(), this));
	}

	@Override
	public OWLClassExpression visit(ElkObjectMaxCardinality ce) {
		return factory_.getOWLObjectMaxCardinality(ce.getCardinality(), ce.getProperty().accept(new ObjectPropertyExpressionConverter(factory_)));
	}

	@Override
	public OWLClassExpression visit(ElkObjectMaxCardinalityQualified ce) {
		return factory_.getOWLObjectMaxCardinality(ce.getCardinality(), ce.getProperty().accept(new ObjectPropertyExpressionConverter(factory_)), ce.getFiller().accept(this));
	}

	@Override
	public OWLClassExpression visit(ElkObjectMinCardinality ce) {
		return factory_.getOWLObjectMinCardinality(ce.getCardinality(), ce.getProperty().accept(new ObjectPropertyExpressionConverter(factory_)));
	}

	@Override
	public OWLClassExpression visit(ElkObjectMinCardinalityQualified ce) {
		return factory_.getOWLObjectMinCardinality(ce.getCardinality(), ce.getProperty().accept(new ObjectPropertyExpressionConverter(factory_)), ce.getFiller().accept(this));
	}

	@Override
	public OWLClassExpression visit(ElkObjectOneOf ce) {
		IndividualConverter converter = new IndividualConverter(factory_);
		OWLIndividual[] inds = new OWLIndividual[ce.getIndividuals().size()];
		
		for (int i = 0; i < inds.length; i++) {
			inds[i] = ce.getIndividuals().get(i).accept(converter);
		}
		
		return factory_.getOWLObjectOneOf(inds);
	}

	@Override
	public OWLClassExpression visit(ElkObjectSomeValuesFrom ce) {
		return factory_.getOWLObjectSomeValuesFrom(ce.getProperty().accept(new ObjectPropertyExpressionConverter(factory_)), ce.getFiller().accept(this));
	}

	@Override
	public OWLClassExpression visit(ElkObjectUnionOf ce) {
		return factory_.getOWLObjectUnionOf(convertClasses(ce.getClassExpressions(), this));
	}

}
