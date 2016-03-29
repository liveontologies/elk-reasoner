package org.semanticweb.elk.owl.inferences;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;

public class ElkInferencePrinter extends ElkInferenceDummyVisitor<String> {

	private static ElkInferencePrinter INSTANCE_ = new ElkInferencePrinter();

	static ElkInference.Visitor<String> getVisitor() {
		return INSTANCE_;
	}

	public static String toString(ElkInference conclusion) {
		return conclusion.accept(INSTANCE_);
	}

	ElkObjectFactory factory_ = new ElkObjectFactoryImpl();

	private ElkInferencePrinter() {

	}

	@Override
	protected String defaultVisit(ElkInference inference) {
		String result = inference.getConclusion(factory_) + " -| ";
		int premiseCount = inference.getPremiseCount();
		for (int i = 0; i < premiseCount; i++) {
			result += inference.getPremise(i, factory_);
			if (i < premiseCount - 1) {
				result += "; ";
			}
		}
		return result;
	}

}
