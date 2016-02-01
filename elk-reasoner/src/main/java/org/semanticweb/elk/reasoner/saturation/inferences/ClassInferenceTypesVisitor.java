package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

public interface ClassInferenceTypesVisitor<O> {

	public O visit(ContextInitializationInference inference);

	public O visit(ContradictionInference inference);

	public O visit(DisjointSubsumerInference inference);

	public O visit(SubContextInitializationInference inference);

	public O visit(ForwardLinkInference inference);

	public O visit(BackwardLinkInference inference);

	public O visit(PropagationInference inference);

	public O visit(SubClassInclusionComposedInference inference);

	public O visit(SubClassInclusionDecomposedInference inference);

}
