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

import java.util.ArrayList;
import java.util.List;

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The feed is the root of all of the objects 
 * 
 * @author Synapticloop
 *
 */
public class Feed extends BaseJsonFeedObject {
	private static final Logger LOGGER = LoggerFactory.getLogger(Feed.class);

	// version (required, string) is the URL of the version of the format the feed uses. This should appear at the very 
	// top, though we recognize that not all JSON generators allow for ordering.
	private final String version;
	// title (required, string) is the name of the feed, which will often correspond to the name of the website (blog, 
	// for instance), though not necessarily.
	private final String title;
	// home_page_url (optional but strongly recommended, string) is the URL of the resource that the feed describes. This 
	// resource may or may not actually be a “home” page, but it should be an HTML page. If a feed is published on the public web, this should be considered as required. But it may not make sense in the case of a file created on a desktop computer, when that file is not shared or is shared only privately.
	private String homePageUrl;
	// feed_url (optional but strongly recommended, string) is the URL of the feed, and serves as the unique identifier 
	// for the feed. As with home_page_url, this should be considered required for feeds on the public web.
	private String feedUrl;
	// description (optional, string) provides more detail, beyond the  title, on what the feed is about. A feed reader 
	// may display this text.
	private String description;
	// user_comment (optional, string) is a description of the purpose of the feed. This is for the use of people looking 
	// at the raw JSON, and should be ignored by feed readers.
	private String userComment;
	// next_url (optional, string) is the URL of a feed that provides the next n items, where n is determined by the 
	// publisher. This allows for pagination, but with the expectation that reader software is not required to use it and 
	// probably won’t use it very often. next_url must not be the same as feed_url, and it must not be the same as a 
	// previous next_url (to avoid infinite loops).
	private String nextUrl;
	// icon (optional, string) is the URL of an image for the feed suitable to be used in a timeline, much the way an 
	// avatar might be used. It should be square and relatively large — such as 512 x 512 — so that it can be scaled-down 
	// and so that it can look good on retina displays. It should use transparency where appropriate, since it may be 
	// rendered on a non-white background.
	private String icon;
	// favicon (optional, string) is the URL of an image for the feed suitable to be used in a source list. It should be 
	// square and relatively small, but not smaller than 64 x 64 (so that it can look good on retina displays). As with 
	// icon, this image should use transparency where appropriate, since it may be rendered on a non-white background.
	private String favicon;
	// expired (optional, boolean) says whether or not the feed is finished — that is, whether or not it will ever update 
	// again. A feed for a temporary event, such as an instance of the Olympics, could expire. If the value is true, then 
	// it’s expired. Any other value, or the absence of expired, means the feed may continue to update.
	private Boolean expired;
	// author (optional, object) specifies the feed author. The author object has several members. These are all 
	// optional — but if you provide an author object, then at least one is required:
	private Author author;
	// items is an array, and is required.
	private List<Item> items = new ArrayList<Item>();
	// hubs (very optional, array of objects) describes endpoints that can be used to subscribe to real-time notifications 
	// from the publisher of this feed. Each object has a type and url, both of which are required. 
	private List<Hub> hubs;
	// Publishers can use custom objects in JSON Feeds. Names must start with an _ character and be followed by a letter. 
	// Custom objects can appear anywhere in a feed.
	private List<Extension> extensions;

	public Feed(String version, 
			String title, 
			String homePageUrl, 
			String feedUrl, 
			String description, 
			String userComment,
			String nextUrl, 
			String icon, 
			String favicon, 
			Boolean expired, 
			Author author,
			List<Hub> hubs,
			List<Extension> extensions) {

		this.version = version;
		this.title = title;
		this.homePageUrl = homePageUrl;
		this.feedUrl = feedUrl;
		this.description = description;
		this.userComment = userComment;
		this.nextUrl = nextUrl;
		this.icon = icon;
		this.favicon = favicon;
		this.expired = expired;
		this.author = author;
		this.hubs = hubs;
		this.extensions = extensions;
	}

	public Feed(String version, String title) {
		this.version = version;
		this.title = title;
	}

	public Feed(String jsonString) {
		this(new JSONObject(jsonString));
	}

