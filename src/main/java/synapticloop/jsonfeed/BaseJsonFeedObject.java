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
/**
 * This is the base class for all of the JSON feed objects.  This allows the 
 * addition of extensions at any level.
 */
public abstract class BaseJsonFeedObject {
	private static final String LOGGER_COULD_NOT_PARSE_ARRAY_FOR_KEY_VALUE_WAS_NOT_A_STRING = "Could not parse array for key '{}', value '{}' was not a string";
	private static final String LOGGER_KEY_VALUE_ADDED_TO_STRING_ARRAY = "Key '{}', value '{}' added to string array";
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
	 * Read a boolean value from the JSON Object with the specified key.  This 
	 * key will lookup the boolean value - if it doesn't exist it will return null.
	 * The key (if it exists) will be removed from the JSON Object.
	 * 
	 * @param jsonObject The JSON object to use as the look up
	 * @param key The key to look up on the JSON Object
	 * 
	 * @return The Boolean value or null if it doesn't exist
	 */
	protected Boolean readBoolean(JSONObject jsonObject, String key) {
		if(jsonObject.has(key)) {
			boolean booleanValue = jsonObject.getBoolean(key);
			unmapKey(jsonObject, key);
			return(booleanValue);
		} else {
			return(null);
		}
	}

	
	/**
	 * Read a date from the JSON Object with the specified key.  This will parse
	 * the String into a date format {@link #SIMPLE_DATE_FORMAT} or return null.
	 * The date format is {@link #DATE_FORMAT}. The key (if it exists) will be 
	 * removed from the JSON Object.
	 * 
	 * @param jsonObject The JSON Object to use as the look up
	 * @param key The key to look up on the JSON Object
	 * 
	 * @return The parsed Date, or null if it doesn't exist, or could not be
	 *   parsed 
	 */
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

	/**
	 * Read a Long from the JSON Object with the specified key.  This will lookup
	 * the long value and if it does not exist will return null.  The key (if it
	 * exists) will be removed from the JSON Object.
	 * 
	 * @param jsonObject The JSON object to use as the look up
	 * @param key The key to look up on the JSON Object
	 * 
	 * @return The Long value, or null if it doesn't exist
	 */
	protected Long readLong(JSONObject jsonObject, String key) {
		if(jsonObject.has(key)) {
			Long longValue = jsonObject.getLong(key);
			unmapKey(jsonObject, key);
			return(longValue);
		} else {
			return(null);
		}
	}

