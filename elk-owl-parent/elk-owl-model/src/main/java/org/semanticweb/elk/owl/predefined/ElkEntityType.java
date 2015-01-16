package org.semanticweb.elk.owl.predefined;

/*
 * #%L
 * ELK OWL Object Interfaces
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

/**
 * Corresponds to the types <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Entity_Declarations_and_Typing" >Entity
 * Declaration Axioms<a> in the OWL 2 specification.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public enum ElkEntityType {

	CLASS("Class"), DATATYPE("Datatype"), OBJECT_PROPERTY("ObjectProperty"), DATA_PROPERTY(
			"DataProperty"), ANNOTATION_PROPERTY("AnnotationProperty"), NAMED_INDIVIDUAL(
			"NamedIndividual");

	private String name_;

	ElkEntityType(String name) {
		this.name_ = name;
	}

	@Override
	public String toString() {
		return this.name_;
	}

}
