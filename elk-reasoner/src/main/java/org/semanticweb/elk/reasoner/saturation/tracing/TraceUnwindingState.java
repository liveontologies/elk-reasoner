package org.semanticweb.elk.reasoner.saturation.tracing;
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

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Pair;

/**
 * The state of the recursive trace unwinding procedure for some
 * {@link Conclusion} in some {@link Context} identified by its root.
 * 
 * This state is not thread-safe.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TraceUnwindingState {

	private final Queue<Pair<Conclusion, IndexedClassExpression>> classConclusionsToUnwind_;
	
	private final Queue<ObjectPropertyConclusion> propertyConclusionsToUnwind_;

	private final Set<ClassInference> processedClassInferences_;
	
	private final Set<ObjectPropertyInference> processedPropertyInferences_;

	public TraceUnwindingState() {
		classConclusionsToUnwind_ = new LinkedList<Pair<Conclusion, IndexedClassExpression>>();
		propertyConclusionsToUnwind_ = new LinkedList<ObjectPropertyConclusion>();
		processedClassInferences_ = new ArrayHashSet<ClassInference>();
		processedPropertyInferences_ = new ArrayHashSet<ObjectPropertyInference>();
	}

	public void addToClassUnwindingQueue(Conclusion conclusion, IndexedClassExpression rootWhereStored) {
		classConclusionsToUnwind_.add(new Pair<Conclusion, IndexedClassExpression>(conclusion, rootWhereStored));
	}
	
	public void addToPropertyUnwindingQueue(ObjectPropertyConclusion conclusion) {
		propertyConclusionsToUnwind_.add(conclusion);
	}

	public Pair<Conclusion, IndexedClassExpression> pollFromClassUnwindingQueue() {
		return classConclusionsToUnwind_.poll();
	}
	
	public ObjectPropertyConclusion pollFromPropertyUnwindingQueue() {
		return propertyConclusionsToUnwind_.poll();
	}

	public boolean addToProcessed(ClassInference inference) {
		return processedClassInferences_.add(inference);
	}
	
	public boolean addToProcessed(ObjectPropertyInference inference) {
		return processedPropertyInferences_.add(inference);
	}
}
