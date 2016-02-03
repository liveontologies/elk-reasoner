/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

/**
 * An {@link ObjectPropertyConclusion.Visitor} that always returns {@code null}
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 */
public class DummyObjectPropertyConclusionVisitor<O>
		implements
			ObjectPropertyConclusion.Visitor<O> {

	/**
	 * The default implementation of all methods
	 * 
	 * @param conclusion
	 * @return
	 */
	protected O defaultVisit(ObjectPropertyConclusion conclusion) {
		return null;
	}

	@Override
	public O visit(PropertyRange conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(SubPropertyChain conclusion) {
		return defaultVisit(conclusion);
	}

}
