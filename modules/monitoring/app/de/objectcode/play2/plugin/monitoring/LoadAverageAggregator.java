package de.objectcode.play2.plugin.monitoring;

import java.util.ArrayList;
import java.util.List;

import play.Logger;
import de.objectcode.play2.plugin.monitoring.infoadapter.LoadAverageInfoAdapter;

public class LoadAverageAggregator implements Runnable {

	private LoadAverageInfoAdapter loadAverageInfoAdapter;
	private List<Double> avgList;

	public LoadAverageAggregator(LoadAverageInfoAdapter loadAverageInfoAdapter) {
		this.loadAverageInfoAdapter = loadAverageInfoAdapter;
		avgList = new ArrayList<Double>();
	}
	
	public synchronized Double getAndResetAverage() {
		if (avgList == null || avgList.size() == 0) {
			return loadAverageInfoAdapter.getAvgOneMinute();
		}

		double sum = 0;
		for (Double avg : avgList) {
			sum += avg;
		}
		sum = sum / avgList.size();

		Logger.debug("load average is " + sum + " for interval =" + avgList.size() + " minutes");
		avgList = new ArrayList<Double>();
		return sum;
	}

	@Override
	public synchronized void run() {
		final double avgOneMinute = loadAverageInfoAdapter.getAvgOneMinute();
		Logger.debug("About to add average= " + avgOneMinute);
		avgList.add(avgOneMinute);
	}

}