	/**
	 * Read a JSON Object value from the JSON Object with the specified key and 
	 * return it (or null if it doesn't exist).  The key (if it exists) will be 
	 * removed from the JSON Object
	 * 
	 * @param jsonObject The JSON object to use as the look up
	 * @param key The key to look up on the JSON Object
	 * 
	 * @return The JSONObject value, or null if it doesn't exist
	 */
	protected JSONObject readObject(JSONObject jsonObject, String key) {
		JSONObject optJSONObject = jsonObject.optJSONObject(key);
		unmapKey(jsonObject, key);
		return(optJSONObject);
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

	/**
	 * Read a string from the JSON Object with the specified key.  This will 
	 * return the String value or null if it doesn't exist.  The key (if it
	 * exists) will be removed from the JSON Object.
	 * 
	 * @param jsonObject The JSON object to use as the look up
	 * @param key The key to look up on the JSON object
	 * 
	 * @return The looked up value, or null if it doesn't exist
	 */
	protected String readString(JSONObject jsonObject, String key) {
		String optString = jsonObject.optString(key, null);

		unmapKey(jsonObject, key);

		return (optString);
	}

	/**
	 * Read a JSON array of strings into a List<String>.  This will remove the 
	 * JSONArray once parsed.
	 *  
	 * @param jsonObject The JSON Object to get the JSON array from
	 * @param key The key to look up on the JSON Object
	 * 
	 * @return A list of Strings (which may be null)
	 */
	protected List<String> readStringArray(JSONObject jsonObject, String key) {
		List<String> list = new ArrayList<String>(); 

		if(jsonObject.has(key)) {
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			for (Object object : jsonArray) {
				if(object instanceof String) {
					list.add((String)object);
					getLogger().trace(LOGGER_KEY_VALUE_ADDED_TO_STRING_ARRAY, key, object);
				} else {
					getLogger().error(LOGGER_COULD_NOT_PARSE_ARRAY_FOR_KEY_VALUE_WAS_NOT_A_STRING, key, object);
				}
			}
		}

		unmapKey(jsonObject, key);

		return(list);
	}

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
	 * Add a value to the JSON Object using the specified key.  If the object is
	 * a BaseJsonFeedObject - then it will add .toJSON() representation to it, 
	 * else it will add the primitive value.  This will be added provided the 
	 * key and value is not null.
	 * 
	 * @param jsonObject The JSON Object to add the value to
	 * @param key The key for the JSON Object
	 * @param value The value for the key
	 */
	protected void addKeyValue(JSONObject jsonObject, String key, Object value) {
		if(null != key && null != value) {
			if(value instanceof BaseJsonFeedObject) {
				jsonObject.put(key, ((BaseJsonFeedObject)value).toJSON());
			} else {
				jsonObject.put(key, value);
			}
		}
	}

	/**
	 * Parse extensions from the JSON feed.  Extensions are un-mapped JSON keys
	 * that always start with an '_' underscore character.
	 * 
	 * @param jsonObject The JSON Object to parse for extensions
	 */
	protected void parseExtensions(JSONObject jsonObject) {
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

	/**
	 * Un-map (i.e. remove) a key from the JSON Object
	 * 
	 * @param jsonObject The JSON Object to remove the key from
	 * @param key The key to remove
	 */
	private void unmapKey(JSONObject jsonObject, String key) {
		jsonObject.remove(key);
		getLogger().trace(LOGGER_KEY_REMOVED_FROM_JSON_OBJECT, key);
	}

	/**
	 * Get all of the extensions that are available on this JSON Feed Object
	 * @return
	 */
	public Map<String, Extension> getExtensions() { return extensions; }

	/**
	 * Get an extension by name
	 * 
	 * @param name The name of the extension (which _MUST_ start with an 
	 *     underscore character - '_')
	 *     
	 * @return The extension object, or null if it doesn't exist
	 */
	public Extension getExtension(String name) {
		if(null != extensions) {
			return(extensions.get(name));
		}
		return(null);
	}

	/**
	 * Set the extensions that are going to be added to this object
	 * 
	 * @param extensions The map of extensions to add to this object
	 */
	public void setExtensions(Map<String, Extension> extensions) { this.extensions = extensions; }

	public void addExtension(String name, Extension extension) {
		if(null == extensions) {
			extensions = new LinkedHashMap<String, Extension>();
		}

		extensions.put(name, extension);
	}

	/**
	 * Add extensions to a JSON Object - this just iterates through the keyset of
	 * the extensions map and adds them to the passed in JSON Object.
	 * 
	 * @param jsonObject The JSON Object to add the3 extension to.
	 */
	protected void addExtensions(JSONObject jsonObject) {
		Iterator<String> iterator = extensions.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			jsonObject.put(key, extensions.get(key).toJSON());
		}
	}

	/**
	 * Validate the extensions.
	 * 
	 * @throws ValidationException If there was an error validating the extension
	 */
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
	
	/**
	 * Get the list of validation errors
	 * 
	 * @return The list of validation errors
	 */
	public List<String> getValidationErrors() { return(validationErrors); }

	/**
	 * Return the number of un-mapped keys found.  Un-mapped keys are keys that 
	 * are within the JSON feed, but were not mapped to any fields.
	 * 
	 * @return The number of un-mapped keys
	 */
	public int getUnMappedKeys() { return numUnmappedKeys; }
	
	/**
	 * Validate that the required object is not null.  If it is null, add it to 
	 * the validation error list.
	 *  
	 * @param object The object to evaluate 
	 * @param key The key that was looked up
	 * 
	 * @return true if this is required and is null, false if not null
	 */
	public boolean validateRequiredInError(Object object, String key) {
		if(null == object) {
			String validationError = String.format("[%s] Key '{}' _MUST_NOT_ be null", getLogger().getName(), key);
			validationErrors.add(validationError);
			return(true);
		}

		return(false);
	}
}
