package de.objectcode.play2.plugin.monitoring;

import java.util.concurrent.TimeUnit;

import play.Application;
import play.Configuration;
import play.Logger;
import play.Play;
import play.Plugin;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import de.objectcode.play2.plugin.monitoring.infoadapter.DbInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.GcInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.HeapMemoryInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.LoadAverageInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.NodeInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.SwapInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.ThreadInfoAdapter;

public class MonitoringPlugin extends Plugin {

	public static final String DEFAULT_CLASS_THREAD_INFO_ADAPTER = "de.objectcode.play2.plugin.monitoring.infoadapter.impl.DefaultThreadInfoAdapter";
	public static final String DEFAULT_CLASS_SWAP_INFO_ADAPTER = "de.objectcode.play2.plugin.monitoring.infoadapter.impl.linux.SwapProcFSInfoAdapter";
	public static final String DEFAULT_CLASS_NODE_INFO_ADAPTER = "de.objectcode.play2.plugin.monitoring.infoadapter.impl.DefaultNodeInfoAdapter";
	public static final String DEFAULT_CLASS_LOAD_AVERAGE_INFO_ADAPTER = "de.objectcode.play2.plugin.monitoring.infoadapter.impl.DefaultLoadAverageInfoAdapter";
	public static final String DEFAULT_CLASS_HEAP_MEMORY_INFO_ADAPTER = "de.objectcode.play2.plugin.monitoring.infoadapter.impl.DefaultHeapMemoryInfoAdapter";
	public static final String DEFAULT_CLASS_GC_INFO_ADAPTER = "de.objectcode.play2.plugin.monitoring.infoadapter.impl.DefaultGcInfoAdapter";
	public static final String DEFAULT_CLASS_DB_INFO_ADAPTER = "de.objectcode.play2.plugin.monitoring.infoadapter.impl.BoneCPInfoAdapter";
	public static final String DEFAULT_CLASS_AGGREGATOR_PERSISTER = "de.objectcode.play2.plugin.monitoring.EbeanAggregatorPersister";

	private Application application;
	public static boolean requestLogginEnabled;

	public MonitoringPlugin(final Application application) {
		this.application = application;
	}

	private <T> T newInstance(final String name, final Class<T> interfaceClass) {
		try {
			final Object o = Play.application().classloader().loadClass(name).newInstance();
			return interfaceClass.cast(o);
		} catch (final Exception e) {
			Logger.error("Could not instantiate infoAdapter class=" + name + " due to " + e, e);
			return null;
		}
	}

	private String getOrElse(final Configuration conf, final String key, final String defaultValue) {
		final String value = conf.getString(key);
		if (value != null) return value;
		return defaultValue;
	}

	private <T> T configureInfoAdapter(final Configuration conf, final String confToken, final String confDefault,
		final Class<T> interfaceClass) {
		final T instance = newInstance(getOrElse(conf, confToken, confDefault), interfaceClass);
		if (instance == null) {
			throw conf.reportError(confToken, "could not create instance", null);
		}
		return instance;
	}

	@Override
	public void onStart() {

		final Configuration conf = application.configuration().getConfig("monitoring");
		if (conf == null || conf.keys().isEmpty()) return;

		if (conf.getBoolean("enable_request_logging_db")) MonitoringPlugin.requestLogginEnabled = true;

		final Long aggregateJobMillis = conf.getMilliseconds("aggregateJobPeriodMillis");
		if (aggregateJobMillis != null) {
			Logger.info("about to register aggregate job with interval " + aggregateJobMillis + " milliseconds");
		} else {
			Logger.info("no aggregateJobPeriodMillis configured");
			return;
		}

		final ThreadInfoAdapter threadInfoAdapter = configureInfoAdapter(conf, "class.threadInfoAdapter",
				DEFAULT_CLASS_THREAD_INFO_ADAPTER, ThreadInfoAdapter.class);

		final SwapInfoAdapter swapInfoAdapter = configureInfoAdapter(conf, "class.swapInfoAdapter",
				DEFAULT_CLASS_SWAP_INFO_ADAPTER, SwapInfoAdapter.class);

		final NodeInfoAdapter nodeInfoAdapter = configureInfoAdapter(conf, "class.nodeInfoAdapter",
				DEFAULT_CLASS_NODE_INFO_ADAPTER, NodeInfoAdapter.class);

		final LoadAverageInfoAdapter loadAverageInfoAdapter = configureInfoAdapter(conf,
				"class.loadAverageInfoAdapter", DEFAULT_CLASS_LOAD_AVERAGE_INFO_ADAPTER, LoadAverageInfoAdapter.class);

		final HeapMemoryInfoAdapter heapMemoryInfoAdapter = configureInfoAdapter(conf, "class.heapMemoryInfoAdapter",
				DEFAULT_CLASS_HEAP_MEMORY_INFO_ADAPTER, HeapMemoryInfoAdapter.class);

		final GcInfoAdapter gcInfoAdapter = configureInfoAdapter(conf, "class.gcInfoAdapter",
				DEFAULT_CLASS_GC_INFO_ADAPTER, GcInfoAdapter.class);

		final DbInfoAdapter dbInfoAdapter = configureInfoAdapter(conf, "class.dbInfoAdapter",
				DEFAULT_CLASS_DB_INFO_ADAPTER, DbInfoAdapter.class);

		final AggregatorPersister aggregatorPersister = configureInfoAdapter(conf, "class.aggregatorPersister",
				DEFAULT_CLASS_AGGREGATOR_PERSISTER, AggregatorPersister.class);
		
		final LoadAverageAggregator loadAverageAggregator = new LoadAverageAggregator(loadAverageInfoAdapter);

		final Aggregator agg = new Aggregator(threadInfoAdapter, swapInfoAdapter, nodeInfoAdapter,
				loadAverageAggregator, heapMemoryInfoAdapter, gcInfoAdapter, dbInfoAdapter, aggregatorPersister);

		Aggregator.set(agg);

		Akka.system()
				.scheduler()
				.schedule(Duration.create(aggregateJobMillis, TimeUnit.MILLISECONDS),
						Duration.create(aggregateJobMillis, TimeUnit.MILLISECONDS), Aggregator.get(), Akka.system().dispatcher());

		Akka.system()
				.scheduler()
				.schedule(Duration.create(0, TimeUnit.MILLISECONDS), Duration.create(1, TimeUnit.MINUTES),
						loadAverageAggregator, Akka.system().dispatcher());
	}

}
