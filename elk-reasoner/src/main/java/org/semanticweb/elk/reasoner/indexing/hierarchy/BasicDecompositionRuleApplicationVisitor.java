package org.semanticweb.elk.reasoner.indexing.hierarchy;
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
import org.semanticweb.elk.reasoner.saturation.SaturationState.Writer;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;

/**
 * Performs decomposition of all sorts of indexed class expressions
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BasicDecompositionRuleApplicationVisitor implements
		DecompositionRuleApplicationVisitor {

	protected static final Logger LOGGER_ = Logger
			.getLogger(BasicDecompositionRuleApplicationVisitor.class);	
	
	@Override
	public void visit(IndexedClass ice, Writer writer, Context context) {
		if (ice == writer.getOwlNothing()) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Producing owl:Nothing for " + context.getRoot());
			}
			writer.produce(context, new Contradiction());
		}
	}

	@Override
	public void visit(IndexedDataHasValue ice, Writer writer, Context context) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(IndexedObjectIntersectionOf ice, Writer writer,
			Context context) {
		writer.produce(context, new PositiveSubsumer(ice.getFirstConjunct()));
		writer.produce(context, new PositiveSubsumer(ice.getSecondConjunct()));
	}

	@Override
	public void visit(IndexedObjectSomeValuesFrom ice, Writer writer,
			Context context) {
		writer.produce(writer.getCreateContext(ice.getFiller()), new BackwardLink(
				context, ice.getRelation()));
	}

}
