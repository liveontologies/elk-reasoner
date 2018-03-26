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
/**
 * 
 */
package org.semanticweb.elk.reasoner;

import java.io.IOException;
import java.io.Writer;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InstanceTaxonomyTestOutput extends
		TaxonomyTestOutput<InstanceTaxonomy<ElkClass, ElkNamedIndividual>> {

	public InstanceTaxonomyTestOutput(
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy,
			boolean isComplete) {
		super(taxonomy, isComplete);
	}

	public InstanceTaxonomyTestOutput(Reasoner reasoner) throws ElkException {
		this(reasoner.getInstanceTaxonomyQuietly(),
				reasoner.isClassTaxonomyComplete());
	}

	@Override
	public int hashCode() {
		return InstanceTaxonomyHasher.hash(getTaxonomy());
	}

	@Override
	public boolean equals(final Object obj) {
		return (obj instanceof InstanceTaxonomyTestOutput && super.equals(obj));
	}

	@Override
	protected void dumpTaxonomy(final Writer writer) throws IOException {
		TaxonomyPrinter.dumpInstanceTaxomomy(getTaxonomy(), writer, false);
	}

}
