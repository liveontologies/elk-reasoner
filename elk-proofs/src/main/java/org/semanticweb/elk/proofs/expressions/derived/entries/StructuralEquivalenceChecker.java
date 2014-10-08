/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived.entries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
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
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.AbstractElkObjectVisitor;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedLemmaExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkClassExpressionWrap;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;


/**
 * Implements structural equivalence checking on ELK axioms and lemmas.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class StructuralEquivalenceChecker implements ExpressionEqualityChecker, ExpressionVisitor<Expression, Boolean> {

	public static boolean equal(ElkObject first, ElkObject second) {
		return EntityEquivalenceChecker.equal(first, second);
	}
	
	public static boolean equal(ElkAxiom first, ElkAxiom second) {
		return AxiomEquivalenceChecker.equal(first, second);
	}
	
	public static boolean equal(ElkLemma first, ElkLemma second) {
		return LemmaEquivalenceChecker.equal(first, second);
	}
	
	public static boolean equal(ElkComplexClassExpression first, ElkComplexClassExpression second) {
		return EntityEquivalenceChecker.equal(first, second);
	}
	
	@Override
	public boolean equal(final Expression first, final Expression second) {
		return first.accept(this, second);
	}

	@Override
	public Boolean visit(DerivedAxiomExpression expr, Expression second) {
		return second instanceof DerivedAxiomExpression ? AxiomEquivalenceChecker.equal(expr.getAxiom(), ((DerivedAxiomExpression) second).getAxiom()) : Boolean.FALSE;
	}

	@Override
	public Boolean visit(DerivedLemmaExpression expr, Expression second) {
		return second instanceof DerivedLemmaExpression ? LemmaEquivalenceChecker.equal(expr.getLemma(), ((DerivedLemmaExpression)second).getLemma()) : Boolean.FALSE;
	}

	private static class DefaultLemmaChecker  implements ElkLemmaVisitor<ElkLemma, Boolean> {

		@Override
		public Boolean visit(ElkReflexivePropertyChainLemma lemma,
				ElkLemma input) {
			return false;
		}

		@Override
		public Boolean visit(ElkSubClassOfLemma lemma, ElkLemma input) {
			return false;
		}

		@Override
		public Boolean visit(ElkSubPropertyChainOfLemma lemma, ElkLemma input) {
			return false;
		}
		
	}
	
	private static class LemmaEquivalenceChecker {

		static boolean equal(ElkLemma first, final ElkLemma second) {
			return first.accept(new DefaultLemmaChecker() {

				@Override
				public Boolean visit(final ElkReflexivePropertyChainLemma first,
						ElkLemma input) {
					return second.accept(new DefaultLemmaChecker() {
						@Override
						public Boolean visit(ElkReflexivePropertyChainLemma second,
								ElkLemma input) {
							return EntityEquivalenceChecker.equal(first.getPropertyChain(), second.getPropertyChain());
						}
						
					}, input);
				}

				@Override
				public Boolean visit(final ElkSubClassOfLemma first, ElkLemma input) {
					return second.accept(new DefaultLemmaChecker() {
						@Override
						public Boolean visit(ElkSubClassOfLemma second,
								ElkLemma input) {
							return EntityEquivalenceChecker.equal(first.getSubClass(), second.getSubClass())
									&& EntityEquivalenceChecker.equal(first.getSuperClass(), second.getSuperClass());
						}
						
					}, input);
				}

				@Override
				public Boolean visit(final ElkSubPropertyChainOfLemma first,
						ElkLemma input) {
					return second.accept(new DefaultLemmaChecker() {
						@Override
						public Boolean visit(ElkSubPropertyChainOfLemma second,
								ElkLemma input) {
							return EntityEquivalenceChecker.equal(first.getSubPropertyChain(), second.getSubPropertyChain())
									&& EntityEquivalenceChecker.equal(first.getSuperPropertyChain(), second.getSuperPropertyChain());
						}
						
					}, input);
				}
				
			}, null);
		}
		
	}
	
	private static class DefaultAxiomChecker extends AbstractElkAxiomVisitor<Boolean> {
		@Override
		protected Boolean defaultLogicalVisit(ElkAxiom axiom) {
			return Boolean.FALSE;
		}

		@Override
		protected Boolean defaultNonLogicalVisit(ElkAxiom axiom) {
			return Boolean.FALSE;
		}
	}
	
	private static class AxiomEquivalenceChecker {

		static boolean equal(final ElkAxiom first, final ElkAxiom second) {
			return first.accept(new DefaultAxiomChecker() {
				
				@Override
				public Boolean visit(final ElkDisjointClassesAxiom first) {
					return second.accept(new DefaultAxiomChecker() {

						@Override
						public Boolean visit(ElkDisjointClassesAxiom second) {
							// two lists of disjoint classes are equal if
							// they're equal as sets and both have the same sets
							// of classes appearing more than once
							return equalDisjointnessAxioms(first, second);
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkEquivalentClassesAxiom first) {
					return second.accept(new DefaultAxiomChecker() {

						@Override
						public Boolean visit(ElkEquivalentClassesAxiom second) {
							// compare lists of equivalent classes as sets
							return equalAsSets(first.getClassExpressions(), second.getClassExpressions());
						}

					});
				}

				
				@Override
				public Boolean visit(final ElkSubClassOfAxiom first) {
					return second.accept(new DefaultAxiomChecker() {

						@Override
						public Boolean visit(
								ElkSubClassOfAxiom second) {
							return EntityEquivalenceChecker.equal(first.getSubClassExpression(), second.getSubClassExpression())
									&& EntityEquivalenceChecker.equal(first.getSuperClassExpression(), second.getSuperClassExpression());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkEquivalentObjectPropertiesAxiom first) {
					return second.accept(new DefaultAxiomChecker() {

						@Override
						public Boolean visit(
								ElkEquivalentObjectPropertiesAxiom second) {
							return equalAsSets(first.getObjectPropertyExpressions(), second.getObjectPropertyExpressions());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkObjectPropertyDomainAxiom first) {
					return first.accept(new DefaultAxiomChecker() {
						@Override
						public Boolean visit(
								ElkObjectPropertyDomainAxiom second) {
							return EntityEquivalenceChecker.equal(first.getDomain(), second.getDomain()) 
									&& EntityEquivalenceChecker.equal(first.getProperty(), second.getProperty());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkReflexiveObjectPropertyAxiom first) {
					return first.accept(new DefaultAxiomChecker() {
						@Override
						public Boolean visit(
								ElkReflexiveObjectPropertyAxiom second) {
							return EntityEquivalenceChecker.equal(first.getProperty(), second.getProperty());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkSubObjectPropertyOfAxiom first) {
					return first.accept(new DefaultAxiomChecker() {
						@Override
						public Boolean visit(
								ElkSubObjectPropertyOfAxiom second) {
							return EntityEquivalenceChecker.equal(first.getSubObjectPropertyExpression(), second.getSubObjectPropertyExpression())
									&& EntityEquivalenceChecker.equal(first.getSuperObjectPropertyExpression(), second.getSuperObjectPropertyExpression());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkTransitiveObjectPropertyAxiom first) {
					return first.accept(new DefaultAxiomChecker() {
						@Override
						public Boolean visit(
								ElkTransitiveObjectPropertyAxiom second) {
							return EntityEquivalenceChecker.equal(first.getProperty(), second.getProperty());
						}
						
					});
				}
				
			});
		}
		
	}
	
	private static class EntityChecker extends AbstractElkObjectVisitor<Boolean> implements ElkComplexClassExpressionVisitor<ElkObject, Boolean> {
		
		@Override
		protected Boolean defaultVisit(ElkObject obj) {
			return Boolean.FALSE;
		}

		@Override
		public Boolean visit(ElkComplexObjectSomeValuesFrom ce, ElkObject input) {
			return Boolean.FALSE;
		}

		@Override
		public Boolean visit(ElkClassExpressionWrap ce, ElkObject input) {
			return Boolean.FALSE;
		}
		
	}
	
	/**
	 * Equivalence check on OWL entities.
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class EntityEquivalenceChecker {

		static boolean equal(ElkObject first, final ElkObject second) {
			return first.accept(new EntityChecker() {

				@Override
				public Boolean visit(final ElkSameIndividualAxiom first) {
					return second.accept(new EntityChecker() {
						@Override
						public Boolean visit(ElkSameIndividualAxiom second) {
							return equalAsSets(first.getIndividuals(), second.getIndividuals());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkClass first) {
					return second.accept(new EntityChecker() {
						@Override
						public Boolean visit(ElkClass second) {
							return first.getIri().equals(second.getIri());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkObjectComplementOf first) {
					return second.accept(new EntityChecker() {
						@Override
						public Boolean visit(ElkObjectComplementOf second) {
							return equal(first.getClassExpression(), second.getClassExpression());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkObjectIntersectionOf first) {
					return second.accept(new EntityChecker() {
						@Override
						public Boolean visit(ElkObjectIntersectionOf second) {
							return equalAsSets(first.getClassExpressions(), second.getClassExpressions());
						}
						
					});
				}

				@Override
				public Boolean	visit(final ElkObjectOneOf first) {
					return second.accept(new EntityChecker() {
						@Override
						public Boolean visit(ElkObjectOneOf second) {
							return equalAsSets(first.getIndividuals(), second.getIndividuals());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkObjectSomeValuesFrom first) {
					return second.accept(new EntityChecker() {
						@Override
						public Boolean visit(ElkObjectSomeValuesFrom second) {
							return equal(first.getProperty(), second.getProperty()) && equal(first.getFiller(), second.getFiller());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkObjectUnionOf first) {
					return second.accept(new EntityChecker() {
						@Override
						public Boolean visit(ElkObjectUnionOf second) {
							return equalAsSets(first.getClassExpressions(), second.getClassExpressions());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkObjectPropertyChain first) {
					return second.accept(new EntityChecker() {
						@Override
						public Boolean visit(ElkObjectPropertyChain second) {
							return equalAsLists(first.getObjectPropertyExpressions(), second.getObjectPropertyExpressions());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkObjectProperty first) {
					return second.accept(new EntityChecker() {
						@Override
						public Boolean visit(ElkObjectProperty second) {
							return first.getIri().equals(second.getIri());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkNamedIndividual first) {
					return second.accept(new EntityChecker() {
						@Override
						public Boolean visit(ElkNamedIndividual second) {
							return first.getIri().equals(second.getIri());
						}
						
					});
				}

				@Override
				public Boolean visit(final ElkIri first) {
					return second.accept(new EntityChecker() {
						@Override
						public Boolean visit(ElkIri second) {
							return first.equals(second);
						}
						
					});
				}
				
			});
		}
		
		static boolean equal(ElkComplexClassExpression first, final ElkComplexClassExpression second) {
			
			return first.accept(new EntityChecker() {
				@Override
				public Boolean visit(final ElkComplexObjectSomeValuesFrom first,
						ElkObject input) {
					return second.accept(new EntityChecker() {

						@Override
						public Boolean visit(ElkComplexObjectSomeValuesFrom second,
								ElkObject input) {
							return equal(first.getPropertyChain(), second.getPropertyChain()) 
									&& equal(first.getFiller(), second.getFiller());
						}
						
					}, null);
				}

				@Override
				public Boolean visit(final ElkClassExpressionWrap first, ElkObject input) {
					return second.accept(new EntityChecker() {

						@Override
						public Boolean visit(ElkClassExpressionWrap second,
								ElkObject input) {
							return equal(first.getClassExpression(), second.getClassExpression());
						}
						
					}, null);
				}
			}, null);
		}
		
	}
	
	private static boolean equalDisjointnessAxioms(ElkDisjointClassesAxiom first, ElkDisjointClassesAxiom second) {
		/*
		 * true if: 
		 * i)  sets of self-inconsistent classes are equal (as sets) 
		 * ii) other classes are equal as sets 
		 * This is a straightforward
		 * implementation which separates self-inconsistent classes in each
		 * axioms from the rest and compares both 
		 * TODO optimize?
		 */
		Set<ElkObjectEntry> firstInconsistent = new HashSet<ElkObjectEntry>(1);
		Set<ElkObjectEntry> secondInconsistent = new HashSet<ElkObjectEntry>(1);
		Set<ElkObjectEntry> firstDisjoint = new HashSet<ElkObjectEntry>(first.getClassExpressions().size());
		Set<ElkObjectEntry> secondDisjoint = new HashSet<ElkObjectEntry>(second.getClassExpressions().size());
		// separating out disjoint members for both collections
		for (ElkClassExpression ice : first.getClassExpressions()) {
			ElkObjectEntry entry = new ElkObjectEntry(ice);
			
			if (firstInconsistent.contains(entry)) {
				continue;
			}
			
			if (!firstDisjoint.add(entry)) {
				firstInconsistent.add(entry);
			}
		}
		
		for (ElkClassExpression ice : second.getClassExpressions()) {
			ElkObjectEntry entry = new ElkObjectEntry(ice);
			
			if (secondInconsistent.contains(entry)) {
				continue;
			}
			
			if (!secondDisjoint.add(entry)) {
				secondInconsistent.add(entry);
			}
		}
			
		return firstInconsistent.equals(secondInconsistent) && firstDisjoint.equals(secondDisjoint);
	}
	
	private static boolean equalAsLists(List<? extends ElkObject> first, List<? extends ElkObject> second) {
		if (first.size() != second.size()) {
			return false;
		}
		
		for (int i = 0; i < first.size(); i++) {
			if (!EntityEquivalenceChecker.equal(first.get(i), second.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	private static boolean equalAsSets(List<? extends ElkObject> first, List<? extends ElkObject> second) {
		// TODO optimize?
		Set<? extends ElkObject> secondCopy = new HashSet<ElkObject>(second);
		
		for (ElkObject ce1 : first) {
			boolean found = false;
			
			for (ElkObject ce2 : second) {
				if (EntityEquivalenceChecker.equal(ce1, ce2)) {
					found = true;
					secondCopy.remove(ce2);
					break;
				}
			}
			
			if (!found) {
				return false;
			}
		}
		
		for (ElkObject ce2 : secondCopy) {
			boolean found = false;
			
			for (ElkObject ce1 : first) {
				if (EntityEquivalenceChecker.equal(ce1, ce2)) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				return false;
			}
		}
		
		return true;
	}				

	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class ElkObjectEntry {
		
		private final ElkObject object_;
		
		ElkObjectEntry(ElkObject obj) {
			object_ = obj;
		}

		@Override
		public int hashCode() {
			return StructuralEquivalenceHasher.hashCode(object_);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof ElkObjectEntry)) {
				return false;
			}
			
			return EntityEquivalenceChecker.equal(object_, ((ElkObjectEntry) obj).object_);
		}

		@Override
		public String toString() {
			return OwlFunctionalStylePrinter.toString(object_);
		}
		
		
	}
}
