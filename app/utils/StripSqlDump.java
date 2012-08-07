package utils;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class StripSqlDump {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		List<String> lines = FileUtils.readLines(new File("/tmp/dump.sql"));
		for (Iterator i = lines.iterator(); i.hasNext();) {
			String l = (String) i.next();
			if (l.startsWith("INSERT INTO log_httprequest") ||
				    l.startsWith("INSERT INTO play_evolutions") ||
				    l.startsWith("INSERT INTO monitor_fine") ||
				    l.startsWith("INSERT INTO logging_") ||
			    false
			    ) {
				i.remove();
				continue;
			}
			System.out.println(l);
		}
	}

}
