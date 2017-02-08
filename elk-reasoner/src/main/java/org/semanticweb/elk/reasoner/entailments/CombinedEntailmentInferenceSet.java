package org.semanticweb.elk.reasoner.entailments;

/*-
 * #%L
 * ELK Reasoner Core
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

import java.util.Arrays;

import org.liveontologies.proof.util.CombinedInferenceSet;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInferenceSet;

public class CombinedEntailmentInferenceSet
		extends CombinedInferenceSet<Entailment, EntailmentInference>
		implements EntailmentInferenceSet {

	public CombinedEntailmentInferenceSet(
			final Iterable<? extends EntailmentInferenceSet> inferenceSets) {
		super(inferenceSets);
	}

	public CombinedEntailmentInferenceSet(
			final EntailmentInferenceSet... inferenceSets) {
		this(Arrays.asList(inferenceSets));
	}

}
