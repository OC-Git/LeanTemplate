package de.objectcode.play2.plugin.monitoring.infoadapter.impl;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import play.Logger;
import de.objectcode.play2.plugin.monitoring.infoadapter.NodeInfoAdapter;

/**
 * 
 * DefaultNodeIdentifier.
 * <p/>
 * 
 * by default tries to generate a unique Node-Id utilizing the hosts IP and
 * hostName.
 * 
 * @author <a href="mailto:lochen@objectcode.de">lochen</a>
 * 
 */
public class DefaultNodeInfoAdapter implements NodeInfoAdapter {

	private static final String DELIMITER = "_";

	@Override
	public String getNodeId() {
		try {
			final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			final StringBuilder b = new StringBuilder();

			while (networkInterfaces.hasMoreElements()) {
				final NetworkInterface iff = networkInterfaces.nextElement();
				if (iff.isVirtual() || iff.isLoopback() || !iff.isUp()) continue;
				
				for (InterfaceAddress a : iff.getInterfaceAddresses()) {
					final String ipv4 = getIp(a.getAddress().getAddress());
					if (ipv4 == null) continue;
					if (b.length() != 0) b.append(DELIMITER);
					b.append(ipv4);
				}
			}
			return b.toString();
		} catch (final SocketException e) {
			Logger.error("Could not determine network interfaces due to " + e, e);
		}
		return null;
	}

	private String getIp(final byte[] address) {
		if (address == null || address.length != 4) return null;
		final StringBuilder bf = new StringBuilder(16);
		for (int i = 0; i < 4; i++) {
			if (i > 0) bf.append(".");
			bf.append(address[i] & 0xFF);
		}
		return bf.toString();
	}
}
