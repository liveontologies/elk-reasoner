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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Performs decomposition of all sorts of indexed class expressions
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
abstract class BasicDecompositionRuleApplicationVisitor implements
		DecompositionRuleApplicationVisitor {

	protected static final Logger LOGGER_ = Logger
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
	public void visit(IndexedDatatypeExpression ice, Context context) {
		ice.applyRule(context, ice, getSaturationStateWriter());
	}

	@Override
	public void visit(IndexedObjectIntersectionOf ice, Context context) {
		BasicSaturationStateWriter writer = getSaturationStateWriter();
		
		writer.produce(context, new PositiveSubsumer(ice.getFirstConjunct()));
		writer.produce(context, new PositiveSubsumer(ice.getSecondConjunct()));
	}

	protected abstract BasicSaturationStateWriter getSaturationStateWriter();

}
