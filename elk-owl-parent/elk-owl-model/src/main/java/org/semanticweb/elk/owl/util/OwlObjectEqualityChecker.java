/**
 * 
 */
package org.semanticweb.elk.owl.util;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.AbstractElkObjectVisitor;

/**
 * Implements equality for {@link ElkObject}s.
 * 
 * TODO incomplete
 * TODO probably creates too many visitor objects during the recursive checks
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class OwlObjectEqualityChecker extends AbstractElkObjectVisitor<Boolean> {

	@Override
	protected Boolean defaultVisit(ElkObject obj) {
		return Boolean.FALSE;
	}	
	
	public boolean disjointClassesEqual(List<? extends ElkClassExpression> first, List<? extends ElkClassExpression> second) {
		// TODO
		return false;
	}
	
	public boolean equals(final ElkObject first, final ElkObject second) {
		return first.accept(new OwlObjectEqualityChecker() {

			@Override
			public Boolean visit(final ElkClass first) {
				return second.accept(new OwlObjectEqualityChecker() {

					@Override
					public Boolean visit(ElkClass second) {
						return equals(first.getIri(), second.getIri());
					}
					
				});
			}
			
			@Override
			public Boolean visit(final ElkDisjointClassesAxiom first) {
				return second.accept(new OwlObjectEqualityChecker() {

					@Override
					public Boolean visit(ElkDisjointClassesAxiom second) {
						return disjointClassesEqual(first.getClassExpressions(), second.getClassExpressions());
					}
					
				});
			}

			@Override
			public Boolean visit(ElkEquivalentClassesAxiom obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkSubClassOfAxiom obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkEquivalentObjectPropertiesAxiom obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkSubObjectPropertyOfAxiom obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkTransitiveObjectPropertyAxiom obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkClassAssertionAxiom obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkObjectPropertyAssertionAxiom obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkObjectComplementOf obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkObjectIntersectionOf obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkObjectOneOf obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkObjectSomeValuesFrom obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkObjectUnionOf obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkObjectPropertyChain obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkObjectProperty obj) {
				// TODO Auto-generated method stub
				return super.visit(obj);
			}

			@Override
			public Boolean visit(ElkNamedIndividual elkNamedIndividual) {
				// TODO Auto-generated method stub
				return super.visit(elkNamedIndividual);
			}

			@Override
			public Boolean visit(ElkIri iri) {
				// TODO Auto-generated method stub
				return super.visit(iri);
			}
			
			
		});
	}
	
}
