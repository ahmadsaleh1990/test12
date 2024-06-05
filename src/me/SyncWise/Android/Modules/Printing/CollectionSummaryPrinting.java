package me.SyncWise.Android.Modules.Printing;

import java.io.Serializable;
import java.sql.Date;

public class CollectionSummaryPrinting implements Serializable{
    private static final long serialVersionUID = 1L;
    private String clientCode;
    private String currencyCode;
    private String collectionCode;
    private Double totalAmount;
    private int collectionDetailType;
    private String checkCode;
    private String bankName;
    private Date collectionDate;
    private Double collectionAmount;
    private String companyCode;
    private String divisionCode;
    private int collectionType;
    public int getCollectionType() {
		return collectionType;
	}
	public void setCollectionType(int collectionType) {
		this.collectionType = collectionType;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getDivisionCode() {
		return divisionCode;
	}
	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}
	public Double getCollectionAmount() {
		return collectionAmount;
	}
	public void setCollectionAmount(Double collectionAmount) {
		this.collectionAmount = collectionAmount;
	}
	public String getCheckCode() {
		return checkCode;
	}
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public Date getCollectionDate() {
		return collectionDate;
	}
	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}
	public String getClientCode() {
		return clientCode;
	}
	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getCollectionCode() {
		return collectionCode;
	}
	public void setCollectionCode(String collectionCode) {
		this.collectionCode = collectionCode;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public int getCollectionDetailType() {
		return collectionDetailType;
	}
	public void setCollectionDetailType(int collectionDetailType) {
		this.collectionDetailType = collectionDetailType;
	}
}
