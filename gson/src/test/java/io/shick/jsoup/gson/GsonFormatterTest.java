package io.shick.jsoup.gson;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import io.shick.jsoup.BasicWhitelistConfiguration;
import io.shick.jsoup.WhitelistConfiguration;

import java.text.ParseException;

import org.junit.Test;

public class GsonFormatterTest {

  @Test
  public void fullCircleWithTags() throws ParseException {
    WhitelistConfiguration wlc = new BasicWhitelistConfiguration()
      .allowTag("blockquote")
      .allowTag("b");
    final String json = new GsonFormatter().format(wlc).toString();
    assertThat(json, is("{\"tags\":[\"blockquote\",\"b\"]}"));
    final WhitelistConfiguration wlc2 = new GsonParser().parse(json);
    assertThat(wlc, is(wlc2));
  }
  @Test
  public void fullCircleWithAllowedAttributes() throws ParseException {
    WhitelistConfiguration wlc = new BasicWhitelistConfiguration()
      .allowAttribute("blockquote","cite")
      .allowAttribute("a","href");
    final String json = new GsonFormatter().format(wlc).toString();
    assertThat(json, is("{\"attributes\":{\"a\":[\"href\"],\"blockquote\":[\"cite\"]}}"));
    final WhitelistConfiguration wlc2 = new GsonParser().parse(json);
    assertThat(wlc, is(wlc2));
  }
  @Test
  public void fullCircleWithEnforcedAttributes() throws ParseException {
    WhitelistConfiguration wlc = new BasicWhitelistConfiguration()
      .enforceAttribute("a","rel","nofollow");
    final String json = new GsonFormatter().format(wlc).toString();
    assertThat(json, is("{\"enforcedAttributes\":{\"a\":{\"rel\":\"nofollow\"}}}"));
    final WhitelistConfiguration wlc2 = new GsonParser().parse(json);
    assertThat(wlc, is(wlc2));
  }
  @Test
  public void fullCircleWithProtocols() throws ParseException {
    WhitelistConfiguration wlc = new BasicWhitelistConfiguration()
      .allowProtocol("a","href","mailto");
    final String json = new GsonFormatter().format(wlc).toString();
    assertThat(json, is( "{\"protocols\":{\"a\":{\"href\":[\"mailto\"]}}}"));
    final WhitelistConfiguration wlc2 = new GsonParser().parse(json);
    assertThat(wlc, is(wlc2));
  }
  @Test
  public void format() throws ParseException {
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

    final WhitelistConfiguration config = new GsonParser().parse(json);
    final String actual = new GsonFormatter().format(config).toString();
    final WhitelistConfiguration config2 = new GsonParser().parse(actual);

    assertThat(stripped(json),is(stripped(actual)));
    assertThat(config, is(config2));
  }


  @Test
  public void emptyCollectionsAreNull() throws ParseException {
    final String json="\n"
      + "{\n"
      + "  \"tags\" : [],\n"
      + "  \"attributes\" : {},\n"
      + "  \"enforcedAttributes\": {},\n"
      + "  \"protocols\" : {}\n"
      + "}";

    final WhitelistConfiguration config = new GsonParser().parse(json);
    final String actual = new GsonFormatter().format(config).toString();
    
    final WhitelistConfiguration config2 = new GsonParser().parse(actual);

    assertThat(actual,is("{}"));
    assertThat(config, is(config2));
  }

  private String stripped(String json) {
    return json.replaceAll("[\\n\\s]","");
  }
}
