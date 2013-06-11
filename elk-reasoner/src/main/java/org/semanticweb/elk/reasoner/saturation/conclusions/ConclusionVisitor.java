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
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ConclusionVisitor<R> {

	public R visit(NegativeSubsumer negSCE, Context context);
	
	public R visit(PositiveSubsumer posSCE, Context context);
	
	public R visit(BackwardLink link, Context context);
	
	public R visit(ForwardLink link, Context context);
	
	public R visit(Contradiction bot, Context context);

	public R visit(Propagation propagation, Context context);

	public R visit(DisjointnessAxiom disjointnessAxiom, Context context);
	
	/**
	 * A dummy visitor, does nothing
	 */
	public static final ConclusionVisitor<?> DUMMY = new ConclusionVisitor<Object>() {

		@Override
		public Object visit(NegativeSubsumer negSCE, Context context) {
			return null;
		}

		@Override
		public Object visit(PositiveSubsumer posSCE, Context context) {
			return null;
		}

		@Override
		public Object visit(BackwardLink link, Context context) {
			return null;
		}

		@Override
		public Object visit(ForwardLink link, Context context) {
			return null;
		}

		@Override
		public Object visit(Contradiction bot, Context context) {
			return null;
		}

		@Override
		public Object visit(Propagation propagation, Context context) {
			return null;
		}

		@Override
		public Object visit(DisjointnessAxiom disjointnessAxiom, Context context) {
			return null;
		}
		
	};
}
