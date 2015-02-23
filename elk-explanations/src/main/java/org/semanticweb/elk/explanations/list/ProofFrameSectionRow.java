package org.semanticweb.elk.explanations.list;
/*
 * #%L
 * Explanation Workbench
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.elk.explanations.OWLRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;

/**
 * Represents an item in the list which holds an {@link OWLExpression} represented by an {@link OWLAxiom}.
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class ProofFrameSectionRow implements OWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom>{

    private final int depth_;
    
    private boolean expanded_ = false;
    
    private boolean selected_ = false;
    
    private List<ProofFrameSection> inferenceSections_ = new ArrayList<ProofFrameSection>(2);
    
    private OWLExpression expression_;
    
    private final ProofFrameSection section_;
    
    private final OWLRenderer renderer_;

    public ProofFrameSectionRow(ProofFrameSection section, OWLExpression rootObject, OWLAxiom axiom, int depth, OWLRenderer r) {
    	section_ = section;
    	expression_ = rootObject;
        depth_ = depth;
        renderer_ = r;
    }

    public int getDepth() {
        return depth_;
    }
    
    boolean isExpanded() {
    	return expanded_;
    }
    
    void setExpanded(boolean v) {
    	expanded_ = v;
    }
    
    boolean isSelected() {
    	return selected_;
    }
    
    void setSelected(boolean v) {
    	selected_ = v;
    }
    
    boolean isFilled() {
    	return !isInferred() || !inferenceSections_.isEmpty();
    }
    
    List<ProofFrameSection> getInferenceSections() {
    	return inferenceSections_;
    }
    
    void refillInferenceSections() {
    	inferenceSections_.clear();
    	
    	try {
			for (OWLInference inf : expression_.getInferences()) {
				ProofFrameSection inferenceSection = new ProofFrameSection(getFrameSection().getFrame(), inf.getPremises(), inf.getName(), depth_ + 1, renderer_);
				// filling up rows with premises
				inferenceSection.refill();
				inferenceSections_.add(inferenceSection);
			}
		} catch (ProofGenerationException e) {
			ProtegeApplication.getErrorLog().logError(e);
		}
    }

    public List<? extends OWLObject> getManipulatableObjects() {
        return Arrays.asList(getAxiom());
    }
    
    @Override
	public ProofFrameSection getFrameSection() {
		return section_;
	}

	@Override
    public boolean isEditable() {
        return !isInferred();
    }

    @Override
    public boolean isDeleteable() {
        return isEditable();
    }

    @Override
    public boolean isInferred() {
        return !OWLProofUtils.isAsserted(getRoot());
    }

	public void disposeOfRow() {
		for (ProofFrameSection section : inferenceSections_) {
			section.dispose();
		}
	}

	public void update(OWLExpression expression) {
		//FIXME
		System.err.println("Updating row " + toString());
		
		expression_ = expression;
		
		try {
			Iterable<? extends OWLInference> inferences = expression.getInferences();
			// remove obsolete sections first
			Set<ProofFrameSection> sectionsCopy = new HashSet<ProofFrameSection>(inferenceSections_);
			List<ProofFrameSection> newSections = new ArrayList<ProofFrameSection>();
			
			// update existing sections and add new ones
			for (OWLInference inf : inferences) {
				ProofFrameSection section = findSection(inf);
				
				if (section == null) {
					section = new ProofFrameSection(getFrameSection().getFrame(), inf.getPremises(), inf.getName(), depth_ + 1, renderer_);
					// filling up rows with premises
					section.refill();
					newSections.add(section);
					
					//FIXME
					System.err.println("Adding section:" + section);
				}
				else {
					// update the section
					sectionsCopy.remove(section);
				}
				
				section.update(inf.getPremises());
			}
			
			//removing sections for which there are no more inferences
			//FIXME
			System.err.println("Removing sections:\n" + sectionsCopy);
			
			inferenceSections_.removeAll(sectionsCopy);
			
			if (expanded_) {
				// don't expand expressions which are currently unexpanded
				inferenceSections_.addAll(newSections);
			}
			
		} catch (ProofGenerationException e) {
			ProtegeApplication.getErrorLog().logError(e);
		}
		
	}

	ProofFrameSection findSection(OWLInference inference) {
		// TODO slow, index sections by inferences?
		for (ProofFrameSection section : inferenceSections_) {
			if (section.match(inference)) {
				return section;
			};
		}
		
		return null;
	}

	@Override
	public OWLObjectEditor<OWLAxiom> getEditor() {
		return null;
	}

	@Override
	public boolean checkEditorResults(OWLObjectEditor<OWLAxiom> editor) {
		return true;
	}

	@Override
	public boolean canAcceptDrop(List<OWLObject> objects) {
		return false;
	}

	@Override
	public boolean dropObjects(List<OWLObject> objects) {
		return false;
	}

	@Override
	public String getTooltip() {
		return isInferred() ? "Inferred" : "Asserted";
	}

    public void handleEdit() {
    }


    public boolean handleDelete() {
        return false;
    }

	@Override
	public OWLExpression getRoot() {
		return expression_;
	}

	@Override
	public OWLAxiom getAxiom() {
		return OWLProofUtils.getAxiom(expression_);
	}

	@Override
	public Object getUserObject() {
		return null;
	}

	@Override
	public void setUserObject(Object object) {
		// no-op
	}

	@Override
	public OWLOntology getOntology() {
		return getFrameSection().getFrame().getActiveOntology();
	}

	@Override
	public OWLOntologyManager getOWLOntologyManager() {
		return null;
	}

	@Override
	public List<? extends OWLOntologyChange> getDeletionChanges() {
		return Collections.singletonList(new RemoveAxiom(getOntology(), getAxiom()));
	}

    public String getRendering() {
    	OWLObject ax = getAxiom();
    	
        return renderer_.render(ax);
    }
	
    public String toString() {
        return getRendering();
    }

	public boolean match(OWLExpression expr) {
		return expression_.equals(expr);
	}

}
