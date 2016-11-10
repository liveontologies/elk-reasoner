package org.semanticweb.elk.matching.conclusions;

import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectHasSelfMatch1;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectSomeValuesFromMatch1;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyOfObjectComplementOfMatch1;
import org.semanticweb.elk.matching.inferences.ForwardLinkOfObjectHasSelfMatch1;
import org.semanticweb.elk.matching.inferences.ForwardLinkOfObjectSomeValuesFromMatch1;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedEntityMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionDecomposedFirstConjunctMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionDecomposedSecondConjunctMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedDefinitionMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionObjectHasSelfPropertyRangeMatch1;

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

public interface SubClassInclusionDecomposedMatch1Watch extends InferenceMatch {

	<O> O accept(Visitor<O> visitor);

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> extends BackwardLinkOfObjectHasSelfMatch1.Visitor<O>,
			BackwardLinkOfObjectSomeValuesFromMatch1.Visitor<O>,
			ClassInconsistencyOfObjectComplementOfMatch1.Visitor<O>,
			ForwardLinkOfObjectHasSelfMatch1.Visitor<O>,
			ForwardLinkOfObjectSomeValuesFromMatch1.Visitor<O>,
			SubClassInclusionComposedEntityMatch1.Visitor<O>,
			SubClassInclusionDecomposedFirstConjunctMatch1.Visitor<O>,
			SubClassInclusionDecomposedSecondConjunctMatch1.Visitor<O>,
			SubClassInclusionExpandedDefinitionMatch2.Visitor<O>,
			SubClassInclusionObjectHasSelfPropertyRangeMatch1.Visitor<O> {

		// combined interface

	}

}
