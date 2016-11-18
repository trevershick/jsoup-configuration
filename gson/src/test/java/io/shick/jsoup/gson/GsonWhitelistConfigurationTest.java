package io.shick.jsoup.gson;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.shick.jsoup.WhitelistConfiguration;

import java.text.ParseException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.junit.Test;

public class GsonWhitelistConfigurationTest {

  @Test
  public void parseTags() throws ParseException {
    String json = "{\"tags\" : [\"a\",\"b\"]}";

    final WhitelistConfiguration c = new GsonParser().parse(json);
    assertThat(c.allowsTag("a"), is(true));
    assertThat(c.allowsTag("b"), is(true));
    assertThat(c.allowsTag("c"), is(false));
    verifyNoNpes(c);
  }

  @Test
  public void parseAttributes() throws ParseException {
    String json = "{"
      + "  \"attributes\" : {\n"
      + "    \"blockquote\": [\"cite\"]\n"
      + "  }}";

    final WhitelistConfiguration c = new GsonParser().parse(json);
    assertThat(c.hasAllowedAttributes("blockquote"), is(true));
    assertThat(c.hasAllowedAttributes("something-else"), is(false));

    assertThat(c.allowsAttribute("blockquote", "cite"), is(true));
    assertThat(c.allowsAttribute("blockquote", "cited"), is(false));
    assertThat(c.allowsAttribute("something-else", "cite"), is(false));
    verifyNoNpes(c);
  }

  @Test
  public void enforcedAttributes() throws ParseException {
    String json = "{  \"enforcedAttributes\": {\n"
      + "    \"a\" : {\n"
      + "      \"rel\" : \"nofollow\"\n"
      + "    }\n"
      + "  }\n}";

    final WhitelistConfiguration c = new GsonParser().parse(json);
    assertThat(c.hasEnforcedAttributes("a"), is(true));
    assertThat(c.hasEnforcedAttributes("something-else"), is(false));

    assertThat(c.enforcesAttribute("a", "rel"), is(true));
    assertThat(c.enforcesAttribute("a", "rel", "nofollow"), is(true));
    assertThat(c.enforcesAttribute("a", "rel", "something-else"), is(false));
    assertThat(c.enforcesAttribute("blockquote", "cited"), is(false));
    assertThat(c.enforcesAttribute("something-else", "cite"), is(false));
    assertThat(c.enforcesAttribute("something-else", "cite", "something-else"), is(false));
    verifyNoNpes(c);
  }

  //    "protocols" : {
  @Test
  public void protocols() throws ParseException {
    String json = "{\n"
      + "  \"protocols\" : {\n"
      + "    \"a\" : { \n"
      + "      \"href\":[\"ftp\", \"http\", \"https\", \"mailto\"]\n"
      + "    }\n"
      + "  }\n"
      + "}";

    final WhitelistConfiguration c = new GsonParser().parse(json);
    assertThat(c.hasAllowedProtocols("a"), is(true));
    assertThat(c.hasAllowedProtocols("a", "href"), is(true));
    assertThat(c.allowsProtocol("a", "href", "ftp"), is(true));

    assertThat(c.hasAllowedProtocols("not-a"), is(false));
    assertThat(c.hasAllowedProtocols("a", "not-href"), is(false));
    assertThat(c.hasAllowedProtocols("not-a", "href"), is(false));
    assertThat(c.allowsProtocol("not-a", "href", "ftp"), is(false));
    assertThat(c.allowsProtocol("a", "not-href", "ftp"), is(false));
    assertThat(c.allowsProtocol("a", "href", "not-ftp"), is(false));

    verifyNoNpes(c);
  }

  private void verifyNoNpes(WhitelistConfiguration c) {
    assertThat("ensure no NPEs", c.allowsTag("tag"), is(false));
    assertThat("ensure no NPEs", c.hasEnforcedAttributes("tag"), is(false));
    assertThat("ensure no NPEs", c.hasAllowedAttributes("tag"), is(false));
    assertThat("ensure no NPEs", c.hasAllowedProtocols("tag"), is(false));
  }

  @Test
  public void apply() throws ParseException {
    final String json = "\n"
      + "{\n"
      + "  \"tags\" : [\"a\",\"b\"],\n"
      + "  \"attributes\" : {\n"
      + "    \"blockquote\": [\"cite\"]\n"
      + "  },\n"
      + "  \"enforcedAttributes\": {\n"
      + "    \"a\" : {\n"
      + "      \"rel\" : \"nofollow\"\n"
      + "    }\n"
      + "  },\n"
      + "  \"protocols\" : {\n"
      + "    \"a\" : { \n"
      + "      \"href\":[\"ftp\", \"http\", \"https\", \"mailto\"]\n"
      + "    }\n"
      + "  }\n"
      + "}";

    final WhitelistConfiguration c = new GsonParser().parse(json);
    final Whitelist wl = mock(Whitelist.class);

    c.apply(wl);

    verify(wl).addTags(new String[]{"a", "b"});
    verify(wl).addAttributes("blockquote", new String[]{"cite"});
    verify(wl).addEnforcedAttribute("a", "rel", "nofollow");
    verify(wl).addProtocols("a", "href", new String[]{"ftp", "http", "https", "mailto"});
  }

