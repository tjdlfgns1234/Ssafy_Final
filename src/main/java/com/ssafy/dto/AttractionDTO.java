package com.ssafy.dto;

public class AttractionDTO {
	private int no;
	private int content_id;
	private String title;
	private int content_type_id;
	private int area_code;
	private int si_gun_gu_code;
	private String firstimg;
	private String secondimg;

	private int maplv;
	private double lati;
	private double longi;
	private String addr1;
	private String homepage;
	private String overview;
	
	
	
	
	public String getAddr1() {
		return addr1;
	}
	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}
	
	public AttractionDTO(int no, int content_id, String title, int content_type_id, int area_code, int si_gun_gu_code,
			String firstimg, String secondimg, int maplv, double lati, double longi, String addr1, String homepage,
			String overview) {
		super();
		this.no = no;
		this.content_id = content_id;
		this.title = title;
		this.content_type_id = content_type_id;
		this.area_code = area_code;
		this.si_gun_gu_code = si_gun_gu_code;
		this.firstimg = firstimg;
		this.secondimg = secondimg;
		this.maplv = maplv;
		this.lati = lati;
		this.longi = longi;
		this.addr1 = addr1;
		this.homepage = homepage;
		this.overview = overview;
	}
	public AttractionDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public int getContentId() {
		return content_id;
	}
	public void setContentId(int content_id) {
		this.content_id = content_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getContentTypeId() {
		return content_type_id;
	}
	public void setContentTypeId(int content_type_id) {
		this.content_type_id = content_type_id;
	}
	public int getAreaCode() {
		return area_code;
	}
	public void setAreaCode(int area_code) {
		this.area_code = area_code;
	}
	public int getGiGunGuCode() {
		return si_gun_gu_code;
	}
	public void setGiGunGuCode(int si_gun_gu_code) {
		this.si_gun_gu_code = si_gun_gu_code;
	}
	public String getOverView() {
		return overview;
	}
	public void setOverView(String overview) {
		this.overview = overview;
	}


	//
	public String getFirstimg() {
		return firstimg;
	}
	public void setFirstimg(String firstimg) {
		this.firstimg = firstimg;
	}
	public String getSecondimg() {
		return secondimg;
	}
	public void setSecondimg(String secondimg) {
		this.secondimg = secondimg;
	}
	public int getMaplv() {
		return maplv;
	}
	public void setMaplv(int maplv) {
		this.maplv = maplv;
	}
	public double getLati() {
		return lati;
	}
	public void setLati(double lati) {
		this.lati = lati;
	}
	public double getlongi() {
		return longi;
	}
	public void setlongi(double longi) {
		this.longi = longi;
	}
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	
	
	
	@Override
	public String toString() {
	    return "장소 :  " + title + ",  주소 = "+ addr1 + " lati : " + lati;
	}
	
}
