package synapticloop.jsonfeed;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONObject;

public class AuthorTest {
	private static final String LITE_STRING = "{ \"url\": \"http://some-url.com/\", \"name\": \"John Doe\", \"avatar\": \"http://example.com/avatar\" }";

	@Test
	public void testInstantiate() throws ValidationException {
		Author author = new Author("name", "url", "avatar");
		assertEquals("url", author.getUrl());
		assertEquals("name", author.getName());
		assertEquals("avatar", author.getAvatar());
		assertEquals(0, author.getUnMappedKeys());
		author.validate();
		assertEquals(0,  author.getValidationErrors().size());
	}
	
	@Test
	public void testInstantiateOneNull() {
		Author author = new Author(null, null, null);
		
		try {
			author.validate();
		} catch (ValidationException e) {
		}
		assertEquals(1, author.getValidationErrors().size());

		author.setName("name");

		try {
			author.validate();
		} catch (ValidationException e) {
		}
		assertEquals(0, author.getValidationErrors().size());
		
		author.setName(null);
		author.setUrl("url");
		try {
			author.validate();
		} catch (ValidationException e) {
		}
		assertEquals(0, author.getValidationErrors().size());

		author.setUrl(null);
		author.setAvatar("avatar");
		try {
			author.validate();
		} catch (ValidationException e) {
		}
		assertEquals(0, author.getValidationErrors().size());
	}
	

	@Test
	public void testJSONInstantiateLite() {
		Author author = new Author(new JSONObject(LITE_STRING));
		assertEquals("http://some-url.com/", author.getUrl());
		assertEquals("John Doe", author.getName());
		assertEquals("http://example.com/avatar", author.getAvatar());
		assertEquals(0, author.getUnMappedKeys());
	}

	@Test
	public void testNullJSON() {
		Author author = new Author(new JSONObject());
		assertEquals(0, author.getUnMappedKeys());
		assertEquals("{}", author.toString());
		try {
			author.validate();
		} catch (ValidationException e) {
			// do nothing
		}
		assertEquals(1,  author.getValidationErrors().size());
	}
}
