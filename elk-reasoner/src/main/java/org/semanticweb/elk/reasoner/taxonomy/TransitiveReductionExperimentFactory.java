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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.saturation.classes.ContextClassSaturation;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * The factory for engines that concurrently computes equivalent classes of
 * named classes and creates taxonomy nodes for them. The jobs are submitted
 * using the method {@link NaiveEngine#submit(IndexedClass)}, which require the
 * computation of the {@link Node} for the input {@link IndexedClass}.
 * 
 * @author Frantisek Simancik
 */
public class TransitiveReductionExperimentFactory implements
		InputProcessorFactory<IndexedClass, InputProcessor<IndexedClass>> {
	
	private final static boolean NAIVE = false;
	
	int equivalentNo = 0;
	int directNo = 0;
	

	public TransitiveReductionExperimentFactory() {
	}

	/**
	 * Print statistics about taxonomy construction
	 */
	public void printStatistics() {
		System.out.println("Equivalent sum: " + equivalentNo);
		System.out.println("Direct sum: " + directNo);
	}

	public class NaiveEngine implements InputProcessor<IndexedClass> {

		// don't allow creating of engines directly; only through the factory
		private NaiveEngine() {
		}

		@Override
		public final void submit(IndexedClass root) {
			ContextClassSaturation rootContext = (ContextClassSaturation) root
					.getContext();
			if (!rootContext.isSatisfiable()) {
				return;
			}

			Collection<ElkClass> equivalent = new ArrayList<ElkClass>(1);
			Collection<ContextClassSaturation> directSups = new LinkedList<ContextClassSaturation>();

			for (IndexedClass sup : Operations.filter(
					rootContext.getSuperClassExpressions(), IndexedClass.class)) {
				ContextClassSaturation supContext = (ContextClassSaturation) sup
						.getContext();
				if (supContext.getSuperClassExpressions().contains(root)) {
					equivalent.add(sup.getElkClass());
					continue;
				}
				
				boolean isDirect = true;
				
				for (IndexedClass b : Operations.filter(
						rootContext.getSuperClassExpressions(), IndexedClass.class)) {
					ContextClassSaturation bContext = (ContextClassSaturation) b.getContext();
					if (b != sup && !bContext.getSuperClassExpressions().contains(root) && bContext.getSuperClassExpressions().contains(sup)) {
						isDirect = false;
						break;
					}
				}
				
				if (isDirect)
					directSups.add(supContext);
			}
			
			equivalentNo += equivalent.size();
			directNo += directSups.size();
		}

		@Override
		public void process() throws InterruptedException {
			// nothing to do here, everything should be processed during the
			// submission
		}

		@Override
		public void finish() {
		}

	}
	
	public class BetterEngine implements InputProcessor<IndexedClass> {

		// don't allow creating of engines directly; only through the factory
		private BetterEngine() {
		}

		@Override
		public final void submit(IndexedClass root) {
			ContextClassSaturation rootContext = (ContextClassSaturation) root
					.getContext();
			if (!rootContext.isSatisfiable()) {
				return;
			}

			Collection<ElkClass> equivalent = new ArrayList<ElkClass>(1);
			Collection<ContextClassSaturation> directSups = new LinkedList<ContextClassSaturation>();

			for (IndexedClass sup : Operations.filter(
					rootContext.getSuperClassExpressions(), IndexedClass.class)) {
				ContextClassSaturation supContext = (ContextClassSaturation) sup
						.getContext();
				if (supContext.getSuperClassExpressions().contains(root)) {
					equivalent.add(sup.getElkClass());
					continue;
				}
				
				boolean isDirect = true;

				Iterator<ContextClassSaturation> i = directSups.iterator();
				while (i.hasNext()) {
					ContextClassSaturation dir = i.next();
					if (dir.getSuperClassExpressions().contains(sup)) {
						isDirect = false;
						break;
					}
					if (supContext.getSuperClassExpressions().contains(dir.getRoot()))
						i.remove();
				}
				
				if (isDirect)
					directSups.add(supContext);
			}
			
			equivalentNo += equivalent.size();
			directNo += directSups.size();
		}

		@Override
		public void process() throws InterruptedException {
			// nothing to do here, everything should be processed during the
			// submission
		}

		@Override
		public void finish() {
		}

	}

	@Override
	public InputProcessor<IndexedClass> getEngine() {
		if (NAIVE)
			return new NaiveEngine();
		else
			return new BetterEngine();

	}

}
