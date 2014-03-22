package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

/*
 * #%L
 * ALC Reasoner
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

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.saturation.Root;

/**
 * 
 * A {@link LocalConclusion} representing a conjecture that a given
 * {@link IndexedClassExpression} is not derived as a subsumer of the members of
 * the {@link Root} for which it is produced. If a {@link Clash} is derived
 * based on this conjecture, this means that the {@link IndexedClassExpression}
 * is a deterministic subsumer.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface ConjectureNonSubsumer extends Subsumer,
		LocalPossibleConclusion {

	public static final String NAME = "Conjecture Non-Subsumer";

}
