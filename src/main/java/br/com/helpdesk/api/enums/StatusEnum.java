package br.com.helpdesk.api.enums;

public enum StatusEnum {

	New,
	Assigned,
	Resolved,
	Approvad,
	Disapprovad,
	Clesed;
	
	public static StatusEnum getStatus(String status) {
		switch (status) {
			case "New": return New;
			case "Resolved": return Resolved;
			case "Approvad": return Approvad;
			case "Disapprovad": return Disapprovad;
			case "Assigned": return Assigned;
			case "Clesed": return Clesed;
			default: return New;
		}
	}
}
