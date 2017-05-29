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
import java.util.Date;
import java.util.List;
import java.util.Map;

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Item extends BaseJsonFeedObject {
	private static final Logger LOGGER = LoggerFactory.getLogger(Item.class);

	// id (required, string) is unique for that item for that feed over time. If an item is ever updated, the id should 
	// be unchanged. New items should never use a previously-used id. If an id is presented as a number or other type, a 
	// JSON Feed reader must coerce it to a string. Ideally, the id is the full URL of the resource described by the item, 
	// since URLs make great unique identifiers.
	private final String id;
	// url (optional, string) is the URL of the resource described by the item. It’s the permalink. This may be the same 
	// as the id — but should be present regardless.
	private String url;
	// external_url (very optional, string) is the URL of a page elsewhere. This is especially useful for linkblogs. If 
	// url links to where you’re talking about a thing, then external_url links to the thing you’re talking about.
	private String externalUrl;
	// title (optional, string) is plain text. Microblog items in particular may omit titles.
	private String title;
	// content_html and content_text are each optional strings — but one or both must be present. This is the HTML or 
	// plain text of the item. Important: the only place HTML is allowed in this format is in  content_html. A Twitter-like 
	// service might use content_text, while a blog might use content_html. Use whichever makes sense for your resource. 
	// (It doesn’t even have to be the same for each item in a feed.)
	private String contentHtml;
	private String contentText;
	// summary (optional, string) is a plain text sentence or two describing the item. This might be presented in a 
	// timeline, for instance, where a detail view would display all of content_html or content_text.
	private String summary;
	// image (optional, string) is the URL of the main image for the item. This image may also appear in the content_html 
	// — if so, it’s a hint to the feed reader that this is the main, featured image. Feed readers may use the image as a 
	// preview (probably resized as a thumbnail and placed in a timeline).
	private String image;
	// banner_image (optional, string) is the URL of an image to use as a banner. Some blogging systems (such as Medium) 
	// display a different banner image chosen to go with each post, but that image wouldn’t otherwise appear in the 
	// content_html. A feed reader with a detail view may choose to show this banner image at the top of the detail view, 
	// possibly with the title overlaid.
	private String bannerImage;
	// date_published (optional, string) specifies the date in RFC 3339 format. (Example: 2010-02-07T14:04:00-05:00.)
	private Date datePublished;
	// date_modified (optional, string) specifies the modification date in RFC 3339 format.
	private Date dateModified;
	// author (optional, object) has the same structure as the top-level  author. If not specified in an item, then the 
	// top-level author, if present, is the author of the item.
	private Author author;
	// tags (optional, array of strings) can have any plain text values you want. Tags tend to be just one word, but they 
	// may be anything. Note: they are not the equivalent of Twitter hashtags. Some blogging systems and other feed 
	// formats call these categories.
	private List<String> tags = new ArrayList<String>();
	// An individual item may have one or more attachments.
	private List<Attachment> attachments =  new ArrayList<Attachment>();

	public Item(JSONObject jsonObject) {
		this.id = readString(jsonObject, KEY_ID);
		this.url = readString(jsonObject, KEY_URL);
		this.externalUrl = readString(jsonObject, KEY_EXTERNAL_URL);
		this.title = readString(jsonObject, KEY_TITLE);
		this.contentHtml = readString(jsonObject, KEY_CONTENT_HTML);
		this.contentText = readString(jsonObject, KEY_CONTENT_TEXT);
		this.summary = readString(jsonObject, KEY_SUMMARY);
		this.image = readString(jsonObject, KEY_IMAGE);
		this.bannerImage = readString(jsonObject, KEY_BANNER_IMAGE);
		this.datePublished = readDate(jsonObject, KEY_DATE_PUBLISHED);
		this.dateModified = readDate(jsonObject, KEY_DATE_MODIFIED);

		JSONObject tempAuthorObject = readObject(jsonObject, KEY_AUTHOR);
		if(null != tempAuthorObject) {
			this.author = new Author(tempAuthorObject);
		}

		this.tags = readStringArray(jsonObject, KEY_TAGS);
		this.attachments = readObjectArray(jsonObject, KEY_ATTACHMENTS, Attachment.class);
		parseExtensions(jsonObject);
	}

	public Item(String id) { 
		this.id = id; 
	}


	public Item(String id, 
			String url, 
			String externalUrl, 
			String title, 
			String contentHtml, 
			String contentText,
			String summary, 
			String image, 
			String bannerImage, 
			Date datePublished, 
			Date dateModified, 
			Author author,
			List<String> tags, 
			List<Attachment> attachments, 
			Map<String, Extension> extensions) {

		this.id = id;
		this.url = url;
		this.externalUrl = externalUrl;
		this.title = title;
		this.contentHtml = contentHtml;
		this.contentText = contentText;
		this.summary = summary;
		this.image = image;
		this.bannerImage = bannerImage;
		this.datePublished = datePublished;
		this.dateModified = dateModified;
		this.author = author;
		this.tags = tags;
		this.attachments = attachments;
		this.extensions = extensions;
	}

	public String getUrl() { return url; }

	public void setUrl(String url) { this.url = url; }

	public String getExternalUrl() { return externalUrl; }

	public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }

	public String getTitle() { return title; }

	public void setTitle(String title) { this.title = title; }

	public String getContentHtml() { return contentHtml; }

	public void setContentHtml(String contentHtml) { this.contentHtml = contentHtml; }

	public String getContentText() { return contentText; }

	public void setContentText(String contentText) { this.contentText = contentText; }

	public String getSummary() { return summary; }

	public void setSummary(String summary) { this.summary = summary; }

	public String getImage() { return image; }

	public void setImage(String image) { this.image = image; }

	public String getBannerImage() { return bannerImage; }

	public void setBannerImage(String bannerImage) { this.bannerImage = bannerImage; }

	public Date getDatePublished() { return datePublished; }

	public void setDatePublished(Date datePublished) { this.datePublished = datePublished; }

	public Date getDateModified() { return dateModified; }

	public void setDateModified(Date dateModified) { this.dateModified = dateModified; }

	public Author getAuthor() { return author; }

	public void setAuthor(Author author) { this.author = author; }

	public List<String> getTags() { return tags; }

	public void setTags(List<String> tags) { this.tags = tags; }

	public String getId() { return id; }

	public List<Attachment> getAttachments() { return attachments; }

	public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }

	@Override
	protected Logger getLogger() {
		return(LOGGER);
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validate() throws ValidationException {
		validationErrors.clear();

		boolean isInError = false;
		if(null == id) {
			String validationError = String.format("[%s] Key '{}' _MUST_NOT_ be null", LOGGER.getName(), KEY_ID);
			LOGGER.error(validationError);
			validationErrors.add(validationError);
			isInError = true;
		}

		try {
			author.validate();
		} catch(ValidationException ex) {
			validationErrors.addAll(author.getValidationErrors());
			isInError = true;
		}

		if(null == contentHtml && null == contentText) {
			String validationError = String.format("[%s] One of '{}' or '{}' _MUST_NOT_ be null", LOGGER.getName(), KEY_CONTENT_HTML, KEY_CONTENT_TEXT);
			validationErrors.add(validationError);
			isInError = true;
		}

		for (Attachment attachment : attachments) {
			try {
				attachment.validate();
			} catch(ValidationException ex) {
				validationErrors.addAll(attachment.getValidationErrors());
			}
		}

		try {
			validateExtensions();
		} catch(ValidationException ex) {
			isInError = true;
		}

		if(isInError) {
			throw new ValidationException("Could not validate Item");
		}
	}


}
