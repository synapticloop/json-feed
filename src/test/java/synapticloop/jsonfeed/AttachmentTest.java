package synapticloop.jsonfeed;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONObject;

public class AttachmentTest {
	private Attachment attachment = null;
	private static final String LITE_STRING = "{ \"url\": \"http://some-url.com/\", \"mime_type\": \"application/something\" }";
	private static final String HEAVY_STRING = "{\"url\": \"http://some-url.com/\",\"mime_type\": \"application/something\",\"title\": \"this is the title of the attachment\",\"size_in_bytes\": 100,\"duration_in_seconds\": 400,\"_some_attachment_extension\": { \"about\": \"https://blueshed-podcasts.com/json-feed-extension-docs\",\"explicit\": false,\"copyright\": \"1948 by George Orwell\",\"owner\": \"Big Brother and the Holding Company\",\"subtitle\": \"All shouting, all the time. Double. Plus. Good.\"}}";

	@Before
	public void before() {
		attachment = new Attachment("url", "mimeType", "title", -1l, -1l, null);
	}

	@Test
	public void testInstantiate() throws ValidationException {
		Attachment attachment = new Attachment("url", "mimeType");
		assertEquals("url", attachment.getUrl());
		assertEquals("mimeType", attachment.getMimeType());
		assertEquals(0, attachment.getUnMappedKeys());
		attachment.validate();
		assertEquals(0,  attachment.getValidationErrors().size());
	}
	
	
	@Test
	public void testSetterGetter() throws ValidationException {
		assertEquals("url", attachment.getUrl());
		assertEquals("mimeType", attachment.getMimeType());
		assertEquals("title", attachment.getTitle());
		assertEquals(new Long(-1), attachment.getSizeInBytes());
		assertEquals(new Long(-1), attachment.getDurationInSeconds());
		assertNull(attachment.getExtensions());
		assertEquals(0, attachment.getUnMappedKeys());
		attachment.validate();
		assertEquals(0,  attachment.getValidationErrors().size());
	}

	@Test
	public void testJSONInstantiateLite() {
		Attachment attachment = new Attachment(new JSONObject(LITE_STRING));
		assertEquals("http://some-url.com/", attachment.getUrl());
		assertEquals("application/something", attachment.getMimeType());
		assertEquals(0, attachment.getUnMappedKeys());
	}

	@Test
	public void testJSONInstantiateHeavy() throws ValidationException {
		Attachment attachment = new Attachment(new JSONObject(HEAVY_STRING));
		assertEquals("http://some-url.com/", attachment.getUrl());
		assertEquals("application/something", attachment.getMimeType());
		assertEquals(0, attachment.getUnMappedKeys());
		attachment.validate();
		assertEquals(0,  attachment.getValidationErrors().size());
		JSONObject jsonObject = new JSONObject(attachment.toString());
	}

	@Test
	public void testNullJSON() {
		Attachment attachment = new Attachment(new JSONObject());
		assertEquals(0, attachment.getUnMappedKeys());
		assertEquals("{}", attachment.toString());
		try {
			attachment.validate();
		} catch (ValidationException e) {
			// do nothing
		}
		assertEquals(2,  attachment.getValidationErrors().size());
	}
}
