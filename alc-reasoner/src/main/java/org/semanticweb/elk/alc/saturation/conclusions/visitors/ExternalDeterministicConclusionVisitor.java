package org.semanticweb.elk.alc.saturation.conclusions.visitors;

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

import org.semanticweb.elk.alc.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PropagatedClash;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PropagatedComposedSubsumer;

public interface ExternalDeterministicConclusionVisitor<I, O> {

	public O visit(ContextInitialization conclusion, I input);

	public O visit(BackwardLink conclusion, I input);

	public O visit(PropagatedClash conclusion, I input);

	public O visit(PropagatedComposedSubsumer conclusion, I input);

}