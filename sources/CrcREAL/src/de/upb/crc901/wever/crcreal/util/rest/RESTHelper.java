package de.upb.wever.util.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.aeonbits.owner.ConfigCache;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RESTHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(RESTHelper.class);

	private final RESTConfig CONFIG = ConfigCache.getOrCreate(RESTConfig.class);
	private final HttpClient httpClient;
	private int sentRequests = 0;

	private final String keyToken = "AAAAB3NzaC1yc2EAAAABJQAAAgEAicszdT3TYn0rBRMoNB2L/Fo+rmH87nKaLXb8" + "e1UymhILuxg8xbC8I8UkWcESNKORTGYFL+xzUvbNijyhMitUbdYXToShhyK7vqZt"
			+ "Dx88L/kct5hyyoXjSVZByiqXmA32HAWdEZzvNWWAKBHxqIxiGSqc7CEnMV/Ulodf" + "JSgaxrtsI4DG5yVVPgD6JmJ5AqQG1cE4ytSosPnBRfKji3323DHS5XSgl9yD08hW"
			+ "PwHHdjOvmpR2luyNsfYdTFKi589bFLH0/dKxhhmwJEMOV93VKOxOsfu1FqvZOTdw" + "mTQ9sGICR5XM8Ua4uXy2LifY9pabN4K7svjBdw0QrBHKHfMaNWhd4e5jOMPi40Tb"
			+ "JapEDVCJZOhjexQ0mn9MG7eqX1zfXeQx3qHNm9rXsa3o6g2JYCjAupsuWx7tkRC9" + "PQogMzkUZ5BYv6JPKAJwDKLOl0lqYfXqu/RffExBBjIpAxf4zmdd2Dg1xQua2THJ"
			+ "uVCs0sebDcaAMPdzuU8oGuJTumlxJ9vDkDSVGOGMcH4JEBmTqU4RAwUSEwnMEJF8" + "zCtbBf0TdIMwmj8R6xxwJ5o8k9I45vjmgP352fGTsrM5bUc7cjWX2U25tePs3V8N"
			+ "97IUTmWMf5aae3/Xf6ciMNW3rF0TNEFiybWdIWxoC3mB1cFahp+QglIIRrVNTSLS" + "JRermPs=";

	private RequestDispatcher requestDispatcher;
	LinkedBlockingQueue<HttpUriRequest> requestBuffer;

	private class RequestDispatcher extends Thread {
		private boolean keepRunning = true;

		private final LinkedBlockingQueue<HttpUriRequest> requestsToDispatch;

		public RequestDispatcher(final LinkedBlockingQueue<HttpUriRequest> pRequestsToDispatch) {
			this.requestsToDispatch = pRequestsToDispatch;
		}

		public void shutdown() {
			LOGGER.debug("RequestDispatcher is going down as soon as the buffer is emtpy");
			this.keepRunning = false;
		}

		@Override
		public void run() {
			while (this.keepRunning || this.requestsToDispatch.size() > 0) {
				try {
					LOGGER.trace("Keep running={} | {} sent requests | {} requests pending", this.keepRunning, RESTHelper.this.sentRequests, this.requestsToDispatch.size());
					final HttpUriRequest request = this.requestsToDispatch.poll(5000, TimeUnit.MILLISECONDS);
					if (request != null) {
						RESTHelper.this.sentRequests++;
						final HttpResponse response = RESTHelper.this.httpClient.execute(request);
						final String responseString = EntityUtils.toString(response.getEntity());
						if (request instanceof HttpPost) {
							((HttpPost) request).releaseConnection();
						} else if (request instanceof HttpGet) {
							((HttpGet) request).releaseConnection();
						}

						if (!responseString.endsWith("true")) {
							LOGGER.error(responseString);
						}
					}
				} catch (final InterruptedException e) {
					LOGGER.error(e.getMessage());
				} catch (final ClientProtocolException e1) {
					LOGGER.error(e1.getMessage());
				} catch (final IOException e1) {
					LOGGER.error(e1.getMessage());
				}
			}
			LOGGER.debug("Request dispatcher shut down");
		}
	}

	public RESTHelper() {
		SSLContext sslContext = null;
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
		} catch (final NoSuchAlgorithmException e) {
			LOGGER.error(e.getMessage());
		} catch (final KeyManagementException e) {
			LOGGER.error(e.getMessage());
		}

		this.httpClient = HttpClientBuilder.create().setSSLContext(sslContext).build();

		this.requestBuffer = new LinkedBlockingQueue<>();
		this.requestDispatcher = new RequestDispatcher(this.requestBuffer);
		this.requestDispatcher.start();
	}

	public void sendJsonReport(final JsonObjectBuilder pReport) {
		try {
			final HttpPost request = new HttpPost(this.CONFIG.baseURL() + this.CONFIG.postResult());
			final StringEntity params = new StringEntity(pReport.add("keyToken", this.keyToken).build().toString());
			request.addHeader("content-type", "application/x-www-form-urlencoded");
			request.setEntity(params);
			this.requestBuffer.put(request);
		} catch (final UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
		} catch (final InterruptedException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public String sendHeartbeat() {
		String responseString = "";
		try {
			final HttpGet heartbeatRequest = new HttpGet(this.CONFIG.baseURL() + this.CONFIG.heartbeat());
			final HttpResponse response = this.httpClient.execute(heartbeatRequest);
			responseString = EntityUtils.toString(response.getEntity());
			if (!responseString.endsWith("true")) {
				LOGGER.error("Heartbeat failed");
			}
			heartbeatRequest.releaseConnection();
		} catch (final IOException e) {
			LOGGER.error(e.getMessage());
		}
		return responseString;
	}

	public String sendChunkDone(final int chunkID) {
		String responseString = "";
		try {
			final HttpGet chunkDoneRequest = new HttpGet(this.CONFIG.baseURL() + this.CONFIG.chunkDone() + "?id=" + chunkID);
			final HttpResponse response = this.httpClient.execute(chunkDoneRequest);
			responseString = EntityUtils.toString(response.getEntity());
			chunkDoneRequest.releaseConnection();
		} catch (final IOException e) {
			LOGGER.error(e.getMessage());
		}
		return responseString;
	}

	public String getNewChunk() {
		String responseString = "";
		try {
			final HttpPost chunkRequest = new HttpPost(this.CONFIG.baseURL() + this.CONFIG.newChunk());
			final HttpResponse chunkRequestResponse = this.httpClient.execute(chunkRequest);
			responseString = EntityUtils.toString(chunkRequestResponse.getEntity());
			chunkRequest.releaseConnection();
		} catch (final IOException e) {
			LOGGER.error(e.getMessage());
		}
		return responseString;
	}

	public String sendChunkConfirmation(final int chunkID) {
		String responseString = "";
		try {
			final HttpGet confirmationRequest = new HttpGet(this.CONFIG.baseURL() + this.CONFIG.confirmChunk() + "?id=" + chunkID);
			final HttpResponse confirmationResponse = this.httpClient.execute(confirmationRequest);
			responseString = EntityUtils.toString(confirmationResponse.getEntity());
			confirmationRequest.releaseConnection();
		} catch (final IOException e) {
			LOGGER.error(e.getMessage());
		}
		return responseString;
	}

	public void shutdownDispatcher() {
		this.requestDispatcher.shutdown();
		LOGGER.debug("Sent shutdown to dispatcher");
	}

	public void sendChunkState(final int chunkID, final String state) {
		final String asJsonString = Json.createObjectBuilder().add("chunkID", chunkID).add("state", state).build().toString();
		try {
			final HttpPost request = new HttpPost(this.CONFIG.baseURL() + this.CONFIG.chunkStateUpdate());
			final StringEntity params = new StringEntity(asJsonString);
			request.addHeader("content-type", "application/x-www-form-urlencoded");
			request.setEntity(params);
			this.requestBuffer.put(request);
		} catch (final UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
		} catch (final InterruptedException e) {
			LOGGER.error(e.getMessage());
		}
	}
}
