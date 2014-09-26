/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.proofs.InferenceReader;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.mapping.SingleInferenceMapper;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractObjectPropertyInferenceVisitor;


/**
 * Inference reader which works directly with the reasoner to request low-level
 * inferences for expressions and maps them to the user-level inferences showed
 * in proofs.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ReasonerInferenceReader implements InferenceReader {

	final AbstractReasonerState reasoner;
	
	public ReasonerInferenceReader(AbstractReasonerState r) {
		reasoner = r;
	}

	@Override
	public void initialize(ElkAxiom subsumption) throws ElkException {
		ElkSubClassOfAxiom ax = subsumption.accept(new AbstractElkAxiomVisitor<ElkSubClassOfAxiom>() {

			@Override
			public ElkSubClassOfAxiom visit(ElkSubClassOfAxiom elkSubClassOfAxiom) {
				return elkSubClassOfAxiom;
			}
			
		});
		
		if (ax != null) {
			reasoner.explainSubsumption(ax.getSubClassExpression(), ax.getSuperClassExpression());
		}
		else {
			throw new IllegalArgumentException(String.format("Only support explanations for SubClassOf axioms: %s", OwlFunctionalStylePrinter.toString(subsumption)));
		}
	}
	
	@Override
	public Iterable<Inference> getInferences(Expression expression) throws ElkException {
		final SingleInferenceMapper mapper = new SingleInferenceMapper();
		// first transform the expression into an input for trace reader
		TracingInput input = expression.accept(new ExpressionTracingInputVisitor(reasoner.getIndexObjectConverter()), null);
		TraceStore.Reader traceReader = reasoner.getTraceState().getTraceStore().getReader();
		// TODO the input shouldn't be null, we expect the above code to throw an exception if it can't transform the input
		// now requesting tracing inferences and mapping them to user inferencess
		if (input instanceof ClassTracingInput) {
			ClassTracingInput inp = (ClassTracingInput) input;
			final List<Inference> userInferences = new LinkedList<Inference>();
			
			traceReader.accept(inp.root, inp.conclusion, new AbstractClassInferenceVisitor<IndexedClassExpression, Void>() {

				@Override
				protected Void defaultTracedVisit(ClassInference inf,
						IndexedClassExpression whereStored) {
					Inference userInf = mapper.map(inf, whereStored);
					
					if (userInf != null) {
						userInferences.add(userInf);
					}
					
					return null;
				}
				
			});
			
			return userInferences;
					
		}
		else if (input instanceof ObjectPropertyTracingInput) {
			ObjectPropertyTracingInput inp = (ObjectPropertyTracingInput) input;
			final List<Inference> userInferences = new LinkedList<Inference>();
			
			traceReader.accept(inp.conclusion, new AbstractObjectPropertyInferenceVisitor<Void, Void>() {

				@Override
				protected Void defaultTracedVisit(ObjectPropertyInference inf,
						Void input) {
					Inference userInf = mapper.map(inf);
					
					if (userInf != null) {
						userInferences.add(userInf);
					}
					
					return null;
				}
				
			});
			
			return userInferences;
		}
		
		return null;
	}

}
