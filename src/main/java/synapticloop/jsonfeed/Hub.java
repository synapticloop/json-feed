package synapticloop.jsonfeed;

/*
 * Copyright (c) 2017 Synapticloop.
 * 
 * All rights reserved.
 * 
 * This code may contain contributions from other parties which, where 
 * applicable, will be listed in the default build file for the project 
 * ~and/or~ in a file named CONTRIBUTORS.txt in the root of the project.
 * 
 * This source code and any derived binaries are covered by the terms and 
 * conditions of the Licence agreement ("the Licence").  You may not use this 
 * source code or any derived binaries except in compliance with the Licence.  
 * A copy of the Licence is available in the file named LICENSE.txt shipped with 
 * this source code or binaries.
 */

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hub extends BaseJsonFeedObject {
	private static final Logger LOGGER = LoggerFactory.getLogger(Hub.class);

	private static final String VALIDATION_ERROR_BOTH_KEYS_MUST_NOT_BE_NULL_VALUES_WERE = "[%s] Both keys '%s' and '%s' _MUST_NOT_ be null, values were '%s' and '%s'";

	// The type field describes the protocol used to talk with the hub, such as "rssCloud" or "WebSub."
	private final String type;
	// the url field is where the subscription url exists
	private final String url;

	public Hub(String url, String type) {
		this.url = url;
		this.type = type;
	}

	public Hub(JSONObject jsonObject) {
		this.type = readString(jsonObject, KEY_TYPE);
		this.url = readString(jsonObject, KEY_URL);

		parseExtensions(jsonObject);
		warnOnMissingKeys(jsonObject);
	}

	public String getType() { return type; }

	public String getUrl() { return url; }

	@Override
	protected Logger getLogger() {
		return(LOGGER);
	}

	@Override
	public String toString() {
		return(toJSON().toString());
	}

	@Override
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		addKeyValue(jsonObject, KEY_TYPE, this.type);
		addKeyValue(jsonObject, KEY_URL, this.url);
		return(jsonObject);
	}

	@Override
	public void validate() throws ValidationException {
		boolean isInError = false;
		isInError = validateRequiredInError(url, KEY_URL) || isInError;
		isInError = validateRequiredInError(type, KEY_TYPE) || isInError;


		if(isInError) {
			throw new ValidationException("Could not validate hub.");
		}
	}

}
