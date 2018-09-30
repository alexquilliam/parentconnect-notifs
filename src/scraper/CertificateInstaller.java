package scraper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class CertificateInstaller {
	private final char SEPERATOR = File.separatorChar;

	public void install(String host, int port) throws Exception {
		char[] passphrase = "changeit".toCharArray();

		File jssecacerts = new File("jssecacerts");
		if (jssecacerts.isFile() == false) {
			File dir = new File(System.getProperty("java.home") + SEPERATOR + "lib" + SEPERATOR + "security");
			jssecacerts = new File(dir, "jssecacerts");
			if (jssecacerts.isFile() == false) {
				jssecacerts = new File(dir, "cacerts");
			}
		}

		InputStream in = new FileInputStream(jssecacerts);
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(in, passphrase);
		in.close();

		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(keyStore);
		X509TrustManager defaultTrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory factory = context.getSocketFactory();

		SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
		socket.setSoTimeout(10000);
		try {
			socket.startHandshake();
			socket.close();
		} catch (SSLException e) {
			System.out.println();
			e.printStackTrace(System.out);
		}

		X509Certificate[] chain = tm.chain;
		if (chain == null) {
			return;
		}

		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = chain[i];
			sha1.update(cert.getEncoded());
			md5.update(cert.getEncoded());
		}

		X509Certificate cert = chain[0];
		String alias = host + "-1";
		keyStore.setCertificateEntry(alias, cert);

		OutputStream out = new FileOutputStream("jssecacerts");
		keyStore.store(out, passphrase);
		out.close();

		extractCert(host, new String(passphrase));

		InputStream oldStream = System.in;
		try {
			System.setIn(new ByteArrayInputStream("y".getBytes("UTF-8")));

			importCert(host, new String(passphrase));
		} finally {
			System.setIn(oldStream);
		}
	}

	@SuppressWarnings("unused")
	private void removeCert(String alias) {
		String command =
				"-delete " +
				"-alias " + alias + " " +
				"-keystore \"" + System.getProperty("java.home") + SEPERATOR + "lib" + SEPERATOR + "security" + SEPERATOR + "cacerts\"";

		executeKeytool(command);
	}

	private void extractCert(String host, String passphrase) {
		String command =
				"-exportcert " +
				"-alias " + host + "-1 " +
				"-keystore " + "jssecacerts " +
				"-storepass " + passphrase + " " +
				"-file " + host + ".cer";

		executeKeytool(command);
	}

	private void importCert(String host, String passphrase) {
		String command =
				"-importcert " +
				"-alias " + host + " " +
				"-keystore \"" + System.getProperty("java.home") + SEPERATOR + "lib" + SEPERATOR + "security" + SEPERATOR + "cacerts\" " +
				"-storepass " + passphrase + " " +
				"-file " + host + ".cer";

		executeKeytool(command);
	}

	@SuppressWarnings("restriction")
	private void executeKeytool(String command) {
		try {
			ArrayList<String> commands = new ArrayList<String>();

			Matcher matcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
			while(matcher.find()) {
				commands.add(matcher.group(1).replace("\"", ""));
			}

			sun.security.tools.keytool.Main.main(commands.toArray(new String[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class SavingTrustManager implements X509TrustManager {
		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
			this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			this.chain = chain;
			tm.checkServerTrusted(chain, authType);
		}
	}
}
