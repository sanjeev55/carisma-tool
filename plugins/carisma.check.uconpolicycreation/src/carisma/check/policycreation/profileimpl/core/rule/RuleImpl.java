package carisma.check.policycreation.profileimpl.core.rule;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.InputPin;
import org.eclipse.uml2.uml.OutputPin;

import carisma.check.policycreation.UMLModelConverter;
import carisma.check.policycreation.profileimpl.common.relation.OutputImpl;
import carisma.check.policycreation.profileimpl.core.ODRLClassImpl;
import carisma.check.policycreation.profileimpl.core.action.ActionImpl;
import carisma.check.policycreation.profileimpl.core.asset.AssetImpl;
import carisma.check.policycreation.profileimpl.core.constraint.ConstraintInterfaceImpl;
import carisma.check.policycreation.profileimpl.core.function.FunctionImpl;
import carisma.check.policycreation.profileimpl.core.relation.RelationImpl;
import carisma.check.policycreation.profileimpl.core.relation.TargetImpl;

public abstract class RuleImpl extends ODRLClassImpl {
	String uid;
	List<FunctionImpl> involvedParties = new LinkedList<>();
	List<RelationImpl> involvedAssets = new LinkedList<>();
	ActionImpl action;
	ConstraintInterfaceImpl constraint;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public List<FunctionImpl> getInvolvedParties() {
		return involvedParties;
	}
	public void setInvolvedParties(List<FunctionImpl> involvedParties) {
		this.involvedParties = involvedParties;
	}
	public void addInvolvedParties(FunctionImpl involvedParty) {
		this.involvedParties.add(involvedParty);
	}
	
	public List<RelationImpl> getInvolvedAssets() {
		return involvedAssets;
	}
	public void setInvolvedAssets(List<RelationImpl> involvedAssets) {
		this.involvedAssets = involvedAssets;
	}
	public void addInvolvedAssets(RelationImpl involvedAsset) {
		this.involvedAssets.add(involvedAsset);
	}
	
	public ActionImpl getAction() {
		return action;
	}
	public void setAction(ActionImpl action) {
		this.action = action;
	}
	public ConstraintInterfaceImpl getConstraint() {
		return constraint;
	}
	public void setConstraint(ConstraintInterfaceImpl constraint) {
		this.constraint = constraint;
	}
	
	
	
	@Override
	public void fill(EObject currentEObject, Element activityElement) {
		super.fill(currentEObject, activityElement);
		Object attributeValue = UMLModelConverter.getValue(currentEObject, odrlPackage.getRule_Action());
		if (attributeValue instanceof EObject newEObj) {
			Object attributeValueOdrl = handler.addElement(newEObj, this, containingUmlElement);
			if (attributeValueOdrl instanceof ActionImpl action) {
				this.setAction(action);
			}
		}
		attributeValue = UMLModelConverter.getValue(currentEObject, odrlPackage.getRefinableElement_Refinement());
		if (attributeValue instanceof EObject newEObj) {//TODO get constraint
			Object attributeValueOdrl = handler.addElement(newEObj, this.getAction(), containingUmlElement);
			if (attributeValueOdrl instanceof ConstraintInterfaceImpl constraintInterface) {
				//if (attributeValueOdrl instanceof List constraintList) {TODO add seperate cases for logicalConstraint and List of constraints (in the 2nd case possibly also add instead of set)
				//	rule.getConstraint().
				//}
				if (this.getAction()!=null) {//TODO also add null check for other cases where a gotten object is further used or keep the nullpointer as sign that something is missing
					this.getAction().setRefinement(constraintInterface);
				}
			}
			
		}
		attributeValue = UMLModelConverter.getValue(currentEObject, odrlPackage.getRule_Uid());
		if (attributeValue instanceof String string) {
			this.setUid(string);
		}
		attributeValue = UMLModelConverter.getValue(currentEObject, odrlPackage.getRule_InvolvedAssets());
		if (attributeValue instanceof List list) { //TODO List attribute
			List<RelationImpl> attributeValueOdrl = handler.addElement(list, this, containingUmlElement, RelationImpl.class);
			if (attributeValueOdrl!=null) {
				this.setInvolvedAssets(attributeValueOdrl);
			}
		}
		attributeValue = UMLModelConverter.getValue(currentEObject, odrlPackage.getRule_InvolvedParties());
		if (attributeValue instanceof List list) { //TODO List attribute
			List<FunctionImpl> attributeValueOdrl = handler.addElement(list, this, containingUmlElement, FunctionImpl.class);
			if (attributeValueOdrl!=null) {
				this.setInvolvedParties(attributeValueOdrl);
			}
		}
		attributeValue = UMLModelConverter.getValue(currentEObject, odrlPackage.getConstrainableElement_Constraint());
		if (attributeValue instanceof EObject newEObj) {//TODO get constraint
			Object attributeValueOdrl = handler.addElement(newEObj, this, containingUmlElement);
			if (attributeValueOdrl instanceof ConstraintInterfaceImpl constraintInterface) {
				//if (attributeValueOdrl instanceof List constraintList) {TODO maybe add seperate cases for logicalConstraint and List of constraints (in the 2nd case possibly also add instead of set)
				//	rule.getConstraint().
				//}
				this.setConstraint(constraintInterface);
			}
		}
		//Activity diagram: Get related Assets from neighboring pins (TODO: clear up conflicts with explicitly listed Relations?)
		if (containingUmlElement instanceof org.eclipse.uml2.uml.Action action) {
			for (InputPin inPin : action.getInputs()) {
				for (EObject stereoAppl : inPin.getStereotypeApplications()) {
					if (handler.addElement(stereoAppl, this, containingUmlElement) instanceof AssetImpl asset) {
						RelationImpl newTarget = new TargetImpl();
						newTarget.setHandler(handler);//TODO watch out: not all classes are created in the Converter, remove if handler passing is changed to constructor
						newTarget.setAsset(asset);
						this.addInvolvedAssets(newTarget);
					}
				}
			}
			for (OutputPin outPin : action.getOutputs()) {
				for (EObject stereoAppl : outPin.getStereotypeApplications()) {
					if (handler.addElement(stereoAppl, this, containingUmlElement) instanceof AssetImpl asset) {
						RelationImpl newOutput = new OutputImpl();
						newOutput.setHandler(handler);//TODO watch out: not all classes are created in the Converter, remove if handler passing is changed to constructor
						newOutput.setAsset(asset);
						this.addInvolvedAssets(newOutput);
					}
				}
			}
		}
	}
	
	@Override
	public Object fillMapIndividual(Map<String,Object> map, Set<ODRLClassImpl> circlePreventionSet) throws NoSuchFieldException, SecurityException {
		for (FunctionImpl function : involvedParties) {
			Object functionMapObject = handler.createMap(function, circlePreventionSet);
			if (functionMapObject != null) {
				map.put(function.gatClassTerm(), functionMapObject);//TODO: check for duplicates (not here, in the fill()-method (or in the validity checks later))
			}
		}
		for (RelationImpl relation : involvedAssets) {
			Object relationMapObject = handler.createMap(relation, circlePreventionSet);
			if (relationMapObject != null) {
				map.put(relation.gatClassTerm(), relationMapObject);//TODO: check for duplicates (not here, in the fill()-method (or in the validity checks later))
			}
		}
		return null;
	}
}
