package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;

/**
 * A skeleton for implementation of {@link SubClassConclusion.Visitor}s using a common
 * (default) method
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public abstract class AbstractSubClassConclusionVisitor<O> implements
		SubClassConclusion.Visitor<O> {

	abstract O defaultVisit(SubClassConclusion subConclusion);

	@Override
	public O visit(BackwardLink subConclusion) {
		return defaultVisit(subConclusion);
	}

	@Override
	public O visit(Propagation subConclusion) {
		return defaultVisit(subConclusion);
	}

	@Override
	public O visit(SubContextInitialization subConclusion) {
		return defaultVisit(subConclusion);
	}

}
