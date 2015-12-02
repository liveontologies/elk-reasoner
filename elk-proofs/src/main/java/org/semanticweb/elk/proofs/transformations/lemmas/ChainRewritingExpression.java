/**
 * 
 */
package org.semanticweb.elk.proofs.transformations.lemmas;
/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.ExpressionFactory;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.inferences.util.TracingUtils;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.stages.ReasonerInferenceReader;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * Represents expressions of the form R1 o R2 o ... o Rn <= V for which we may not have recorded inferences.
 * Instead, we know the sub-property chain axiom S1 o S2 o ... o Sm <= V from which the given expression follows (using other axioms).
 * This class recreates such inferences using recorded inferences for substrings of R1 o R2 o ... o Rn.
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
class ChainRewritingExpression implements AxiomExpression<ElkSubObjectPropertyOfAxiom> {

	private final AxiomExpression<ElkSubObjectPropertyOfAxiom> expr_;

	private final List<IndexedObjectProperty> indexedChainList_;
	
	private final List<IndexedObjectProperty> indexedTargetChainList_;
	
	private final ElkObjectFactory elkFactory_ = new ElkObjectFactoryImpl();
	
	private final ReasonerInferenceReader reader_;
	
	ChainRewritingExpression(AxiomExpression<ElkSubObjectPropertyOfAxiom> expr, 
			ElkSubObjectPropertyOfAxiom targetAxiom, ReasonerInferenceReader reader) {
		expr_ = expr;
		indexedChainList_ = index(((ElkObjectPropertyChain) expr.getAxiom().getSubObjectPropertyExpression()).getObjectPropertyExpressions(), reader.getExpressionConverter());
		indexedTargetChainList_ = index(((ElkObjectPropertyChain) targetAxiom.getSubObjectPropertyExpression()).getObjectPropertyExpressions(), reader.getExpressionConverter());
		reader_ = reader;
	}
	
	ChainRewritingExpression(AxiomExpression<ElkSubObjectPropertyOfAxiom> expr, 
			List<IndexedObjectProperty> indexedChainList,
			List<IndexedObjectProperty> indexedTargetChainList,
			ReasonerInferenceReader reader) {
		expr_ = expr;
		indexedChainList_ = indexedChainList;
		indexedTargetChainList_ = indexedTargetChainList;
		reader_ = reader;
	}

	private ExpressionFactory getExpressionFactory() {
		return reader_.getExpressionFactory();
	}
	
	@Deprecated
	private ElkSubObjectPropertyExpressionVisitor<? extends IndexedPropertyChain> getSubPropertyConverter() {
		return reader_.getSubPropertyConverter();
	}
	
	@Deprecated
	private ElkPolarityExpressionConverter getExpressionConverter() {
		return reader_.getExpressionConverter();
	}

	
	@Override
	public ElkSubObjectPropertyOfAxiom getAxiom() {
		return expr_.getAxiom();
	}

	@Override
	public boolean isAsserted() {
		return expr_.isAsserted();
	}
	
	@Override
	public String toString() {
		return expr_.toString();
	}

