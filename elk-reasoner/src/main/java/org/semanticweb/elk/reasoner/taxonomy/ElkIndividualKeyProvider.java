package org.semanticweb.elk.reasoner.taxonomy;

/*
 * #%L
 * ELK Reasoner
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

import java.util.Comparator;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;

public class ElkIndividualKeyProvider extends ElkEntityKeyProvider
		implements ComparatorKeyProvider<ElkEntity> {
	
	public static final ElkIndividualKeyProvider INSTANCE = new ElkIndividualKeyProvider();
	
	private static Comparator<ElkEntity> COMPARATOR = new Comparator<ElkEntity>() {
		@Override
		public int compare(ElkEntity o1, ElkEntity o2) {
			return o1.getIri().compareTo(o2.getIri());
		}
	};

	@Override
	public Comparator<ElkEntity> getComparator() {
		return COMPARATOR;
	}

}
