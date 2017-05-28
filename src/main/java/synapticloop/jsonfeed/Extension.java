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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Extension extends BaseJsonFeedObject {
	private static final Logger LOGGER = LoggerFactory.getLogger(Extension.class);

	private static final String VALIDATION_ERROR_KEY_MUST_NOT_START_WITH_AN_UNDERSCORE_CHARACTER = "[%s] key %s _MUST_ not start with an underscore character '_'";
	private static final String VALIDATION_ERROR_KEY_MUST_NOT_START_CONTAIN_AN_FULL_STOP_CHARACTER = "[%s] key %s _MUST_ not start contain an full-stop character '.'";
	private static final String VALIDATION_ERROR_KEYS_WERE_NOT_CORRECTLY_NAMED = "[%s] Keys were not correctly named";

	private Map<String, Object> keyValues = new LinkedHashMap<String, Object>();

	public Extension(Map<String, Object> keyValues) {
		this.keyValues = keyValues;
	}

	public Extension(JSONObject jsonObject) {
		Iterator<String> keys = jsonObject.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			keyValues.put(key, jsonObject.get(key));
		}
	}

	public void addKeyValue(String key, Object value) {
		keyValues.put(key, value);
	}

	@Override
	protected Logger getLogger() {
		return(LOGGER);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		Iterator<String> keys = keyValues.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			jsonObject.put(key, keyValues.get(key));
		}
		return(jsonObject);
	}

	@Override
	public void validate() throws ValidationException {
		boolean isInError = false;
		Iterator<String> iterator = keyValues.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if(key.startsWith("_")) {
				String validationError = String.format(VALIDATION_ERROR_KEY_MUST_NOT_START_WITH_AN_UNDERSCORE_CHARACTER, LOGGER.getName(), key);
				isInError = true;
				validationErrors.add(validationError);
			}

			if(key.contains(".")) {
				String validationError = String.format(VALIDATION_ERROR_KEY_MUST_NOT_START_CONTAIN_AN_FULL_STOP_CHARACTER, LOGGER.getName(), key);
				isInError = true;
				validationErrors.add(validationError);
			}
		}

		if(isInError) {
			throw new ValidationException(String.format(VALIDATION_ERROR_KEYS_WERE_NOT_CORRECTLY_NAMED, LOGGER.getName()));
		}
	}

	/**
	 * Return a string representation of the object - which is
	 * 
	 * @return The representation of this object as a JSON string
	 */
	@Override
	public String toString() {
		return(toJSON().toString());
	}
}
