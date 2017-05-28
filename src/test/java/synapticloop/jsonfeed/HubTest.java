package synapticloop.jsonfeed;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONObject;

public class HubTest {
	private Hub hub = null;
	private static final String LITE_STRING = "{ \"url\": \"http://some-url.com/\", \"type\": \"application/something\" }";

	@Before
	public void before() {
		hub = new Hub("url", "type");
	}

	@Test
	public void testInstantiate() throws ValidationException {
		Hub hub = new Hub("url", "type");
		assertEquals("url", hub.getUrl());
		assertEquals("type", hub.getType());
		assertEquals(0, hub.getUnMappedKeys());
		hub.validate();
		assertEquals(0,  hub.getValidationErrors().size());
	}
	
	@Test
	public void testInstantiateOneNull() {
		Hub hub = new Hub("url", null);
		assertEquals("url", hub.getUrl());
		assertEquals(null, hub.getType());
		assertEquals(0, hub.getUnMappedKeys());
		try {
			hub.validate();
		} catch (ValidationException e) {
		}
		assertEquals(1, hub.getValidationErrors().size());
	}
	
	@Test
	public void testSetterGetter() throws ValidationException {
		assertEquals("url", hub.getUrl());
		assertEquals("type", hub.getType());
		assertEquals(0, hub.getExtensions().size());
		assertEquals(0, hub.getUnMappedKeys());
		hub.validate();
		assertEquals(0,  hub.getValidationErrors().size());
	}

	@Test
	public void testJSONInstantiateLite() {
		Hub hub = new Hub(new JSONObject(LITE_STRING));
		assertEquals("http://some-url.com/", hub.getUrl());
		assertEquals("application/something", hub.getType());
		assertEquals(0, hub.getUnMappedKeys());
	}

	@Test
	public void testNullJSON() {
		Hub hub = new Hub(new JSONObject());
		assertEquals(0, hub.getUnMappedKeys());
		assertEquals("{}", hub.toString());
		try {
			hub.validate();
		} catch (ValidationException e) {
			// do nothing
		}
		assertEquals(1,  hub.getValidationErrors().size());
	}
}
