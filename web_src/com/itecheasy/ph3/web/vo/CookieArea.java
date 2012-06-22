package com.itecheasy.ph3.web.vo;

import com.itecheasy.ph3.system.Country;
import com.itecheasy.ph3.system.Currency;

/**
 * 本类是把本应放cookie内空放session中去。
 * 
 */
public class CookieArea {
	private Country country;
	private Currency currency;
	private String city;
	private String zip;
	private String uuid;

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public CookieArea(Country country, Currency currency, String city, String zip) {
		super();
		this.country = country;
		this.currency = currency;
		this.city = city;
		this.zip = zip;
	}

	public CookieArea() {
		super();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
