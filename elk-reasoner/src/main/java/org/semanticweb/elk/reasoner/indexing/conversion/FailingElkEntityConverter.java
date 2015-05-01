package org.semanticweb.elk.reasoner.indexing.conversion;

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

import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;

/**
 * An {@link ElkEntityConverter} that always throws
 * {@link ElkIndexingUnsupportedException}.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class FailingElkEntityConverter implements ElkEntityConverter {

	private static <O> O fail(ElkEntity expression) {
		throw new ElkIndexingUnsupportedException(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkAnnotationProperty expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkClass expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkDataProperty expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkDatatype expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkNamedIndividual expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkObjectProperty expression) {
		return fail(expression);
	}

}
