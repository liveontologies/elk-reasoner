/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.util;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracingUtils {

	public static Subsumer getSubsumerWrapper(IndexedClassExpression ice) {
		return new SubsumerPremise(ice);
	}

	public static BackwardLink getBackwardLinkWrapper(IndexedPropertyChain relation, Context source) {
		return new BackwardLinkPremise(source, relation);
	}

	public static ForwardLink getForwardLinkWrapper(IndexedPropertyChain relation, 	Context target) {
		return new ForwardLinkPremise(target, relation);
	}

	public static Propagation getPropagationWrapper(IndexedPropertyChain linkRelation, IndexedObjectSomeValuesFrom carry) {
		return new PropagationPremise(linkRelation, carry);
	}
}
