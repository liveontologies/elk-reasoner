/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkClassExpressionWrap;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;
import org.semanticweb.elk.proofs.utils.ElkLemmaPrinter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexObjectConverter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubPropertyChain;

/**
 * Converts expressions to either a pair (context, conclusion) or (object
 * property conclusion) for which we can request tracing from the reasoner.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class ExpressionTracingInputVisitor extends
		AbstractElkAxiomVisitor<TracingInput> implements
		ExpressionVisitor<Void, TracingInput>,
		ElkLemmaVisitor<Void, TracingInput> {

	private final IndexObjectConverter converter_;
	
	ExpressionTracingInputVisitor(IndexObjectConverter c) {
		converter_ = c;
	}
	
	@Override
	public TracingInput visit(ElkReflexivePropertyChainLemma lemma, Void input) {
		IndexedPropertyChain chain = lemma.getPropertyChain().accept(converter_);
		
		return new ObjectPropertyTracingInput(new ReflexivePropertyChain<IndexedPropertyChain>(chain));
	}

	@Override
	public TracingInput visit(ElkSubClassOfLemma lemma, Void input) {
		// subsumee must be a simple class expression
		IndexedClassExpression subsumee = lemma.getSubClass().accept(new ElkComplexClassExpressionVisitor<Void, IndexedClassExpression>() {

			@Override
			public IndexedClassExpression visit(
					ElkComplexObjectSomeValuesFrom ce, Void input) {
				throw new IllegalStateException(String.format("%s cannot be explained because the LHS is not an OWL class expression", ElkLemmaPrinter.print(ce)));
			}

			@Override
			public IndexedClassExpression visit(ElkClassExpressionWrap ce, 	Void input) {
				return ce.getClassExpression().accept(converter_);
			}
		}, input);
		
		Conclusion subsumer = lemma.getSuperClass().accept(new ElkComplexClassExpressionVisitor<Void, Conclusion>() {

			@Override
			public Conclusion visit(ElkComplexObjectSomeValuesFrom ce, Void input) {
				// a forward link
				IndexedPropertyChain chain = ce.getPropertyChain().accept(converter_);
				IndexedClassExpression filler = ce.getFiller().accept(converter_);
				
				return new ForwardLinkImpl(chain, filler);
			}

			@Override
			public Conclusion visit(ElkClassExpressionWrap ce, Void input) {
				return new DecomposedSubsumerImpl<IndexedClassExpression>(ce.getClassExpression().accept(converter_));
			}
			
		}, input);
		
		return new ClassTracingInput(subsumee, subsumer);
	}

	@Override
	public TracingInput visit(ElkSubPropertyChainOfLemma lemma, Void input) {
		IndexedPropertyChain subchain = lemma.getSubPropertyChain().accept(converter_);
		IndexedPropertyChain superchain = lemma.getSuperPropertyChain().accept(converter_);
		
		return new ObjectPropertyTracingInput(new SubPropertyChain<IndexedPropertyChain, IndexedPropertyChain>(subchain, superchain));
	}

	@Override
	public TracingInput visit(AxiomExpression expr, Void input) {
		return expr.getAxiom().accept(this);
	}

	@Override
	public TracingInput visit(LemmaExpression expr, Void input) {
		return expr.getLemma().accept(this, input);
	}

	@Override
	public TracingInput visit(ElkSubClassOfAxiom ax) {
		IndexedClassExpression subsumee = ax.getSubClassExpression().accept(converter_);
		IndexedClassExpression subsumer = ax.getSuperClassExpression().accept(converter_);
		
		return new ClassTracingInput(subsumee, new DecomposedSubsumerImpl<IndexedClassExpression>(subsumer));
	}
	
}
