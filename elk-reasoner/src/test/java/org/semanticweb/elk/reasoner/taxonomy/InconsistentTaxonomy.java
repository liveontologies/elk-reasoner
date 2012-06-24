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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InconsistentTaxonomy<T extends ElkEntity> implements Taxonomy<T> {

	final Bottom bottom;

	public InconsistentTaxonomy(T top, T bot) {
		bottom = new Bottom(top, bot);
	}

	@Override
	public TaxonomyNode<T> getNode(T elkObject) {
		return bottom.members.contains(elkObject) ? bottom : null;
	}

	@Override
	public Set<? extends TaxonomyNode<T>> getNodes() {
		return Collections.singleton(bottom);
	}

	@Override
	public TaxonomyNode<T> getTopNode() {
		return bottom;
	}

	@Override
	public TaxonomyNode<T> getBottomNode() {
		return bottom;
	}

	class Bottom implements TaxonomyNode<T> {

		final Set<T> members;
		final T bottom;

		@SuppressWarnings("unchecked")
		Bottom(T top, T bot) {
			members = new HashSet<T>(Arrays.asList(top, bot));
			bottom = bot;
		}

		@Override
		public Set<T> getMembers() {
			return members;
		}

		@Override
		public T getCanonicalMember() {
			return bottom;
		}

		@Override
		public Set<? extends TaxonomyNode<T>> getDirectSuperNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<? extends TaxonomyNode<T>> getAllSuperNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<? extends TaxonomyNode<T>> getDirectSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<? extends TaxonomyNode<T>> getAllSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public Taxonomy<T> getTaxonomy() {
			return InconsistentTaxonomy.this;
		}
	}
}