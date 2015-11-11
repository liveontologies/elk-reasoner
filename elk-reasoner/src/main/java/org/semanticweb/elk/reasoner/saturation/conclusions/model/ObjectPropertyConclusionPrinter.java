package org.semanticweb.elk.reasoner.saturation.conclusions.model;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

public class ObjectPropertyConclusionPrinter implements
		ObjectPropertyConclusion.Visitor<Void, String> {

	private static ObjectPropertyConclusionPrinter INSTANCE_ = new ObjectPropertyConclusionPrinter();

	private ObjectPropertyConclusionPrinter() {

	}

	public static String toString(ObjectPropertyConclusion conclusion) {
		return conclusion.accept(INSTANCE_, null);
	}

	@Override
	public String visit(SubPropertyChain conclusion, Void input) {
		return "SubPropertyChain(" + conclusion.getSubChain() + " "
				+ conclusion.getSuperChain() + ")";
	}

}
