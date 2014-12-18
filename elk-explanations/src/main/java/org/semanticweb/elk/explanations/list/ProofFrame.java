/**
 * 
 */
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

import java.util.Collections;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class ProofFrame extends AbstractOWLFrame<OWLExpression> {
	
	//private static final Set<AxiomType<?>> EDITABLE_AXIOM_TYPES = new HashSet<AxiomType<?>>(Arrays.<AxiomType<?>>asList(AxiomType.SUBCLASS_OF, AxiomType.OBJECT_PROPERTY_DOMAIN, AxiomType.OBJECT_PROPERTY_RANGE));
	
    public ProofFrame(OWLEditorKit owlEditorKit, OWLExpression proofRoot) {
    	super(owlEditorKit.getOWLModelManager().getOWLOntologyManager());
    	
        addSection(new ProofFrameSection(owlEditorKit, this, Collections.singleton(proofRoot), "Proof tree", 0));
    }

	@Override
	protected void addSection(OWLFrameSection<? extends Object, ? extends Object, ? extends Object> section, int index) {
		super.addSection(section, index);
	}
	
	int indexOf(OWLFrameSection<? extends Object, ? extends Object, ? extends Object> section) {
		int index = 0;
		
		for (OWLFrameSection<?, ?, ?> sec : getFrameSections()) {
			if (sec == section) {
				return index;
			}
			
			index++;
		}
		
		return -1;
	}
	
	void removeSection(int index) {
		// this works only as long as the returned list isn't a copy of the real
		// data structure. unfortunately the superclass doesn't provide access
		// to the underlying list so maybe it's better to not use the superclass
		// at all.
		getFrameSections().remove(index);
	}
    
    /*private void showAxiomEditor(final OWLAxiom axiom) {
    	final AxiomExpressionEditor editor = new AxiomExpressionEditor(kit_);
        final JComponent editorComponent = editor.getEditorComponent();
        @SuppressWarnings("serial")
		final VerifyingOptionPane optionPane = new VerifyingOptionPane(editorComponent) {

            public void selectInitialValue() {
                // This is overriden so that the option pane dialog default
                // button doesn't get the focus.
            }
        };
        final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
            public void verifiedStatusChanged(boolean verified) {
                optionPane.setOKEnabled(verified);
            }
        };
        // Protege's syntax checkers only cover the class axiom's syntax
        editor.setEditedObject((OWLClassAxiom) axiom);
        // prevent the OK button from being available until the expression is syntactically valid
        editor.addStatusChangedListener(verificationListener);
        
        JDialog dlg = optionPane.createDialog(this, null);

        dlg.setModal(false);
        dlg.setResizable(true);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.addComponentListener(new ComponentAdapter() {

            public void componentHidden(ComponentEvent e) {
                Object retVal = optionPane.getValue();
                
                editorComponent.setPreferredSize(editorComponent.getSize());
                
                if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
                    handleEditFinished(axiom, editor.getEditedObject());
                }
                
                //setSelectedValue(frameObject, true);
                
                editor.removeStatusChangedListener(verificationListener);
                editor.dispose();
            }
        });

        dlg.setTitle("Class axiom expression editor");
        dlg.setVisible(true);
    }
    
	private void handleEditFinished(OWLAxiom oldAxiom, OWLAxiom newAxiom) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		OWLOntology ontology = kit_.getOWLModelManager().getActiveOntology();
		// remove the old axiom
		changes.add(new RemoveAxiom(ontology, oldAxiom));
		changes.add(new AddAxiom(ontology, newAxiom));

		kit_.getOWLModelManager().applyChanges(changes);
	}*/
    
}
