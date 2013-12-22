/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;
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


/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BaseConclusionVisitor<R, C> implements ConclusionVisitor<R, C> {

	protected R defaultVisit(Conclusion conclusion, C cxt) {
		return null;
	}
	
	@Override
	public R visit(ComposedSubsumer negSCE, C context) {
		return defaultVisit(negSCE, context);
	}

	@Override
	public R visit(DecomposedSubsumer posSCE, C context) {
		return defaultVisit(posSCE, context);
	}

	@Override
	public R visit(BackwardLink link, C context) {
		return defaultVisit(link, context);
	}

	@Override
	public R visit(ForwardLink link, C context) {
		return defaultVisit(link, context);
	}

	@Override
	public R visit(Contradiction bot, C context) {
		return defaultVisit(bot, context);
	}

	@Override
	public R visit(Propagation propagation, C context) {
		return defaultVisit(propagation, context);
	}

	@Override
	public R visit(DisjointnessAxiom disjointnessAxiom, C context) {
		return defaultVisit(disjointnessAxiom, context);
	}

}
