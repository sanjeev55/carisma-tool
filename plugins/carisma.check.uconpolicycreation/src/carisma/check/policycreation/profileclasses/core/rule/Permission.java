package carisma.check.policycreation.profileclasses.core.rule;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Element;

import carisma.check.policycreation.UMLModelConverter;
import carisma.check.policycreation.profileclasses.ODRLClass;
import carisma.check.policycreation.profileclasses.core.policy.Policy;

public class Permission extends Rule {
	List<Duty> duties =  new LinkedList<Duty>();

	public List<Duty> getDuties() {
		return duties;
	}

	public void setDuties(List<Duty> duties) {
		this.duties = duties;
	}
	
	@Override
	public void fill(EObject currentEObject, Element activityElement) {
		super.fill(currentEObject, activityElement);
		Object attributeValue = UMLModelConverter.getValue(currentEObject, odrlPackage.getPermission_Duties());
		if (attributeValue instanceof List list) { //TODO List attribute
			List<Duty> attributeValueOdrl = handler.addElement(list, this, activityElement, Duty.class);
			if (attributeValueOdrl!=null) {
				this.setDuties(attributeValueOdrl);
				//remove duties from policy as direct elements, only contained through this rule
				for (Duty duty : attributeValueOdrl) {
					List<Policy> referringPolicies = new LinkedList<>();
					for (ODRLClass referringObject: duty.gatReferredBy()) {
						if (referringObject instanceof Policy policy) {
							policy.getObligation().remove(duty);
							referringPolicies.add(policy);
						}
					}
					for (Policy policy : referringPolicies) {
						duty.removeReferredBy(policy);
					}
				}
			}
		}
	}
}
