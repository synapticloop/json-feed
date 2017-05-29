package synapticloop.jsonfeed;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONObject;
import org.junit.Test;

import synapticloop.jsonfeed.util.Utils;

import static org.junit.Assert.*;

public class ExtensionTest {
	private static final String LITE_JSON = "{\"key\": \"value\", \"something\": \"else\", \"boolean\": false}";
	private static final String JSON_BAD_KEY_1 = "{\"_bad_key\": \"OK\"}";
	private static final String JSON_BAD_KEY_2 = "{\"bad.key\": \"OK\", \"another.bad.key\": \"OK\"}";

	@Test
	public void testEmptyJSON() throws ValidationException {
		Extension extension = new Extension(new JSONObject());
		extension.validate();
		assertEquals(0, extension.getValidationErrors().size());
	}

	@Test
	public void testInstantiation() throws ValidationException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("one", "two");
		Extension extension = new Extension(map);
		extension.validate();
		assertEquals(0, extension.getValidationErrors().size());

		assertEquals("{\"one\":\"two\"}", extension.toString());
		extension.addKeyValue("three", 4);
		assertEquals(4, extension.getValue("three"));
	}

	@Test
	public void testJSON() throws ValidationException {
		Extension extension = new Extension(new JSONObject(LITE_JSON));
		extension.validate();
		assertEquals(0, extension.getValidationErrors().size());
		assertEquals(false, extension.getValue("boolean"));
		assertEquals("else", extension.getValue("something"));
		assertEquals("value", extension.getValue("key"));
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
