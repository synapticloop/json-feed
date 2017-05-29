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

import java.util.Map;

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author synapticloop
 *
 */
public class Attachment extends BaseJsonFeedObject {
	private static final Logger LOGGER = LoggerFactory.getLogger(Attachment.class);

	private static final String VALIDATION_ERROR_KEYS_CANNOT_BE_NULL = "[%s] %s and %s cannot be null";


	// url (required, string) specifies the location of the attachment.
	private final String url;
	// mime_type (required, string) specifies the type of the attachment, such as “audio/mpeg.”
	private final String mimeType;
	// title (optional, string) is a name for the attachment. Important: if there are multiple attachments, and two or 
	// more have the exact same title (when title is present), then they are considered as alternate representations of 
	// the same thing. In this way a podcaster, for instance, might provide an audio recording in different formats.
	private String title;
	// size_in_bytes (optional, number) specifies how large the file is.
	private Long sizeInBytes;
	// duration_in_seconds (optional, number) specifies how long the attachment takes to listen to or watch.
	private Long durationInSeconds;

	/**
	 * Instantiate a new attachment object
	 * 
	 * @param url The URL which points to the attachment
	 * @param mimeType The mimetype of the attachment
	 */
	public Attachment(String url, String mimeType) {
		this.url = url;
		this.mimeType = mimeType;
	}

	public Attachment(String url, String mimeType, String title, Long sizeInBytes, Long durationInSeconds, Map<String, Extension> extensions) {
		this.url = url;
		this.mimeType = mimeType;
		this.title = title;
		this.sizeInBytes = sizeInBytes;
		this.durationInSeconds = durationInSeconds;
		this.extensions = extensions;
	}

	public Attachment(JSONObject jsonObject) {
		this.url = readString(jsonObject, KEY_URL);
		this.mimeType = readString(jsonObject, KEY_MIME_TYPE);
		this.title = readString(jsonObject, KEY_TITLE);
		this.sizeInBytes = readLong(jsonObject, KEY_SIZE_IN_BYTES);
		this.durationInSeconds = readLong(jsonObject, KEY_DURATION_IN_SECONDS);

		// now go through the extensions and parse them
		parseExtensions(jsonObject);

		warnOnMissingKeys(jsonObject);
	}

	/**
	 * url (required, string) specifies the location of the attachment.
	 * 
	 * @return the location of the attachment
	 */
	public String getUrl() { return url; }

	public String getMimeType() { return mimeType; }

	public String getTitle() { return title; }

	public void setTitle(String title) { this.title = title; }

	public Long getSizeInBytes() { return sizeInBytes; }

	public void setSizeInBytes(Long sizeInBytes) { this.sizeInBytes = sizeInBytes; }

	public Long getDurationInSeconds() { return durationInSeconds; }

	public void setDurationInSeconds(Long durationInSeconds) { this.durationInSeconds = durationInSeconds; }

	@Override
	protected Logger getLogger() {
		return(LOGGER);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		addKeyValue(jsonObject, KEY_URL, this.url);
		addKeyValue(jsonObject, KEY_MIME_TYPE, this.mimeType);
		addKeyValue(jsonObject, KEY_TITLE, this.title);
		addKeyValue(jsonObject, KEY_SIZE_IN_BYTES, this.sizeInBytes);
		addKeyValue(jsonObject, KEY_DURATION_IN_SECONDS, this.durationInSeconds);

		addExtensionsToJSON(jsonObject);

		return(jsonObject);
	}

	@Override
	public String toString() {
		return(toJSON().toString());
	}

	@Override
	public void validate() throws ValidationException {
		boolean isInError = false;
		isInError = validateRequiredInError(url, KEY_URL) || isInError;
		isInError = validateRequiredInError(mimeType, KEY_MIME_TYPE) || isInError ;

		if(isInError) {
			String validationException = String.format(VALIDATION_ERROR_KEYS_CANNOT_BE_NULL, LOGGER.getName(), KEY_URL, KEY_MIME_TYPE);
			LOGGER.error(validationException);

			throw new ValidationException(validationException);
		}

	}
}
