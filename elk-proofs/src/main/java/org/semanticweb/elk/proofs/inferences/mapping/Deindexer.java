/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.mapping;
/*
 * #%L
 * ELK Proofs Package
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedRangeFiller;

/**
 * Converts indexed objects to ELK OWL objects i.e. the inverse of indexing.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class Deindexer implements IndexedContextRoot.Visitor<ElkClassExpression> {

	private final static ElkObjectFactory factory_ = new ElkObjectFactoryImpl();

	public static ElkClassExpression deindex(IndexedContextRoot root) {
		return root.accept(new Deindexer());
	}

	public static ElkSubObjectPropertyExpression deindex(
			IndexedPropertyChain ipc) {
		return ipc
				.accept(new IndexedPropertyChain.Visitor<ElkSubObjectPropertyExpression>() {

					@Override
					public ElkSubObjectPropertyExpression visit(
							IndexedObjectProperty element) {
						return deindex(element);
					}

					@Override
					public ElkSubObjectPropertyExpression visit(
							IndexedComplexPropertyChain element) {
						return deindex(element);
					}
				});
	}

	public static ElkObjectProperty deindex(IndexedObjectProperty ip) {
		return ip.getElkEntity();
	}

	public static ElkObjectPropertyChain deindex(IndexedComplexPropertyChain ipc) {
		final List<ElkObjectPropertyExpression> properties = new LinkedList<ElkObjectPropertyExpression>();

		while (ipc != null) {
			properties.add(deindex(ipc.getFirstProperty()));

			ipc = ipc
					.getSuffixChain()
					.accept(new IndexedPropertyChain.Visitor<IndexedComplexPropertyChain>() {

						@Override
						public IndexedComplexPropertyChain visit(
								IndexedObjectProperty element) {
							properties.add(element.getElkEntity());
							return null;
						}

						@Override
						public IndexedComplexPropertyChain visit(
								IndexedComplexPropertyChain element) {
							return element;
						}
					});
		}

		return factory_.getObjectPropertyChain(properties);
	}

	private List<? extends ElkClassExpression> deindex(
			Set<? extends IndexedClassExpression> expressions) {
		List<ElkClassExpression> deindexed = new ArrayList<ElkClassExpression>(
				expressions.size());

		for (IndexedClassExpression ice : expressions) {
			deindexed.add(deindex(ice));
		}

		return deindexed;
	}

	@Override
	public ElkClass visit(IndexedClass element) {
		return element.getElkEntity();
	}

	@Override
	public ElkClassExpression visit(IndexedIndividual element) {
		return factory_.getObjectOneOf(element.getElkEntity());
	}

	@Override
	public ElkClassExpression visit(IndexedObjectComplementOf element) {
		return factory_
				.getObjectComplementOf(element.getNegated().accept(this));
	}

	@Override
	public ElkClassExpression visit(IndexedObjectIntersectionOf element) {
		return factory_.getObjectIntersectionOf(element.getFirstConjunct()
				.accept(this), element.getSecondConjunct().accept(this));
	}

	@Override
	public ElkClassExpression visit(IndexedObjectSomeValuesFrom element) {
		return factory_.getObjectSomeValuesFrom(deindex(element.getProperty()),
				element.getFiller().accept(this));
	}

	@Override
	public ElkClassExpression visit(IndexedObjectUnionOf element) {
		return factory_.getObjectUnionOf(deindex(element.getDisjuncts()));
	}

	@Override
	public ElkClassExpression visit(IndexedDataHasValue element) {
		return factory_.getDataHasValue(element.getRelation(),
				element.getFiller());
	}

	@Override
	public ElkClassExpression visit(IndexedRangeFiller element) {
		// TODO: take property ranges into account
		return element.getFiller().accept(this);		
	}

}
