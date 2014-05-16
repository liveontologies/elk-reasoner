package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubContextInitialization;

/**
 * A skeleton for implementation of {@link SubConclusionVisitor}s using a common
 * (default) method
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of input parameter with which this visitor works
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public abstract class AbstractSubConclusionVisitor<I, O> implements
		SubConclusionVisitor<I, O> {

	abstract O defaultVisit(SubConclusion subConclusion, I input);

	@Override
	public O visit(BackwardLink subConclusion, I input) {
		return defaultVisit(subConclusion, input);
	}

	@Override
	public O visit(Propagation subConclusion, I input) {
		return defaultVisit(subConclusion, input);
	}

	@Override
	public O visit(SubContextInitialization subConclusion, I input) {
		return defaultVisit(subConclusion, input);
	}

}
