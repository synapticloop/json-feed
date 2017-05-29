package synapticloop.jsonfeed;

import java.util.List;

import synapticloop.jsonfeed.exception.ValidationException;

import org.json.JSONObject;
import org.junit.Test;

import synapticloop.jsonfeed.util.Utils;

import static org.junit.Assert.*;

public class FeedTest {

	@Test
	public void testDaringFireballFeed() throws ValidationException {
		JSONObject jsonObject = new JSONObject(Utils.resourceToString(FeedTest.class.getResourceAsStream("/daringfireball.com.json")));
		Feed feed = new Feed(jsonObject);
		List<Item> items = feed.getItems();
		assertNotNull(items);
		assertTrue(items.size() > 0);

		feed.validate();
		assertEquals(0, feed.getValidationErrors().size());
	}

	@Test
	public void testBitSplittingFeed() throws ValidationException {
		JSONObject jsonObject = new JSONObject(Utils.resourceToString(FeedTest.class.getResourceAsStream("/bitsplitting.org.json")));
		Feed feed = new Feed(jsonObject);
		List<Item> items = feed.getItems();
		assertNotNull(items);
		assertTrue(items.size() > 0);

		feed.validate();
		assertEquals(0, feed.getValidationErrors().size());
	}

}
