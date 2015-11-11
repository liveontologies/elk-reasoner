package org.semanticweb.elk.reasoner.saturation;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;

/**
 * A {@link Context} with additional methods for managing them in
 * {@link SaturationState}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ExtendedContext extends Context {

	/**
	 * Marks this {@code Context} as saturated. This means that all
	 * {@link ClassConclusion}s for this {@link Context} except for
	 * {@link SubClassConclusion}s for its {@link SubContext}s, are already computed.
	 * This method could be used from multiple threads producing consistent
	 * result (if the flag is changed concurrently by two workers, only one of
	 * them returns the previous value).
	 * 
	 * @return the previous value of the saturation state for this
	 *         {@link Context}
	 * 
	 * @see ClassConclusion#getSourceRoot(IndexedClassExpression)
	 */
	boolean setSaturated(boolean saturated);

}
