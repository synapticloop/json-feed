package synapticloop.jsonfeed;

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
		if(null == url || null == type) {
			String validationError = String.format(VALIDATION_ERROR_BOTH_KEYS_MUST_NOT_BE_NULL_VALUES_WERE, LOGGER.getName(), KEY_URL, KEY_TYPE, url, type);
			LOGGER.error(validationError);
			validationErrors.add(validationError);
			throw new ValidationException(validationError);
		}
	}

}