	@Override
	public <I, O> O accept(ExpressionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public Iterable<? extends Inference> getInferences() throws ElkException {
		return rewrite(indexedChainList_, indexedTargetChainList_);
	}

	private Iterable<? extends Inference> rewrite(List<IndexedObjectProperty> indexedChain, List<IndexedObjectProperty> indexedTargetChain) {
		// try to rewrite substrings starting from the back and going to the left
		List<Inference> inferences = new ArrayList<Inference>();
		
		for (int index = indexedChain.size() - 2; index >=0; index--) {
			IndexedComplexPropertyChain subChain = (IndexedComplexPropertyChain) binarize(indexedChain.subList(index,  indexedChain.size()));
			Multimap<IndexedPropertyChain, ObjectPropertyInference> leftSuperChainToInferences = getSuperChainToInferencesMultimap(subChain.getFirstProperty());
			Multimap<IndexedPropertyChain, ObjectPropertyInference> rightSuperChainToInferences = getSuperChainToInferencesMultimap(subChain.getSuffixChain());
			
			for (IndexedPropertyChain leftSuperChain : leftSuperChainToInferences.keySet()) {
				if (leftSuperChain instanceof IndexedObjectProperty) {
					IndexedObjectProperty leftSuper = (IndexedObjectProperty) leftSuperChain;
					// now leftSuper o subChain.getRight() is a superchain of subChain
					if (!composable(leftSuper, subChain.getSuffixChain())) {
						continue;
					}
					
					IndexedComplexPropertyChain superChain = compose(leftSuper, subChain.getSuffixChain());
					Inference inf = createInference(subChain, superChain, index, leftSuperChainToInferences.get(leftSuper));
					
					if (inf != null) {
						inferences.add(inf);
					}
				}
			}
			
			for (IndexedPropertyChain rightSuperChain : rightSuperChainToInferences.keySet()) {
				// now subChain.getLeft() o rightSuperChain is a superchain of subChain
				if (!composable(subChain.getFirstProperty(), rightSuperChain)) {
					continue;
				}
				
				IndexedComplexPropertyChain superChain = compose(subChain.getFirstProperty(), rightSuperChain);
				Inference inf = createInference(subChain, superChain, index, rightSuperChainToInferences.get(rightSuperChain));
				
				if (inf != null) {
					inferences.add(inf);
				}	
			}
		}
		
		return inferences;
	}

	private boolean composable(IndexedObjectProperty left, IndexedPropertyChain right) {
		for (IndexedPropertyChain composition : left.getSaturated().getCompositionsByRightSubProperty().get(right)) {
			if (endsWith(indexedTargetChainList_, flatten(composition))) {
				return true;
			}
		}
		
		return false;
	}

	private IndexedComplexPropertyChain compose(IndexedObjectProperty left, IndexedPropertyChain right) {
		List<ElkObjectProperty> props = new ArrayList<ElkObjectProperty>();
		
		props.add(left.getElkEntity());
		
		for (IndexedObjectProperty iop : flatten(right)) {
			props.add(iop.getElkEntity());
		}
		
		return (IndexedComplexPropertyChain) elkFactory_.getObjectPropertyChain(props).accept(getSubPropertyConverter());
	}

	private boolean endsWith(List<IndexedObjectProperty> chain, List<IndexedObjectProperty> subChain) {
		if (subChain.size() > chain.size()) {
			return false;
		}
		
		for (int i = 0; i < subChain.size(); i++) {
			IndexedObjectProperty firstItem = chain.get(i + (chain.size() - subChain.size()));
			IndexedObjectProperty secondItem = subChain.get(i);
			
			if (!firstItem.equals(secondItem)) {
				return false;
			}
		}
		
		return true;
	}

	private Multimap<IndexedPropertyChain, ObjectPropertyInference> getSuperChainToInferencesMultimap(IndexedPropertyChain chain) {
		return TracingUtils.getSuperPropertyInferenceMultimap(reader_.getTraceReader(), chain);
	}

	private List<IndexedObjectProperty> replace(List<IndexedObjectProperty> chain, List<IndexedObjectProperty> subChain, List<IndexedObjectProperty> superChain, int index) {
		List<IndexedObjectProperty> result = new ArrayList<IndexedObjectProperty>(chain.size() + superChain.size() - subChain.size());
		
		if (index > 0) {
			result.addAll(chain.subList(0, index));
		}
		
		result.addAll(superChain);
		result.addAll(chain.subList(index + subChain.size(), chain.size()));
		
		return result;
	}
	
	private Inference createInference(	final IndexedPropertyChain subChain, 
										final IndexedPropertyChain superChain,
										int subChainIndex,
										Iterable<ObjectPropertyInference> superChainInferences) {
		List<IndexedObjectProperty> premiseChain = replace(indexedChainList_, flatten(subChain), flatten(superChain), subChainIndex);
		AxiomExpression<ElkSubObjectPropertyOfAxiom> premise = null;
		ExpressionFactory exprFactory = getExpressionFactory();
		
		if (premiseChain.equals(indexedTargetChainList_)) {
			// reached the target
			premise = exprFactory.create(elkFactory_.getSubObjectPropertyOfAxiom(deindex(premiseChain), getSuperProperty()));
		}
		else {
			// still on the way to the target
			premise = new ChainRewritingExpression(
					exprFactory.create(elkFactory_.getSubObjectPropertyOfAxiom(deindex(premiseChain), getSuperProperty())),
					premiseChain, indexedTargetChainList_, reader_);
		}
		
		LemmaFreePropertyInferenceFactory roleInfFactory = new LemmaFreePropertyInferenceFactory(expr_, reader_.getExpressionFactory());
		
		for (ObjectPropertyInference inf : superChainInferences) {
			Inference result = inf.accept(roleInfFactory, premise);
			
			if (result != null) {
				return result;
			}
		}
		
		return null;
	}
	
	private List<IndexedObjectProperty> flatten(IndexedPropertyChain ipc) {
		List<IndexedObjectProperty> props = new ArrayList<IndexedObjectProperty>();
		IndexedPropertyChain top = ipc;
		
		for (;;) {
			if (top instanceof IndexedObjectProperty) {
				props.add((IndexedObjectProperty) top);
				break;
			}
			else {
				props.add(((IndexedComplexPropertyChain) top).getFirstProperty());
				top = ((IndexedComplexPropertyChain) top).getSuffixChain();
			}
		}
		
		return props;
	}
	
	private IndexedPropertyChain binarize(List<IndexedObjectProperty> chainList) {
		List<ElkObjectProperty> elkProps = new ArrayList<ElkObjectProperty>(chainList.size());
		
		for (IndexedObjectProperty iop : chainList) {
			elkProps.add(iop.getElkEntity());
		}
		
		return elkFactory_.getObjectPropertyChain(elkProps).accept(getSubPropertyConverter());
	}
	
	private ElkObjectProperty getSuperProperty() {
		return (ElkObjectProperty) expr_.getAxiom().getSuperObjectPropertyExpression();
	}
	
	private ElkSubObjectPropertyExpression deindex(List<IndexedObjectProperty> chainList) {
		List<ElkObjectProperty> props = new ArrayList<ElkObjectProperty>(chainList.size());
		
		for (IndexedObjectProperty iop : chainList) {
			props.add(iop.getElkEntity());
		}
		
		return elkFactory_.getObjectPropertyChain(props);
	}

	private static List<IndexedObjectProperty> index(List<? extends ElkObjectPropertyExpression> chain, ElkPolarityExpressionConverter converter) {
		List<IndexedObjectProperty> result = new ArrayList<IndexedObjectProperty>(chain.size());
		
		for (ElkObjectPropertyExpression next : chain) {
			result.add((IndexedObjectProperty) next.accept(converter));
		}
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		return expr_.equals(obj);
	}

	@Override
	public int hashCode() {
		return expr_.hashCode();
	}
	
}
