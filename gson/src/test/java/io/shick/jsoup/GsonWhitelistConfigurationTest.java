package io.shick.jsoup;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.junit.Test;

public class GsonWhitelistConfigurationTest {

  @Test
  public void parseTags() {
    String json = "{\"tags\" : [\"a\",\"b\"]}";

    final GsonWhitelistConfiguration c = GsonWhitelistConfiguration.fromJson(json);
    assertThat(c.allowsTag("a"), is(true));
    assertThat(c.allowsTag("b"), is(true));
    assertThat(c.allowsTag("c"), is(false));
    verifyNoNpes(c);

  }
  @Test
  public void parseAttributes() {
    String json = "{"
      + "  \"attributes\" : {\n"
      + "    \"blockquote\": [\"cite\"]\n"
      + "  }}";

    final GsonWhitelistConfiguration c = GsonWhitelistConfiguration.fromJson(json);
    assertThat(c.hasAllowedAttributes("blockquote"), is(true));
    assertThat(c.hasAllowedAttributes("something-else"), is(false));

    assertThat(c.allowsAttribute("blockquote", "cite"), is(true));
    assertThat(c.allowsAttribute("blockquote", "cited"), is(false));
    assertThat(c.allowsAttribute("something-else", "cite"), is(false));
    verifyNoNpes(c);

  }

  @Test
  public void enforcedAttributes() {
    String json = "{  \"enforcedAttributes\": {\n"
      + "    \"a\" : {\n"
      + "      \"rel\" : \"nofollow\"\n"
      + "    }\n"
      + "  }\n}";
    
    final GsonWhitelistConfiguration c = GsonWhitelistConfiguration.fromJson(json);
    assertThat(c.hasEnforcedAttributes("a"), is(true));
    assertThat(c.hasEnforcedAttributes("something-else"), is(false));

    assertThat(c.enforcesAttribute("a", "rel"), is(true));
    assertThat(c.enforcesAttribute("a", "rel","nofollow"), is(true));
    assertThat(c.enforcesAttribute("a", "rel","something-else"), is(false));
    assertThat(c.enforcesAttribute("blockquote", "cited"), is(false));
    assertThat(c.enforcesAttribute("something-else", "cite"), is(false));
    assertThat(c.enforcesAttribute("something-else", "cite", "something-else"), is(false));
    verifyNoNpes(c);

  }
  //    "protocols" : {
  @Test
  public void protocols() {
    String json = "{\n"
      + "  \"protocols\" : {\n"
      + "    \"a\" : { \n"
      + "      \"href\":[\"ftp\", \"http\", \"https\", \"mailto\"]\n"
      + "    }\n"
      + "  }\n"
      + "}";
    
    final GsonWhitelistConfiguration c = GsonWhitelistConfiguration.fromJson(json);
    assertThat(c.hasAllowedProtocols("a"), is(true));
    assertThat(c.hasAllowedProtocols("a", "href"), is(true));
    assertThat(c.allowsProtocol("a","href","ftp"), is(true));

    assertThat(c.hasAllowedProtocols("not-a"), is(false));
    assertThat(c.hasAllowedProtocols("a", "not-href"), is(false));
    assertThat(c.hasAllowedProtocols("not-a", "href"), is(false));
    assertThat(c.allowsProtocol("not-a","href","ftp"), is(false));
    assertThat(c.allowsProtocol("a","not-href","ftp"), is(false));
    assertThat(c.allowsProtocol("a","href","not-ftp"), is(false));

    verifyNoNpes(c);
  }

  private void verifyNoNpes(GsonWhitelistConfiguration c) {
    assertThat("ensure no NPEs", c.allowsTag("tag"), is(false));
    assertThat("ensure no NPEs", c.hasEnforcedAttributes("tag"), is(false));
    assertThat("ensure no NPEs", c.hasAllowedAttributes("tag"), is(false));
    assertThat("ensure no NPEs", c.hasAllowedProtocols("tag"), is(false));
  }

  @Test
  public void apply() {
    final String json="\n"
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

    final GsonWhitelistConfiguration c = GsonWhitelistConfiguration.fromJson(json);
    final Whitelist wl = mock(Whitelist.class);
    
    c.apply(wl);

    verify(wl).addTags(new String[]{"a", "b"});
    verify(wl).addAttributes("blockquote", new String[]{"cite"});
    verify(wl).addEnforcedAttribute("a", "rel", "nofollow");
    verify(wl).addProtocols("a", "href", new String[]{"ftp", "http", "https", "mailto"});
    
  }

  @Test
  public void whitelist() {
    final String json="\n"
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

    final Whitelist whitelist = GsonWhitelistConfiguration.whitelistFromJson(json);

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

}
