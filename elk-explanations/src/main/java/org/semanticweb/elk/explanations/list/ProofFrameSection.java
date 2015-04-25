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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.elk.explanations.OWLRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;

/**
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class ProofFrameSection implements OWLFrameSection<OWLExpression, OWLAxiom, OWLAxiom> {

    private boolean filled = false;
    
    private Iterable<? extends OWLExpression> expressions_;
    
    private final int depth_;
    
    private String label_;
    
    private final ProofFrame frame_;
    
    private final List<ProofFrameSectionRow> rows_ = new ArrayList<ProofFrameSectionRow>(2);
    
    private final OWLRenderer renderer_;
    
    public ProofFrameSection(ProofFrame frame, Iterable<? extends OWLExpression> exprs, String label, int depth, OWLRenderer r) {
    	frame_ = frame;
        expressions_ = exprs;
        depth_ = depth;
        label_ = label;
        renderer_ = r;
    }

	int getDepth() {
    	return depth_;
    }
    
    @Override
	public String getName() {
    	String rendering = label_;
    	
		for (int i = 0; i < depth_; i++) {
			rendering = "       " + rendering;
		}
		
		return rendering;
	}
    
    void setLabel(String l) {
    	label_ = l;
    }
    
    void setSelected(boolean sel) {
    	for (ProofFrameSectionRow row : rows_) {
    		row.setSelected(sel);
    	}
    }

    void update(Iterable<? extends OWLExpression> newExpressions) {
    	//FIXME
		//System.err.println("Updating section " + toString());
    	
    	expressions_ = newExpressions;
    	
    	for (OWLExpression expr : newExpressions) {
    		// find the row which corresponds to the updated premise
    		ProofFrameSectionRow row = getRow(expr);
    		
    		if (row == null) {
    			// this is some new expression
    			row = new ProofFrameSectionRow(this, expr, OWLProofUtils.getAxiom(expr), depth_, renderer_);
    			
    			//FIXME
    			//System.err.println("Row added " + row.getRoot());
    			
                rows_.add(row);
    		}
    		else {
    			// updating this row
    			row.update(expr);
    		}
    	}

    	// removing obsolete rows
    	Iterator<ProofFrameSectionRow> rowIter = rows_.iterator();    	
    	
    	for (; rowIter.hasNext();) {
    		ProofFrameSectionRow row = rowIter.next();
    		
    		if (findExpression(row.getRoot(), newExpressions) == null) {
    			
    			//FIXME
    			//System.err.println("Row removed " + row.getRoot());    			
    			
    			rowIter.remove();
    		}
    	}    	
    }
    
    @Override
	public boolean checkEditorResults(OWLObjectEditor<OWLAxiom> editor) {
		return true;
	}

	@Override
	public boolean dropObjects(List<OWLObject> objects) {
		return false;
	}

	@Override
	public void dispose() {
		for (ProofFrameSectionRow row : rows_) {
			row.disposeOfRow();
		}
		
		rows_.clear();
	}

	@Override
	public ProofFrame getFrame() {
		return frame_;
	}

	@Override
	public void setRootObject(OWLExpression rootObject) {
		// no-op
	}

	@Override
	public String getLabel() {
		return label_;
	}

	@Override
	public String getRowLabel(
			OWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom> row) {
		return null;
	}

	@Override
	public OWLExpression getRootObject() {
		return frame_.getRootObject();
	}

	@Override
	public List<OWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom>> getRows() {
		return Collections.<OWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom>>unmodifiableList(rows_);
	}

	@Override
	public List<OWLAxiom> getAxioms() {
		List<OWLAxiom> axioms = new ArrayList<OWLAxiom>();
		
    	for (ProofFrameSectionRow row : rows_) {
    		axioms.add(row.getAxiom());
    	}
    	
    	return axioms;
	}
	
	@Override
	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(label_).append(":\n");
        
        for (ProofFrameSectionRow row : rows_) {
            sb.append("\t").append(row).append("\n");
        }
        
        return sb.toString();
    }

	@SuppressWarnings("rawtypes")
	@Override
	public int getRowIndex(OWLFrameSectionRow row) {
		return rows_.indexOf(row);
	}

	@Override
	public OWLObjectEditor<OWLAxiom> getEditor() {
		return null;
	}

	private OWLExpression findExpression(OWLExpression expr, Iterable<? extends OWLExpression> expressions) {
		for (OWLExpression existing : expressions) {
			if (existing.equals(expr)) {
				return existing;
			}
		}
		
		return null;
	}

	private ProofFrameSectionRow getRow(OWLExpression expr) {
		// TODO inefficient, index rows by expressions
    	for (OWLFrameSectionRow<OWLExpression, OWLAxiom, ?> row : getRows()) {
    		ProofFrameSectionRow premiseRow = (ProofFrameSectionRow) row;
    		
    		if (premiseRow.match(expr)) {
    			return premiseRow;
    		}
    	}
    	
		return null;
	}

    protected void refill() {
        if(filled) {
            return;
        }
        
        filled = true;

        for (OWLExpression premise : expressions_) {
            ProofFrameSectionRow row = new ProofFrameSectionRow(this, premise, OWLProofUtils.getAxiom(premise), depth_, renderer_);
            
            rows_.add(row);
        }
    }

    protected void clear() {
        filled = false;
        rows_.clear();
    }

    public Comparator<OWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom>> getRowComparator() {
        return null;
    }

    @Override
    public boolean canAdd() {
        return false;
    }

    @Override
    public boolean canAcceptDrop(List<OWLObject> objects) {
        return false;
    }

	/**
	 * Returns true if the inference's name matches this sections's label and
	 * the inference's premises match this section's expression (this is
	 * order-sensitive).
	 */
	public boolean match(OWLInference inference) {
		if (!label_.equals(inference.getName())) {
			return false;
		}
		
		Iterator<? extends OWLExpression> premiseIter = inference.getPremises().iterator();
		
		for (OWLExpression expr : expressions_) {
			if (!premiseIter.hasNext()) {
				return false;
			}
			
			OWLExpression premise = premiseIter.next();
			
			if (!expr.equals(premise)) {
				return false;
			}
		}
		
		return !premiseIter.hasNext();
	}
    
}
