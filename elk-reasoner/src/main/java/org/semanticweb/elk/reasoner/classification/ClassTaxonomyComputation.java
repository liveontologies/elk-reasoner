/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.classification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.semanticweb.elk.reasoner.indexing.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.util.AbstractConcurrentComputation;

public class ClassTaxonomyComputation
	extends AbstractConcurrentComputation<IndexedClass> {

	protected final ConcurrentClassTaxonomy classTaxonomy;
	
	
	public ClassTaxonomyComputation(ExecutorService executor, int maxWorkers) {
		super(executor, maxWorkers, 0, 512);
		this.classTaxonomy = new ConcurrentClassTaxonomy();
	}
	
	
	public ClassTaxonomy computateTaxonomy() {
		waitCompletion();
		Linker linker = new Linker();
		for (Map.Entry<ElkClass, ClassNode> e: classTaxonomy.nodeLookup.entrySet())
			linker.submit(e.getValue());
//		for (ClassNode node : classTaxonomy.getNodes())
//			linker.submit(node);
		linker.waitCompletion();
		return classTaxonomy;
	}
	
	
	protected ClassNode getNode(IndexedClass indexedClass) {
		return classTaxonomy.getNode(indexedClass.getClassExpression());
	}
	
	protected void process(IndexedClass root) {
		List<ElkClass> equivalent = new ArrayList<ElkClass>();
		List<IndexedClass> parents = new LinkedList<IndexedClass> ();

		for (IndexedClassExpression superClassExpression : root.getSaturated().getSuperClassExpressions())
			if (superClassExpression instanceof IndexedClass) {
				IndexedClass superClass = (IndexedClass) superClassExpression;
				
				if (superClass.getSaturated().getSuperClassExpressions().contains(root))
					equivalent.add(superClass.getClassExpression());
				else {
					boolean addThis = true;
					Iterator<IndexedClass> i = parents.iterator();
					while (i.hasNext()) {
						IndexedClass last = i.next();
						if (last.getSaturated().getSuperClassExpressions().contains(superClass)) {
							addThis = false;
							break;
						}
						if (superClass.getSaturated().getSuperClassExpressions().contains(last)) {
							i.remove();
						}
					}
					if (addThis) {
						parents.add(superClass);
					}
				}
			}
		
		ElkClass rootElkClass = root.getClassExpression(); 
		for (ElkClass ec : equivalent)
			if (ec.hashCode() < rootElkClass.hashCode())
				return;
		
		ClassNode node = new ClassNode(equivalent);
		node.parentIndexClasses = parents;
		
		for (ElkClass ec : equivalent)
			classTaxonomy.nodeLookup.put(ec, node);
	}
	

	protected class Linker extends AbstractConcurrentComputation<ClassNode> {

		public Linker() {
			super(ClassTaxonomyComputation.this.executor, ClassTaxonomyComputation.this.maxWorkers, 0, 512);
		}

		@Override
		protected void process(ClassNode node) {
			List<IndexedClass> parents;
			synchronized (node) {
				if (node.parentIndexClasses == null)
					return;
				parents = node.parentIndexClasses;
				node.parentIndexClasses = null;
			}
			
			for (IndexedClass ic : parents) {
				ClassNode parent = getNode(ic);
				node.addParent(parent);
				parent.addChild(node);
			}
		}
	}
}