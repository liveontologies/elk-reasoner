/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;
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

import org.semanticweb.elk.reasoner.saturation.inferences.properties.ReflexivePropertyChainImpl;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChainImpl;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ObjectPropertyConclusionVisitor<I, O> {	
	
	public O visit(SubPropertyChainImpl<?, ?> conclusion, I input);
	
	public O visit(ReflexivePropertyChainImpl<?> conclusion, I input);
	
	public static ObjectPropertyConclusionVisitor<?, ?> DUMMY = new ObjectPropertyConclusionVisitor<Void, Void>() {

		@Override
		public Void visit(ReflexivePropertyChainImpl<?> conclusion, Void input) {
			// no-op
			return null;
		}

		@Override
		public Void visit(SubPropertyChainImpl<?, ?> conclusion, Void input) {
			// no-op
			return null;
		}

	};
}
