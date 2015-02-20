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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.border.Border;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListDeleteButton;
import org.protege.editor.core.ui.list.MListEditButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.framelist.OWLFrameListInferredSectionRowBorder;
import org.semanticweb.elk.explanations.WorkbenchManager;
import org.semanticweb.elk.explanations.editing.AxiomExpressionEditor;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.CycleFreeProofRoot;

/**
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
@SuppressWarnings("serial")
public class ProofFrameList extends OWLFrameList<CycleFreeProofRoot> {
	
	private final OWLEditorKit kit_;
	
	private static final Border INFERRED_BORDER = new OWLFrameListInferredSectionRowBorder();
	
	private static final Set<AxiomType<?>> EDITABLE_AXIOM_TYPES = new HashSet<AxiomType<?>>(Arrays.<AxiomType<?>>asList(AxiomType.SUBCLASS_OF, AxiomType.OBJECT_PROPERTY_DOMAIN, AxiomType.OBJECT_PROPERTY_RANGE, AxiomType.EQUIVALENT_CLASSES));
	
    public ProofFrameList(OWLEditorKit editorKit, WorkbenchManager workbenchManager, ProofFrame proofFrame) {
        super(editorKit, proofFrame);
        
        kit_ = editorKit;
        setCellRenderer(new ProofFrameListRenderer(editorKit));
    }
    
    @Override
	public ProofFrame getFrame() {
		return (ProofFrame) super.getFrame();
	}

	@Override
    protected List<MListButton> getButtons(final Object value) {
        if (value instanceof ProofFrameSectionRow) {
        	ProofFrameSectionRow row = (ProofFrameSectionRow) value;
        	
			if (row.isInferred()) {
				if (!row.isExpanded()) {
					// explain button for inferred expressions
					return Arrays.<MListButton> asList(new ExpandButton(
								new AbstractAction() {

									public void actionPerformed(ActionEvent e) {
										explainRow((ProofFrameSectionRow) value);
									}
								}));
				}
				else {
					return Arrays.<MListButton> asList(new CollapseButton(
							new AbstractAction() {

								public void actionPerformed(ActionEvent e) {
									collapseRow((ProofFrameSectionRow) value);
								}
							}));
				}
			}
			else if (row.getAxiom().isOfType(EDITABLE_AXIOM_TYPES)) {
				// asserted axiom, can edit or delete it
				return Arrays.<MListButton> asList(
						new MListEditButton(new AbstractAction() {

							public void actionPerformed(ActionEvent e) {
								editRow((ProofFrameSectionRow) value);
							}
						}),
						new MListDeleteButton(
								new AbstractAction() {

									public void actionPerformed(ActionEvent e) {
										deleteRow((ProofFrameSectionRow) value);
									}
								})
						);
			}
			else {
				// can only remove
				return Arrays.<MListButton> asList(
						new MListDeleteButton(
								new AbstractAction() {

									public void actionPerformed(ActionEvent e) {
										deleteRow((ProofFrameSectionRow) value);
									}
								})
						);
			}
        }

        return Collections.emptyList();
    }
    
	protected void editRow(ProofFrameSectionRow expressionRow) {
		showAxiomEditor(expressionRow);
    }
    
    protected void deleteRow(ProofFrameSectionRow expressionRow) {
    	handleDelete();
    	blockInferencesForPremise(expressionRow.getRoot());
    }
    
    private void blockInferencesForPremise(OWLExpression premise) {
    	getFrame().blockInferencesForPremise(premise);
    	refreshComponent();
    }
    
    protected void collapseRow(ProofFrameSectionRow expressionRow) {
    	expressionRow.setExpanded(false);
    	refreshComponent();
    }

	protected void explainRow(ProofFrameSectionRow expressionRow) {
		expressionRow.setExpanded(true);
		
		if (!expressionRow.isFilled()) {
			expressionRow.refillInferenceSections();
		}
		
		refreshComponent();
	}

	@Override
	protected Border createPaddingBorder(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE);
    }
	
	@Override
    protected Border createListItemBorder(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		// creating indents to reflect the proof's structure
		if (value instanceof ProofFrameSectionRow) {
			ProofFrameSectionRow row = (ProofFrameSectionRow) value;
			
			Border internalPadding = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			Border line = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220));
			Border externalBorder = BorderFactory.createMatteBorder(0, 20 * row.getDepth(), 0, 0, list.getBackground());
			Border border = BorderFactory.createCompoundBorder(externalBorder, BorderFactory.createCompoundBorder(row.isInferred() ? INFERRED_BORDER : line, internalPadding));
			
			return border;
		}
		
		return super.createListItemBorder(list, value, index, isSelected, cellHasFocus);
    }

	
	@Override
	public ListModel<?> getModel() {
		return new FlattenedListModel((ProofFrameSection) this.getFrame().getFrameSections().get(0));
	}

	@Override
	protected Color getItemBackgroundColor(MListItem item) {
        if (item instanceof ProofFrameSectionRow) {
            if (((ProofFrameSectionRow) item).isInferred()) {
                return INFERRED_BG_COLOR;
            }
        }
        
        return super.getItemBackgroundColor(item);
    }
	
	// TODO cache the list of items for better performance, e.g., constant time lookup by index
	private static class FlattenedListModel extends AbstractListModel<Object> {

		private final ProofFrameSection header_;
		
		FlattenedListModel(ProofFrameSection header) {
			header_ = header;
		}
		
		@Override
		public Object getElementAt(int arg0) {
			int i = 0;
			Iterator<?> iter = iterate();
			
			while (iter.hasNext()) {
				Object item = iter.next();
				
				if (i == arg0) {
					return item;
				}
				
				i++;
			}
			
			return null;
		}

		@Override
		public int getSize() {
			int i = 0;
			Iterator<?> iter = iterate();
			
			while (iter.hasNext()) {
				iter.next();
				i++;
			}
			
			return i;
		}
		
		private Iterator<?> iterate() {
			final LinkedList<Object> todo = new LinkedList<Object>();
			
			todo.add(header_);
			
			return new Iterator<Object>() {
				
				private Object next_ = null;

				@Override
				public boolean hasNext() {
					if (next_ != null) {
						return true;
					}

					next_ = todo.poll();
					
					if (next_ == null) {
						return false;
					}
					
					if (next_ instanceof ProofFrameSection) {
						ProofFrameSection section = (ProofFrameSection) next_;
						int i = 0;
						
						for (Object row : section.getRows()) {
							todo.add(i++, row);
						}
					}
					else if (next_ instanceof ProofFrameSectionRow) {
						ProofFrameSectionRow row = (ProofFrameSectionRow) next_;
						int i = 0;
						
						if (row.isExpanded()) {
							for (Object section : row.getInferenceSections()) {
								todo.add(i++, section);
							}
						}
					}
					
					return true;
				}

				@Override
				public Object next() {
					Object tmp = next_;
					
					next_ = null;
					
					return tmp;
				}

				@Override
				public void remove() {
					// no-op
				}
				
			};
		}
		
	}
	
    private void showAxiomEditor(final ProofFrameSectionRow expressionRow) {
    	final OWLAxiom axiom = expressionRow.getAxiom();
    	
    	if (axiom == null) {
    		return;
    	}
    	
    	final AxiomExpressionEditor editor = new AxiomExpressionEditor(kit_);
        final JComponent editorComponent = editor.getEditorComponent();
        
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
                    handleEditFinished(editor.getEditedObject(), expressionRow);
                }
                
                //setSelectedValue(frameObject, true);
                
                editor.removeStatusChangedListener(verificationListener);
                editor.dispose();
            }
        });

        dlg.setTitle("Class axiom editor");
        dlg.setVisible(true);
    }
    
	private void handleEditFinished(OWLAxiom newAxiom, ProofFrameSectionRow exprRow) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		OWLOntology ontology = kit_.getOWLModelManager().getActiveOntology();
		// remove the old axiom
		changes.add(new RemoveAxiom(ontology, exprRow.getAxiom()));
		changes.add(new AddAxiom(ontology, newAxiom));

		kit_.getOWLModelManager().applyChanges(changes);
		// only show proofs which do not use the old axiom
		blockInferencesForPremise(exprRow.getRoot());
	}

}
