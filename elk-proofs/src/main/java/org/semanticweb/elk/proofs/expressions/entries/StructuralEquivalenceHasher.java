/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.entries;
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

import java.util.List;

import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.AbstractElkObjectVisitor;
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class StructuralEquivalenceHasher implements ExpressionHasher {

	static int hashCode(ElkObject obj) {
		return EntityHasher.hashCode(obj);
	}
	
	static int hashCode(ElkComplexClassExpression obj) {
		return EntityHasher.hashCode(obj);
	}
	
	static int hashCode(ElkAxiom obj) {
		return AxiomHasher.hashCode(obj);
	}
	
	static int hashCode(ElkLemma obj) {
		return LemmaHasher.hashCode(obj);
	}
	
	@Override
	public int hashCode(Expression expression) {
		return expression.accept(new ExpressionVisitor<Void, Integer>() {

			@Override
			public Integer visit(AxiomExpression<?> expr, Void input) {
				return AxiomHasher.hashCode(expr.getAxiom());
			}

			@Override
			public Integer visit(LemmaExpression<?> expr, Void input) {
				return LemmaHasher.hashCode(expr.getLemma());
			}
			
		}, null);
	}

	private static class LemmaHasher implements ElkLemmaVisitor<ElkLemma, Integer> {

		static int hashCode(ElkLemma lemma) {
			return lemma.accept(new LemmaHasher(), null);
		}
		
		@Override
		public Integer visit(ElkReflexivePropertyChainLemma lemma,
				ElkLemma input) {
			return HashGenerator.combineListHash(ElkReflexivePropertyChainLemma.class.hashCode(), EntityHasher.hashCode(lemma.getPropertyChain()));
		}

		@Override
		public Integer visit(ElkSubClassOfLemma lemma, ElkLemma input) {
			return HashGenerator.combineListHash(ElkSubClassOfLemma.class.hashCode(), EntityHasher.hashCode(lemma.getSubClass()), EntityHasher.hashCode(lemma.getSuperClass()));
		}

		@Override
		public Integer visit(ElkSubPropertyChainOfLemma lemma, ElkLemma input) {
			return HashGenerator.combineListHash(ElkSubPropertyChainOfLemma.class.hashCode(), EntityHasher.hashCode(lemma.getSubPropertyChain()), EntityHasher.hashCode(lemma.getSuperPropertyChain()));
		}
		
	}
	
	private static class AxiomHasher extends AbstractElkAxiomVisitor<Integer> {

		static int hashCode(ElkAxiom ax) {
			return AxiomCanonicalizer.canonicalize(ax).accept(new AxiomHasher());
		}
		
		@Override
		protected Integer defaultLogicalVisit(ElkAxiom axiom) {
			return axiom.hashCode();
		}

		@Override
		protected Integer defaultNonLogicalVisit(ElkAxiom axiom) {
			return axiom.hashCode();
		}

		@Override
		public Integer visit(ElkDisjointClassesAxiom elkDisjointClasses) {
			return HashGenerator.combineMultisetHash(true, hashes(ElkDisjointClassesAxiom.class.hashCode(), elkDisjointClasses.getClassExpressions()));
		}

		@Override
		public Integer visit(ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
			return HashGenerator.combineMultisetHash(true, hashes(ElkEquivalentClassesAxiom.class.hashCode(), elkEquivalentClassesAxiom.getClassExpressions()));
		}

		@Override
		public Integer visit(ElkSubClassOfAxiom ax) {
			return HashGenerator.combineListHash(ElkSubClassOfAxiom.class.hashCode(), EntityHasher.hashCode(ax.getSubClassExpression()), EntityHasher.hashCode(ax.getSuperClassExpression()));
		}

		@Override
		public Integer visit(ElkEquivalentObjectPropertiesAxiom ax) {
			return HashGenerator.combineMultisetHash(true, hashes(ElkEquivalentObjectPropertiesAxiom.class.hashCode(), ax.getObjectPropertyExpressions()));
		}

		@Override
		public Integer visit(ElkObjectPropertyDomainAxiom ax) {
			return HashGenerator.combineListHash(ElkObjectPropertyDomainAxiom.class.hashCode(), EntityHasher.hashCode(ax.getProperty()), EntityHasher.hashCode(ax.getDomain()));
		}

		@Override
		public Integer visit(ElkReflexiveObjectPropertyAxiom ax) {
			return HashGenerator.combineListHash(ElkReflexiveObjectPropertyAxiom.class.hashCode(), EntityHasher.hashCode(ax.getProperty()));
		}

		@Override
		public Integer visit(ElkSubObjectPropertyOfAxiom ax) {
			return HashGenerator.combineListHash(ElkSubObjectPropertyOfAxiom.class.hashCode(), EntityHasher.hashCode(ax.getSubObjectPropertyExpression()),EntityHasher.hashCode(ax.getSuperObjectPropertyExpression()));
		}

		@Override
		public Integer visit(ElkTransitiveObjectPropertyAxiom ax) {
			return HashGenerator.combineListHash(ElkReflexiveObjectPropertyAxiom.class.hashCode(), EntityHasher.hashCode(ax.getProperty()));
		}
		
	}
	
	private static class EntityHasher extends AbstractElkObjectVisitor<Integer> implements ElkComplexClassExpressionVisitor<ElkObject, Integer> {

		static int hashCode(ElkObject obj) {
			return obj.accept(new EntityHasher());
		}
		
		static int hashCode(ElkComplexClassExpression obj) {
			return obj.accept(new EntityHasher(), null);
		}
		
		@Override
		public Integer visit(ElkComplexObjectSomeValuesFrom ce, ElkObject input) {
			return HashGenerator.combineListHash(
					ElkComplexObjectSomeValuesFrom.class.hashCode(),
					hashCode(ce.getPropertyChain()), hashCode(ce.getFiller()));
		}

		@Override
		protected Integer defaultVisit(ElkObject obj) {
			return obj.hashCode();
		}

		@Override
		public Integer visit(ElkSameIndividualAxiom obj) {
			return HashGenerator.combineMultisetHash(true, hashes(ElkSameIndividualAxiom.class.hashCode(), obj.getIndividuals()));
		}

		@Override
		public Integer visit(ElkClass obj) {
			return obj.getIri().hashCode();
		}

		@Override
		public Integer visit(ElkObjectComplementOf obj) {
			return HashGenerator.combineListHash(ElkObjectComplementOf.class.hashCode(), hashCode(obj.getClassExpression()));
		}

		@Override
		public Integer visit(ElkObjectIntersectionOf obj) {
			return HashGenerator.combineMultisetHash(true, hashes(ElkObjectIntersectionOf.class.hashCode(), obj.getClassExpressions()));
		}

		@Override
		public Integer visit(ElkObjectOneOf obj) {
			return HashGenerator.combineMultisetHash(true, hashes(ElkObjectOneOf.class.hashCode(), obj.getIndividuals()));
		}

		@Override
		public Integer visit(ElkObjectSomeValuesFrom obj) {
			return HashGenerator.combineListHash(hashCode(obj.getProperty()), hashCode(obj.getFiller()));
		}

		@Override
		public Integer visit(ElkObjectUnionOf obj) {
			return HashGenerator.combineMultisetHash(true, hashes(ElkObjectUnionOf.class.hashCode(), obj.getClassExpressions()));
		}

		@Override
		public Integer visit(ElkObjectPropertyChain obj) {
			return HashGenerator.combineListHash(hashes(ElkObjectPropertyChain.class.hashCode(), obj.getObjectPropertyExpressions()));
		}

		@Override
		public Integer visit(ElkObjectProperty obj) {
			return obj.getIri().hashCode();
		}

		@Override
		public Integer visit(ElkNamedIndividual obj) {
			return obj.getIri().hashCode();
		}

		@Override
		public Integer visit(ElkIri iri) {
			return iri.hashCode();
		}
		
	}	
	
	
	private static int[] hashes(int classHash, List<? extends ElkObject> objects) {
		int[] hashes = new int[objects.size() + 1];
		
		hashes[0] = classHash;
		
		for (int i = 0; i < objects.size(); i++) {
			hashes[i + 1] = hashCode(objects.get(i));
		}
		
		return hashes;
	}
}
