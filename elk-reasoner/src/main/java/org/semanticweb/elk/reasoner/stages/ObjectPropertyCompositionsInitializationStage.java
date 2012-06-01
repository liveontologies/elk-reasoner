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
package org.semanticweb.elk.reasoner.stages;

import java.util.Arrays;
import java.util.List;

import org.semanticweb.elk.reasoner.saturation.properties.ObjectPropertyCompositionsInitialization;

// TODO: Document what is going on in this stage
/**
 * @author "Yevgeny Kazakov"
 * 
 */
public class ObjectPropertyCompositionsInitializationStage extends
		AbstractReasonerStage {

	// logger for this class
	// private static final Logger LOGGER_ = Logger
	// .getLogger(ObjectPropertyCompositionsInitializationStage.class);

	ObjectPropertyCompositionsInitialization computation = null;

	public ObjectPropertyCompositionsInitializationStage(
			AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Object Property Compositions Initialization";
	}

	@Override
	public boolean done() {
		return reasoner.doneObjectPropertyCompositionsInitialization;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays
				.asList((ReasonerStage) new ObjectPropertyHierarchyComputationStage(
						reasoner));
	}

	@Override
	public void execute() {
		computation = new ObjectPropertyCompositionsInitialization(
				reasoner.ontologyIndex);
		reasoner.compositions = computation.getCompositions();
		// TODO: this computation cannot be currently terminated
//		if (isInterrupted()) {
//			LOGGER_.warn(getName()
//					+ " is interrupted! The reasoning results might be incorrect!");
//			return;
//		}
		reasoner.doneObjectPropertyCompositionsInitialization = true;
	}

	@Override
	public void printInfo() {
	}

}
