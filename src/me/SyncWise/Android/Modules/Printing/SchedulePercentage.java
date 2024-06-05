package me.SyncWise.Android.Modules.Printing;

import java.io.Serializable;

public class SchedulePercentage implements Serializable{
    private static final long serialVersionUID = 1L;
    private String type;
    private Integer total;
    private String percentage;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public String getPercentage() {
		return percentage;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
     

}
