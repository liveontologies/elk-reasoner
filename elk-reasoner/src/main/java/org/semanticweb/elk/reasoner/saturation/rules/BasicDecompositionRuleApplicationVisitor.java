package org.semanticweb.elk.reasoner.saturation.rules;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.Collection;

import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs decomposition of all sorts of indexed class expressions
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
abstract class BasicDecompositionRuleApplicationVisitor implements
		DecompositionRuleApplicationVisitor {

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(BasicDecompositionRuleApplicationVisitor.class);

	@Override
	public void visit(IndexedClass ice, Context context) {
		BasicSaturationStateWriter writer = getSaturationStateWriter();

		if (ice == getSaturationStateWriter().getOwlNothing()) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Producing contradiction for "
						+ context.getRoot());
			}
			writer.produce(context, Contradiction.getInstance());
		}
	}

	@Override
	public void visit(IndexedDatatypeExpression ide, Context context) {
		BasicSaturationStateWriter writer = getSaturationStateWriter();
		IndexedDataProperty idp = ide.getProperty();
		ValueSpace<?> vs = ide.getValueSpace();

		if (vs == EmptyValueSpace.INSTANCE) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Producing contradiction due to the empty value space for "
						+ context.getRoot());
			}
			// this means that value space is inconsistent; in this
			// case we are done
			writer.produce(context, Contradiction.getInstance());
		} else {
			// this is where we iterate over subsuming indexed datatype
			// expressions and produce subsumers for this context
			for (IndexedDataProperty superProperty : idp.getSaturated()
					.getSuperProperties()) {
				Collection<IndexedDatatypeExpression> subsumers = superProperty
						.getSubsumersFor(ide);
				if (subsumers != null) {
					for (IndexedDatatypeExpression expr : subsumers) {
						writer.produce(context, new NegativeSubsumer(expr));
					}
				}
			}
		}
	}

	@Override
	public void visit(IndexedObjectComplementOf ice, Context context) {
		BasicSaturationStateWriter writer = getSaturationStateWriter();
		if (context.getSubsumers().contains(ice.getNegated()))
			writer.produce(context, Contradiction.getInstance());
	}

	@Override
	public void visit(IndexedObjectIntersectionOf ice, Context context) {
		BasicSaturationStateWriter writer = getSaturationStateWriter();

		writer.produce(context, new PositiveSubsumer(ice.getFirstConjunct()));
		writer.produce(context, new PositiveSubsumer(ice.getSecondConjunct()));
	}

	protected abstract BasicSaturationStateWriter getSaturationStateWriter();

}