  @Test
  public void whitelist() throws ParseException {
    final String json = "\n"
      + "{\n"
      + "  \"tags\" : [\"a\",\"b\"],\n"
      + "  \"attributes\" : {\n"
      + "    \"blockquote\": [\"cite\"]\n"
      + "  },\n"
      + "  \"enforcedAttributes\": {\n"
      + "    \"a\" : {\n"
      + "      \"rel\" : \"nofollow\"\n"
      + "    }\n"
      + "  },\n"
      + "  \"protocols\" : {\n"
      + "    \"a\" : { \n"
      + "      \"href\":[\"ftp\", \"http\", \"https\", \"mailto\"]\n"
      + "    }\n"
      + "  }\n"
      + "}";

    final Whitelist whitelist = new GsonParser().parse(json).whitelist();

    assertThat("Allowed Tag", Jsoup.isValid("<a>test</a>", whitelist), is(true));
    assertThat("Allowed Tag", Jsoup.isValid("<b>test</b>", whitelist), is(true));

    assertThat("Disallowed tag", Jsoup.isValid("<em>test</em>", whitelist), is(false));

    assertThat("Allowed Attribute", Jsoup.isValid("<blockquote cite='test'>test</blockquote>", whitelist), is(true));

    assertThat("Disallowed Attribute", Jsoup.isValid("<blockquote x='test'>test</blockquote>", whitelist), is(false));

    assertThat("Allowed Protocol", Jsoup.isValid("<a href='http://somewhere'>test</a>", whitelist), is(true));
    assertThat("Disallowed Protocol", Jsoup.isValid("<a href='whatevs://somewhere'>test</a>", whitelist), is(false));

    final Document.OutputSettings settings = new Document.OutputSettings().prettyPrint(false);

    assertThat("Clean Disallowed Attribute",
      Jsoup.clean("<blockquote x='test'>test</blockquote>", "", whitelist, settings),
      is("<blockquote>test</blockquote>"));

    assertThat("Clean Enforced Attribute",
      Jsoup.clean("<a>test</a>", "", whitelist, settings),
      is("<a rel=\"nofollow\">test</a>"));

    assertThat("Clean Disallowed Protocol",
      Jsoup.clean("<a href='whatevs://somewhere'>test</a>", "", whitelist, settings),
      is("<a rel=\"nofollow\">test</a>"));
  }

  @Test
  public void format() throws ParseException {
    final String json = "\n"
      + "{\n"
      + "  \"tags\" : [\"a\",\"b\"],\n"
      + "  \"attributes\" : {\n"
      + "    \"blockquote\": [\"cite\"]\n"
      + "  },\n"
      + "  \"enforcedAttributes\": {\n"
      + "    \"a\" : {\n"
      + "      \"rel\" : \"nofollow\"\n"
      + "    }\n"
      + "  },\n"
      + "  \"protocols\" : {\n"
      + "    \"a\" : { \n"
      + "      \"href\":[\"ftp\", \"http\", \"https\", \"mailto\"]\n"
      + "    }\n"
      + "  }\n"
      + "}";

    final WhitelistConfiguration config = new GsonParser().parse(json);
    final String actual = new GsonFormatter().format(config).toString();
    final WhitelistConfiguration config2 = new GsonParser().parse(actual);

    assertThat(actual, is(config.toString()));
    assertThat(stripped(json), is(stripped(actual)));
    assertThat(config, is(config2));
  }

  @Test(expected = IllegalStateException.class)
  public void invalidBase() throws ParseException {
    String text = "{ \"base\":\"basic2\" }"; // use 'basic'
    final WhitelistConfiguration c = new GsonParser().parse(text);
    c.whitelist();
  }
  
  @Test
  public void baseCanBeSpecified() throws ParseException {
    String text = "{ \"base\":\"basic\" }"; // use 'basic'
    final WhitelistConfiguration c = new GsonParser().parse(text);

    Whitelist wl = c.whitelist();
    Whitelist basic = Whitelist.basic();
    String html = "<a href=\"http://x\">To X</a>";
    assertThat("Basic will add rel=nofollow to links.",
      Jsoup.clean(html, wl),
      is(Jsoup.clean(html, basic)));
    assertThat("Basic will add rel=nofollow to links.",
      Jsoup.clean(html, wl),
      containsString("nofollow"));


    assertThat(c.toString(), is(new GsonFormatter().format(c)));
  }


  private String stripped(String json) {
    return json.replaceAll("[\\n\\s]", "");
  }
}
