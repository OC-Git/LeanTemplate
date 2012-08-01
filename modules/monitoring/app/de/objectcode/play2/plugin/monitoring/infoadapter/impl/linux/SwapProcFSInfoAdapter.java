package de.objectcode.play2.plugin.monitoring.infoadapter.impl.linux;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.objectcode.play2.plugin.monitoring.infoadapter.SwapInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.impl.AbstractFileLoaderAdapter;


public class SwapProcFSInfoAdapter extends AbstractFileLoaderAdapter implements SwapInfoAdapter {

	private static final String PROC_FILE_SWAPS = "/proc/meminfo";
	
	private long max;
	private long free;
	
	@Override
	public long getMaxSwapBytes() {
		return max;
	}

	@Override
	public long getUsedSwapBytes() {
		return max - free;
	}

	@Override
	protected String getAbsoluteFilePath() {
		return PROC_FILE_SWAPS;
	}

	@Override
	protected void parse() {
		final Pattern p = Pattern.compile("Swap(Total|Free):\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
		final Matcher m = p.matcher(getFileContent());
		
		while (m.find()) {
			long value = Long.parseLong(m.group(2)) * 1024; // values are kB here 
			if (m.group(1).equals("Total"))	max = value; else free = value;
		}
	}
}
