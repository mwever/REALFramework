import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import de.upb.wever.util.chunk.Chunk;

public class GetChunkTester {

	private static final String BASE_URL = "http://127.0.0.1/real/v1/";

	private final HttpClient httpClient = HttpClientBuilder.create().build();

	// @Test
	public void testGetChunk() {
		try {
			final HttpClient httpClient = HttpClientBuilder.create().build();
			final HttpPost postRequest = new HttpPost(BASE_URL + "experiment/chunk/");
			final HttpResponse response = httpClient.execute(postRequest);

			String responseString = EntityUtils.toString(response.getEntity());
			responseString = responseString.substring(1, responseString.length() - 1);

			final Chunk receivedChunk = Chunk.readFromString(responseString.replaceAll("#", "\n"));

			final HttpGet confirmationRequest = new HttpGet(BASE_URL + "experiment/confirmChunk/?id=" + receivedChunk.getChunkID());
			final HttpResponse confirmationResponse = httpClient.execute(confirmationRequest);
			final String confirmationResponseString = EntityUtils.toString(confirmationResponse.getEntity());

			if (confirmationResponseString.endsWith("true")) {
				System.out.println("Chunk received and confirmed");
			} else {
				System.out.println("Something went terribly wrong.");
				System.out.println(confirmationResponseString);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void heartBeatTest() throws ClientProtocolException, IOException {
		SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("SSL");
			// set up a TrustManager that trusts everything
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			} }, new SecureRandom());

			final HttpClient httpClient = HttpClientBuilder.create().setSSLContext(sslContext).build();
			for (int i = 0; i < 100; i++) {
				final HttpGet getRequest = new HttpGet("https://mastermint.de/webserver/v1/" + "heartbeat/");
				final HttpResponse response = httpClient.execute(getRequest);
				final String responseString = EntityUtils.toString(response.getEntity());
				System.out.println(responseString);
				assertEquals("true", responseString);
			}
		} catch (final KeyManagementException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (final NoSuchAlgorithmException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

}
