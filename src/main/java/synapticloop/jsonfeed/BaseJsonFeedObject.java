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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

public abstract class BaseJsonFeedObject {
	private static final String LOGGER_KEY_REMOVED_FROM_JSON_OBJECT = "Key '{}' removed from JSON Object";
	private static final String ERROR_KEY_WAS_NOT_MAPPED_WITH_VALUE = "Key '{}' was not mapped with value '{}'";

	protected static final String KEY_ATTACHMENTS = "attachments";
	protected static final String KEY_AUTHOR =  "author";
	protected static final String KEY_AVATAR = "avatar";
	protected static final String KEY_BANNER_IMAGE = "banner_image";
	protected static final String KEY_CONTENT_HTML = "content_html";
	protected static final String KEY_CONTENT_TEXT = "content_text";
	protected static final String KEY_DATE_MODIFIED = "date_modified";
	protected static final String KEY_DATE_PUBLISHED = "date_published";
	protected static final String KEY_DESCRIPTION =  "description";
	protected static final String KEY_DURATION_IN_SECONDS = "duration_in_seconds";
	protected static final String KEY_EXPIRED =  "expired";
	protected static final String KEY_EXTERNAL_URL = "external_url";
	protected static final String KEY_FAVICON =  "favicon";
	protected static final String KEY_FEED_URL =  "feed_url";
	protected static final String KEY_HOME_PAGE_URL =  "home_page_url";
	protected static final String KEY_HUBS =  "hubs";
	protected static final String KEY_ICON =  "icon";
	protected static final String KEY_ID = "id";
	protected static final String KEY_IMAGE = "image";
	protected static final String KEY_ITEMS =  "items";
	protected static final String KEY_MIME_TYPE = "mime_type";
	protected static final String KEY_NAME = "name";
	protected static final String KEY_NEXT_URL =  "next_url";
	protected static final String KEY_SIZE_IN_BYTES = "size_in_bytes";
	protected static final String KEY_SUMMARY = "summary";
	protected static final String KEY_TAGS = "tags";
	protected static final String KEY_TITLE =  "title";
	protected static final String KEY_TYPE = "type";
	protected static final String KEY_URL = "url";
	protected static final String KEY_USER_COMMENT =  "user_comment";
	protected static final String KEY_VERSION =  "version";

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);

	// the list of all of the validation errors found
	protected List<String> validationErrors = new ArrayList<String>();
	// Publishers can use custom objects in JSON Feeds. Names must start with an _ character and be followed by a letter. 
	// Custom objects can appear anywhere in a feed.
	protected Map<String, Extension> extensions = new LinkedHashMap<String, Extension>();

	protected abstract Logger getLogger();
	public abstract JSONObject toJSON();
	public abstract void validate() throws ValidationException;
	private int numUnmappedKeys = 0;

	/**
	 * Warn (i.e. log a warning message to the log output) if there were any remaining keys that were not collected by 
	 * the parsing of input JSON file.  The number of missing keys is kept as a variable {@link #numUnmappedKeys}
	 * 
	 * @param jsonObject The JSON object to inspect for missing keys.
	 */
	protected void warnOnMissingKeys(JSONObject jsonObject) {
		numUnmappedKeys = 0;
		Iterator<String> keys = jsonObject.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			getLogger().warn(ERROR_KEY_WAS_NOT_MAPPED_WITH_VALUE, key, jsonObject.get(key));
			numUnmappedKeys++;
		}
	}

	/**
	 * Read an array of objects into a typed array
	 * 
	 * @param jsonObject The jsonObject to read from
	 * @param key The key to look up
	 * @param objectClass The class of the object to instantiate
	 * 
	 * @return The list of objects as an array
	 */
	protected <T> List<T> readObjectArray(JSONObject jsonObject, String key, Class<T> objectClass) {
		List<T> list = new ArrayList<T>();
		if(jsonObject.has(key)) {
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			for (Object object : jsonArray) {
				try {
					T newInstance = objectClass.getConstructor(JSONObject.class).newInstance((JSONObject)object);
					list.add(newInstance);
				} catch (InstantiationException | 
						IllegalAccessException | 
						IllegalArgumentException | 
						InvocationTargetException | 
						NoSuchMethodException | 
						SecurityException e) {

					getLogger().error("Could not parse JSONObject {} with value '{}', message was: {}", objectClass.getName(), object, e.getMessage());
				}
			}
		}

		unmapKey(jsonObject, key);

		return(list);
	}

	protected List<String> readStringArray(JSONObject jsonObject, String key) {
		List<String> list = new ArrayList<String>(); 

		if(jsonObject.has(key)) {
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			for (Object object : jsonArray) {
				if(object instanceof String) {
					list.add((String)object);
					getLogger().trace("Key '{}', value '{}' added to string array", key, object);
				} else {
					getLogger().error("Could not parse array for key '{}', value '{}' was not a string", key, object);
				}
			}
		}

		unmapKey(jsonObject, key);

		return(list);
	}

	protected String readString(JSONObject jsonObject, String key) {
		String optString = jsonObject.optString(key, null);

		unmapKey(jsonObject, key);

		return (optString);
	}

	protected Date readDate(JSONObject jsonObject, String key) {
		String optString = jsonObject.optString(key, null);
		jsonObject.remove(key);
		if(null != optString) {
			try {
				return(SIMPLE_DATE_FORMAT.parse(optString));
			} catch (ParseException e) {
				getLogger().error("Could not parse '{}' to date using format '{}'", optString, DATE_FORMAT);
			}
		}

		unmapKey(jsonObject, key);

		return (null);
	}

	protected Long readLong(JSONObject jsonObject, String key) {
		if(jsonObject.has(key)) {
			Long longValue = jsonObject.getLong(key);
			unmapKey(jsonObject, key);
			return(longValue);
		} else {
			return(null);
		}
	}

	protected Boolean readBoolean(JSONObject jsonObject, String key) {
		if(jsonObject.has(key)) {
			boolean booleanValue = jsonObject.getBoolean(key);
			unmapKey(jsonObject, key);
			return(booleanValue);
		} else {
			return(null);
		}
	}

	protected JSONObject readObject(JSONObject jsonObject, String key) {
		JSONObject optJSONObject = jsonObject.optJSONObject(key);
		unmapKey(jsonObject, key);
		return(optJSONObject);
	}

	protected void addKeyValue(JSONObject jsonObject, String key, Object value) {
		if(null != key && null != value) {
			if(value instanceof BaseJsonFeedObject) {
				jsonObject.put(key, ((BaseJsonFeedObject)value).toJSON());
			} else {
				jsonObject.put(key, value);
			}
		}
	}

	public Map<String, Extension> getExtensions() { return extensions; }

	public Extension getExtension(String name) {
		if(null != extensions) {
			return(extensions.get(name));
		}
		return(null);
	}

	public void parseExtensions(JSONObject jsonObject) {
		List<String> toBeRemoved = new ArrayList<String>();
		// anything that hasn't been picked up and starts with an underscore, followed by an alpha character is by 
		// definition an extension
		Iterator<String> keys = jsonObject.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if(key.startsWith("_")) {
				extensions.put(key, new Extension(jsonObject.getJSONObject(key)));
			}
			toBeRemoved.add(key);
		}

		for (String key : toBeRemoved) {
			unmapKey(jsonObject, key);
		}
	}

	private void unmapKey(JSONObject jsonObject, String key) {
		jsonObject.remove(key);
		getLogger().trace(LOGGER_KEY_REMOVED_FROM_JSON_OBJECT, key);
	}

	public void setExtensions(Map<String, Extension> extensions) { this.extensions = extensions; }

	public void addExtension(String name, Extension extension) {
		if(null == extensions) {
			extensions = new LinkedHashMap<String, Extension>();
		}

		extensions.put(name, extension);
	}

	protected void addExtensions(JSONObject jsonObject) {
		Iterator<String> iterator = extensions.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			jsonObject.put(key, extensions.get(key).toJSON());
		}
	}

	protected void validateExtensions() throws ValidationException {
		boolean isInError = false;
		Iterator<String> iterator = extensions.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Extension extension = extensions.get(key);
			try {
				extension.validate();
			} catch (ValidationException e) {
				validationErrors.addAll(extension.getValidationErrors());
				isInError = true;
			}
		}

		if(isInError) {
			throw new ValidationException("Could not validate extensions");
		}
	}
	public List<String> getValidationErrors() { return(validationErrors); }

	public int getUnMappedKeys() { return numUnmappedKeys; }
	
	public boolean validateIsNotNull(Object object, String key) {
		if(null == object) {
			String validationError = String.format("[%s] Key '{}' _MUST_NOT_ be null", getLogger().getName(), key);
			validationErrors.add(validationError);
			return(true);
		}

		return(false);
	}
}
