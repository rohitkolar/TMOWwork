package com.rohitkolar.tmo.model;

public class Tax {
	private int taxId;
	private String taxName;
	private double taxPercent;
	private String isActive;
	private String description;
	private double amount;
	
	public Tax(int taxId, String taxName, double taxPercent, String isActive, String description) {
		super();
		this.taxId = taxId;
		this.taxName = taxName;
		this.taxPercent = taxPercent;
		this.isActive = isActive;
		this.description = description;
	}
	
	public int getTaxId() {
		return taxId;
	}
	public void setTaxId(int taxId) {
		this.taxId = taxId;
	}
	public String getTaxName() {
		return taxName;
	}
	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}
	public double getTaxPercent() {
		return taxPercent;
	}
	public void setTaxPercent(double taxPercent) {
		this.taxPercent = taxPercent;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}
