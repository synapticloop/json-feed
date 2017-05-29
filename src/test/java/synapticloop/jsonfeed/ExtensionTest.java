package synapticloop.jsonfeed;

import java.io.InputStream;
import java.util.List;

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONObject;
import org.junit.Test;

import synapticloop.jsonfeed.util.Utils;

import static org.junit.Assert.*;

public class ExtensionTest {
	private static final String LITE_JSON = "{\"key\": \"value\", \"something\": else, \"boolean\": false}";
	private static final String JSON_BAD_KEY_1 = "{\"_bad_key\": \"OK\"}";
	private static final String JSON_BAD_KEY_2 = "{\"bad.key\": \"OK\", \"another.bad.key\": \"OK\"}";

	@Test
	public void testEmptyJSON() throws ValidationException {
		Extension extension = new Extension(new JSONObject());
		extension.validate();
		assertEquals(0, extension.getValidationErrors().size());
	}

	@Test
	public void testJSON() throws ValidationException {
		Extension extension = new Extension(new JSONObject(LITE_JSON));
		extension.validate();
		assertEquals(0, extension.getValidationErrors().size());
	}

	@Test
	public void testBadJSONKeyUnderscore() throws ValidationException {
		Extension extension = new Extension(new JSONObject(JSON_BAD_KEY_1));
		try {
			extension.validate();
		} catch (ValidationException ex) {
			// do nothing
		}
		assertEquals(1, extension.getValidationErrors().size());
	}

	@Test
	public void testBadJSONKeyFullStops() throws ValidationException {
		Extension extension = new Extension(new JSONObject(JSON_BAD_KEY_2));
		try {
			extension.validate();
		} catch (ValidationException ex) {
			// do nothing
		}
		assertEquals(2, extension.getValidationErrors().size());
	}

}
