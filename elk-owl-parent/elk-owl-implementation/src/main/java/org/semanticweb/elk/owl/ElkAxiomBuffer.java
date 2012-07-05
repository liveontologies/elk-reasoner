package org.semanticweb.elk.owl;

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

import java.util.ArrayList;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

/**
 * Simple {@link ElkAxiomProcessor} that merely buffers axioms to send them to
 * another processor later on. This is useful for performance measurements, in
 * particular for decoupling parsing time from indexing time. It can also be
 * used for re-using the same set of axioms in more than one place without
 * having to re-parse them again.
 * 
 * @author Markus Kroetzsch
 */
public class ElkAxiomBuffer implements ElkAxiomProcessor {

	final protected ArrayList<ElkAxiom> axioms = new ArrayList<ElkAxiom>();

	@Override
	public void visit(ElkAxiom elkAxiom) {
		axioms.add(elkAxiom);
	}

	/**
	 * Send all axioms that have hitherto been processed by this object to the
	 * given axiom processor.
	 * 
	 * @param elkAxiomProcessor
	 */
	public void sendAxiomsToProcessor(ElkAxiomProcessor elkAxiomProcessor) {
		for (ElkAxiom elkAxiom : axioms) {
			elkAxiomProcessor.visit(elkAxiom);
		}
	}

}
