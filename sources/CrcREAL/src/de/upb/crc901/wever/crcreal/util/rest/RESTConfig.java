package de.upb.wever.util.rest;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({ "file:config/RESTConfig.properties" })
public interface RESTConfig extends Config {

	public static final String PREFIX = "crc.real.rest.";

	public static final String BASE_URL = PREFIX + "baseURL";
	public static final String HEARTBEAT = PREFIX + "heartbeat";
	public static final String CHUNK_DONE = PREFIX + "chunkDone";
	public static final String NEW_CHUNK = PREFIX + "newChunk";
	public static final String CONFIRM_CHUNK = PREFIX + "confirmChunk";
	public static final String POST_RESULT = PREFIX + "postResult";
	public static final String CHUNK_STATE_UP = PREFIX + "chunkStateUpdate";

	@Key(BASE_URL)
	@DefaultValue("http://127.0.0.1/real/v1/")
	public String baseURL();

	@Key(HEARTBEAT)
	@DefaultValue("heartbeat/")
	public String heartbeat();

	@Key(NEW_CHUNK)
	@DefaultValue("experiment/chunk/")
	public String newChunk();

	@Key(CONFIRM_CHUNK)
	@DefaultValue("experiment/confirmChunk/")
	public String confirmChunk();

	@Key(CHUNK_DONE)
	@DefaultValue("experiment/chunkDone/")
	public String chunkDone();

	@Key(BASE_URL + POST_RESULT)
	@DefaultValue("experiment/postResult/")
	public String postResult();

	@Key(BASE_URL + CHUNK_STATE_UP)
	@DefaultValue("experiment/chunkStateUpdate/")
	public String chunkStateUpdate();

}
