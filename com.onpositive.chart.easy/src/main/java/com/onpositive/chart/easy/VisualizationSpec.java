package com.onpositive.chart.easy;


public class VisualizationSpec {

	
	enum ChartType{
		BAR,PIE,TABLE
	}

	public ChartType type;
	public ChartData chart;
	public String xName;
	public String yName;
}
