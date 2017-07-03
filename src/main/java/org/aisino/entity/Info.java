package org.aisino.entity;

public class Info {

	public static final String REGEX = "~~~";

	private String org_no;
	private String org_name;
	private String szcs;
	private String addr;
	private String tel;
	private String taxcode;

	private String user_human;
	private String user_human_pwd;
	private String user_new;
	private String user_new_pwd;
	private String user_robot;
	private String user_robot_pwd;

	private String org_id;
	
	private String payee;
	private String checker;
	
	

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public String getOrg_id() {
		return org_id;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

	public String getOrg_no() {
		return org_no;
	}

	public void setOrg_no(String org_no) {
		this.org_no = org_no;
	}

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

	public String getSzcs() {
		return szcs;
	}

	public void setSzcs(String szcs) {
		this.szcs = szcs;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getTaxcode() {
		return taxcode;
	}

	public void setTaxcode(String taxcode) {
		this.taxcode = taxcode;
	}

	public String getUser_human() {
		return user_human;
	}

	public void setUser_human(String user_human) {
		this.user_human = user_human;
	}

	public String getUser_human_pwd() {
		return user_human_pwd;
	}

	public void setUser_human_pwd(String user_human_pwd) {
		this.user_human_pwd = user_human_pwd;
	}

	public String getUser_new() {
		return user_new;
	}

	public void setUser_new(String user_new) {
		this.user_new = user_new;
	}

	public String getUser_new_pwd() {
		return user_new_pwd;
	}

	public void setUser_new_pwd(String user_new_pwd) {
		this.user_new_pwd = user_new_pwd;
	}

	public String getUser_robot() {
		return user_robot;
	}

	public void setUser_robot(String user_robot) {
		this.user_robot = user_robot;
	}

	public String getUser_robot_pwd() {
		return user_robot_pwd;
	}

	public void setUser_robot_pwd(String user_robot_pwd) {
		this.user_robot_pwd = user_robot_pwd;
	}

	@Override
	public String toString() {
		return "[" + org_no + "|" + org_name + "|" + szcs + "|" + addr + "|" + tel + "|" + taxcode + "]";
	}

}