	public Feed(JSONObject jsonObject) {
		this.version = readString(jsonObject, KEY_VERSION);
		this.title = readString(jsonObject, KEY_TITLE);
		this.homePageUrl = readString(jsonObject, KEY_HOME_PAGE_URL);
		this.feedUrl = readString(jsonObject, KEY_FEED_URL);
		this.description = readString(jsonObject, KEY_DESCRIPTION);
		this.userComment = readString(jsonObject, KEY_USER_COMMENT);
		this.nextUrl = readString(jsonObject, KEY_NEXT_URL);
		this.icon = readString(jsonObject, KEY_ICON);
		this.favicon = readString(jsonObject, KEY_FAVICON);
		this.expired = readBoolean(jsonObject, KEY_EXPIRED);
		this.items = readObjectArray(jsonObject, KEY_ITEMS, Item.class);
		this.hubs = readObjectArray(jsonObject, KEY_HUBS, Hub.class);

		JSONObject tempAuthorObject = readObject(jsonObject, KEY_AUTHOR);
		if(null != tempAuthorObject) {
			this.author = new Author(tempAuthorObject);
		}


		parseExtensions(jsonObject);
		warnOnMissingKeys(jsonObject);
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public void addHub(Hub hub) {
		if(null == hubs) {
			hubs = new ArrayList<Hub>();
		}

		hubs.add(hub);
	}

	public void addExtension(Extension extension) {
		if(null == extension) {
			extensions = new ArrayList<Extension>();
		}

		extensions.add(extension);
	}

	public String getHomePageUrl() { return homePageUrl; }

	public void setHomePageUrl(String homePageUrl) { this.homePageUrl = homePageUrl; }

	public String getFeedUrl() { return feedUrl; }

	public void setFeedUrl(String feedUrl) { this.feedUrl = feedUrl; }

	public String getDescription() { return description; }

	public void setDescription(String description) { this.description = description; }

	public String getUserComment() { return userComment; }

	public void setUserComment(String userComment) { this.userComment = userComment; }

	public String getNextUrl() { return nextUrl; }

	public void setNextUrl(String nextUrl) { this.nextUrl = nextUrl; }

	public String getIcon() { return icon; }

	public void setIcon(String icon) { this.icon = icon; }

	public String getFavicon() { return favicon; }

	public void setFavicon(String favicon) { this.favicon = favicon; }

	public Boolean getExpired() { return expired; }

	public void setExpired(Boolean expired) { this.expired = expired; }

	public Author getAuthor() { return author; }

	public void setAuthor(Author author) { this.author = author; }

	public String getVersion() { return version; }

	public String getTitle() { return title; }

	public List<Item> getItems() { return items; }

	public void setItems(List<Item> items) { this.items = items; }

	public List<Hub> getHubs() { return hubs; }

	public void setHubs(List<Hub> hubs) { this.hubs = hubs; }

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
		addKeyValue(jsonObject, KEY_VERSION, version);
		addKeyValue(jsonObject, KEY_TITLE, title);
		addKeyValue(jsonObject, KEY_HOME_PAGE_URL, homePageUrl);
		addKeyValue(jsonObject, KEY_FEED_URL, feedUrl);
		addKeyValue(jsonObject, KEY_DESCRIPTION, description);
		addKeyValue(jsonObject, KEY_USER_COMMENT, userComment);
		addKeyValue(jsonObject, KEY_NEXT_URL, nextUrl);
		addKeyValue(jsonObject, KEY_ICON, icon);
		addKeyValue(jsonObject, KEY_FAVICON, favicon);
		addKeyValue(jsonObject, KEY_EXPIRED, expired);
		addKeyValue(jsonObject, KEY_HUBS, hubs);
		addKeyValue(jsonObject, KEY_AUTHOR, author.toJSON());

		JSONArray itemsArray = new JSONArray();
		for (Item item : items) {
			itemsArray.put(item.toJSON());
		}

		if(itemsArray.length() > 0) {
			addKeyValue(jsonObject, KEY_ITEMS, itemsArray);
		}

		JSONArray hubsArray = new JSONArray();
		for(Hub hub: hubs) {
			hubsArray.put(hub.toJSON());
		}

		if(hubsArray.length() > 0) {
			addKeyValue(jsonObject, KEY_HUBS, hubsArray);
		}

		addExtensionsToJSON(jsonObject);

		return(jsonObject);
	}

	@Override
	public void validate() throws ValidationException {
		validationErrors.clear();

		boolean isInError = false;
		isInError = validateRequiredInError(version, KEY_VERSION) || isInError;
		isInError = validateRequiredInError(title, KEY_TITLE) || isInError;

		if(null != author) {
			try {
				author.validate();
			} catch(ValidationException ex) {
				validationErrors.addAll(author.getValidationErrors());
				isInError = true;
			}
		}

		for (Item item : items) {
			try {
				item.validate();
			} catch(ValidationException ex) {
				validationErrors.addAll(item.getValidationErrors());
			}
		}

		for (Hub hub : hubs) {
			try {
				hub.validate();
			} catch(ValidationException ex) {
				validationErrors.addAll(hub.getValidationErrors());
			}
		}

		try {
			validateExtensions();
		} catch(ValidationException ex) {
			isInError = true;
		}

		if(isInError) {
			throw new ValidationException("Could not validate feed.");
		}
	}
}
