/*
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi;

import java.util.HashSet;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.ElkFreshEntitiesException;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.stages.ElkInterruptedException;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;

/**
 * Converts {@link ElkException}s to corresponding {@link OWLRuntimeException}s
 * whenever possible
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ElkExceptionConverter {

	private static ElkExceptionConverter INSTANCE_ = new ElkExceptionConverter();

	private ElkExceptionConverter() {
	}

	public static ElkExceptionConverter getInstance() {
		return INSTANCE_;
	}

	@SuppressWarnings("static-method")
	public FreshEntitiesException convert(ElkFreshEntitiesException e) {
		HashSet<OWLEntity> owlEntities = new HashSet<OWLEntity>();
		for (ElkEntity elkEntity : e.getEntities()) {
			owlEntities.add(elkEntity.accept(ElkEntityConverter.getInstance()));
		}
		return new FreshEntitiesException(owlEntities);
	}

	@SuppressWarnings("static-method")
	public InconsistentOntologyException convert(
			ElkInconsistentOntologyException e) {
		return new InconsistentOntologyException();
	}

	@SuppressWarnings("static-method")
	public ReasonerInterruptedException convert(ElkInterruptedException e) {
		return new ReasonerInterruptedException((ElkInterruptedException) e);
	}

	// TODO: perhaps convert using some visitor
	public OWLRuntimeException convert(ElkException e) {
		if (e instanceof ElkFreshEntitiesException)
			return convert((ElkFreshEntitiesException) e);
		else if (e instanceof ElkInconsistentOntologyException)
			return convert((ElkInconsistentOntologyException) e);
		else if (e instanceof ElkInterruptedException)
			return convert((ElkInterruptedException) e);
		else
			return new ReasonerInterruptedException(e);
	}

}
