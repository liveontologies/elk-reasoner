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
package org.semanticweb.elk.reasoner.saturation.classes;

import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Queueable;
import org.semanticweb.elk.util.concurrent.sync.AtomicIntegerFork;

/**
 * @author Frantisek Simancik
 * 
 */
public abstract class SuperClassExpression<C extends ContextElClassSaturation>
		implements Queueable<C> {

	public static AtomicInteger superClassExpressionNo = new AtomicInteger(0);
	public static AtomicInteger superClassExpressionInfNo = new AtomicInteger(0);

	private static ThreadLocal<AtomicIntegerFork> localSuperClassExpressionNo = new ThreadLocal<AtomicIntegerFork>() {
		@Override
		protected synchronized AtomicIntegerFork initialValue() {
			return new AtomicIntegerFork(superClassExpressionNo);
		}
	};

	private static ThreadLocal<AtomicIntegerFork> localSuperClassExpressionInfNo = new ThreadLocal<AtomicIntegerFork>() {
		@Override
		protected synchronized AtomicIntegerFork initialValue() {
			return new AtomicIntegerFork(superClassExpressionInfNo);
		}
	};

	public static void sync() {
		localSuperClassExpressionNo.get().sync();
		localSuperClassExpressionInfNo.get().sync();
	}

	protected final IndexedClassExpression expression;

	public SuperClassExpression(IndexedClassExpression expression) {
		this.expression = expression;
	}

	public IndexedClassExpression getExpression() {
		return expression;
	}

	@Override
	public boolean storeInContext(C context) {
//		localSuperClassExpressionInfNo.get().incrementAndGet();
		if (context.superClassExpressions.add(expression)) {
//			localSuperClassExpressionNo.get().incrementAndGet();
			return true;
		}
		return false;
	}
}
