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

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.saturation.properties.ObjectPropertySaturation;

/**
 * The reasoner stage, which purpose is to compute the saturation for object
 * properties of the given ontology
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class ObjectPropertySaturationStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ObjectPropertySaturationStage.class);

	ObjectPropertySaturation computation = null;

	public ObjectPropertySaturationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Object Property Saturation";
	}

	@Override
	public boolean done() {
		return reasoner.doneObjectPropertySaturation;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void execute() {
		int workerNo = reasoner.getNumberOfWorkers();
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + workerNo + " workers");
		computation = new ObjectPropertySaturation(reasoner.getStageExecutor(),
				reasoner.getExecutor(), workerNo, reasoner.ontologyIndex);
		computation.compute();
		if (isInterrupted()) {
			LOGGER_.warn(getName()
					+ " is interrupted! The reasoning results might be incorrect!");
			return;
		}
		reasoner.doneObjectPropertySaturation = true;
	}

	@Override
	public void printInfo() {
	}

}
