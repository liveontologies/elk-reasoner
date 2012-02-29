/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.saturation.rulesystem;

import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.elkrulesystem.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.elkrulesystem.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.elkrulesystem.NegativeSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.elkrulesystem.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.elkrulesystem.SuperClassExpression;
import org.semanticweb.elk.util.collections.HashSetMultimap;

class QueueableStore { //implements QueueableVisitor<Boolean> {
	
//	// Statistical information
//	static AtomicInteger superClassExpressionNo = new AtomicInteger(0);
//	static AtomicInteger superClassExpressionInfNo = new AtomicInteger(0);
//	static AtomicInteger backLinkNo = new AtomicInteger(0);
//	static AtomicInteger backLinkInfNo = new AtomicInteger(0);
//	static AtomicInteger forwLinkNo = new AtomicInteger(0);
//	static AtomicInteger forwLinkInfNo = new AtomicInteger(0);
//	
//	protected final Context context;
//
//	public QueueableStore(Context context) {
//		this.context = context;
//	}
//	
//	protected Boolean add(SuperClassExpression superClassExpression) {
//		superClassExpressionInfNo.incrementAndGet();
//		if (context.superClassExpressions.add(
//				superClassExpression.getExpression())) {
//			superClassExpressionNo.incrementAndGet();
//			return true;
//		}
//		return false;
//	}
//
//	public Boolean visit(NegativeSuperClassExpression negSuperClassExpression) {
//		return add(negSuperClassExpression);	
//	}
//
//	public Boolean visit(PositiveSuperClassExpression posSuperClassExpression) {
//		return add(posSuperClassExpression);
//	}
//
//	public Boolean visit(BackwardLink backwardLink) {
//		backLinkInfNo.incrementAndGet();
//
//		if (context.backwardLinksByObjectProperty == null)
//			context.backwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Context>();
//		
//		if (context.backwardLinksByObjectProperty.add(
//				backwardLink.getRelation(), backwardLink.getTarget())) {
//			backLinkNo.incrementAndGet();
//			return true;
//		}
//		return false;
//	}
//
//	public Boolean visit(ForwardLink forwardLink) {
//		forwLinkInfNo.incrementAndGet();
//
//		if (context.forwardLinksByObjectProperty == null)
//			context.forwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Context>();
//
//		if (context.forwardLinksByObjectProperty.add(
//				forwardLink.getRelation(), forwardLink.getTarget())) {
//			forwLinkNo.incrementAndGet();
//			return true;
//		}
//		return false;
//	}

}
