package org.semanticweb.elk.alc.saturation.conclusions.interfaces;

import org.semanticweb.elk.alc.saturation.Root;
import org.semanticweb.elk.alc.saturation.conclusions.visitors.ConclusionVisitor;

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

/**
 * 
 * A general type of conclusions derived by rules
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface Conclusion {

	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input);

	/**
	 * Returns the source {@link Root} as a consequence of which this
	 * {@link Conclusion} given the {@link Root} for which this
	 * {@link Conclusion} was produced (except for a few cases, it will be
	 * always the same {@link Root}).
	 * 
	 * @param forWhicProduced
	 * @return
	 */
	public Root getSourceRoot(Root forWhicProduced);

}
