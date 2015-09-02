package org.semanticweb.elk.reasoner.saturation.inferences.visitors;

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

import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedDefinition;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedFirstConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedSecondConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ObjectHasSelfPropertyRangeSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassOfSubsumer;

public interface DecomposedSubsumerInferenceVisitor<I, O> {

	public O visit(DecomposedDefinition inference, I input);

	public O visit(DecomposedFirstConjunct inference, I input);

	public O visit(DecomposedSecondConjunct inference, I input);

	public O visit(InitializationSubsumer inference, I input);

	public O visit(ObjectHasSelfPropertyRangeSubsumer inference, I input);

	public O visit(SubClassOfSubsumer inference, I input);

}
