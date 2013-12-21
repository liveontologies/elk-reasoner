/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ComposedConjunction extends NegativeSubsumerImpl implements TracedConclusion {
	//TODO store this as ICE
	private final Conclusion first_;
	
	private final IndexedClassExpression second_;
	
	/**
	 * @param superClassExpression
	 */
	public ComposedConjunction(Conclusion subsumer, IndexedClassExpression conjunct, IndexedObjectIntersectionOf conjunction) {
		super(conjunction);
		first_ = subsumer;
		second_ = conjunct;
	}

	public Conclusion getFirstConjunct() {
		return first_;
	}
	
	public Conclusion getSecondConjunct() {
		return TracingUtils.getSubsumerWrapper(second_);
	}
	
	@Override
	public <R, C> R acceptTraced(TracedConclusionVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}

	@Override
	public Context getInferenceContext(Context defaultContext) {
		return defaultContext;
	}

}
