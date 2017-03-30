package org.semanticweb.elk.reasoner.saturation.properties.inferences;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SaturationInference;
import org.semanticweb.elk.reasoner.tracing.AbstractTracingInference;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.TracingInference;

/**
 * A skeleton implementation of {@link ClassInference}
 * 
 * @author Yevgeny Kazakov
 *
 */
abstract class AbstractObjectPropertyInference extends AbstractTracingInference
		implements ObjectPropertyInference {

	/**
	 * @param factory
	 *            the factory for creating conclusions
	 * 
	 * @return the conclusion produced by this inference
	 */
	abstract <F extends ObjectPropertyConclusion.Factory> Conclusion getConclusion(
			F factory);

//	@Override
//	public final Conclusion getConclusion(Conclusion.Factory factory) {
//		return getConclusion((ObjectPropertyConclusion.Factory) factory);
//	}

	@Override
	public final <O> O accept(TracingInference.Visitor<O> visitor) {
		return accept((ObjectPropertyInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(SaturationInference.Visitor<O> visitor) {
		return accept((ObjectPropertyInference.Visitor<O>) visitor);
	}

}
