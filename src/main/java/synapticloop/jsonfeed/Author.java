package synapticloop.jsonfeed;

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Author extends BaseJsonFeedObject {
	private static final Logger LOGGER = LoggerFactory.getLogger(Author.class);

	private static final String VALIDATION_ERROR_AT_LEAST_ONE_OF_MUST_BE_PRESENT_ALL_VALUES_WERE_NULL = "[%s] At least one of %s, %s, %S _MUST_ be present, all values were null.";


	// name (optional, string) is the author’s name.
	private String name;
	// url (optional, string) is the URL of a site owned by the author. It could be a blog, micro-blog, Twitter account, 
	// and so on. Ideally the linked-to page provides a way to contact the author, but that’s not required. The URL could 
	// be a mailto: link, though we suspect that will be rare.
	private String url;
	// avatar (optional, string) is the URL for an image for the author. As with icon, it should be square and relatively 
	// large — such as 512 x 512 — and should use transparency where appropriate, since it may be rendered on a 
	// non-white background.
	private String avatar;

	public Author(String name, String url, String avatar) {
		this.name = name;
		this.url = url;
		this.avatar = avatar;
	}

	public Author(JSONObject jsonObject) {
		this.name = readString(jsonObject, KEY_NAME);
		this.url = readString(jsonObject, KEY_URL);
		this.avatar = readString(jsonObject, KEY_AVATAR);

		warnOnMissingKeys(jsonObject);
	}

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public String getUrl() { return url; }

	public void setUrl(String url) { this.url = url; }

	public String getAvatar() { return avatar; }

	public void setAvatar(String avatar) { this.avatar = avatar; }

	@Override
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		addKeyValue(jsonObject, KEY_NAME, this.name);
		addKeyValue(jsonObject, KEY_URL, this.url);
		addKeyValue(jsonObject, KEY_AVATAR, this.avatar);
		return(jsonObject);
	}

	@Override
	protected Logger getLogger() {
		return(LOGGER);
	}

	@Override
	public String toString() {
		return(toJSON().toString());
	}

	@Override
	public void validate() throws ValidationException {
		if(null == this.name && null == this.url && null == this.avatar) {
			String validationError = String.format(VALIDATION_ERROR_AT_LEAST_ONE_OF_MUST_BE_PRESENT_ALL_VALUES_WERE_NULL, LOGGER.getName(), KEY_NAME, KEY_URL, KEY_AVATAR);
			LOGGER.error(validationError);
			validationErrors.add(validationError);
			throw new ValidationException(validationError);
		}
	}
}