package com.morganstanley.data;

public class Weather {
	private String temperature;
	private String wind;
	public Weather(String temperature, String wind) {
		super();
		this.temperature = temperature;
		this.wind = wind;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getWind() {
		return wind;
	}
	public void setWind(String wind) {
		this.wind = wind;
	}
	

}
