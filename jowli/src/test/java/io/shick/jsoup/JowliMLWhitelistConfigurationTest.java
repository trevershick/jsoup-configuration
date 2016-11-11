package io.shick.jsoup;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.junit.Test;

/**
 * Created by tshick on 11/10/16.
 */
public class JowliMLWhitelistConfigurationTest {

  @Test
  public void whitelist() {
//    p:a[href:[ftp,http,https],a:b];

    final StringBuilder jowliml = new StringBuilder()
      .append("t:a,b")
      .append(";")
      .append("a:blockquote[cite],a[href,rel]")
      .append(";")
      .append("e:a[rel:nofollow,x:y]")
      .append(";")
      .append("p:a[href:[ftp,http,https],z:[d]]")
      ;
    final Whitelist whitelist = JowliMLWhitelistConfiguration.whitelistFromJowliML(jowliml.toString());

    assertThat("Allowed Tag", Jsoup.isValid("<a>test</a>", whitelist), is(true));
    assertThat("Allowed Tag", Jsoup.isValid("<b>test</b>", whitelist), is(true));

    assertThat("Disallowed tag", Jsoup.isValid("<em>test</em>", whitelist), is(false));

    assertThat("Allowed Attribute", Jsoup.isValid("<blockquote cite='test'>test</blockquote>", whitelist), is(true));

    assertThat("Disallowed Attribute", Jsoup.isValid("<blockquote x='test'>test</blockquote>", whitelist), is(false));

    assertThat("Allowed Protocol", Jsoup.isValid("<a href='http://somewhere'>test</a>", whitelist), is(true));
    assertThat("Allowed Protocol", Jsoup.isValid("<a z='d://somewhere'>test</a>", whitelist), is(true));

    assertThat("Disallowed Protocol", Jsoup.isValid("<a href='whatevs://somewhere'>test</a>", whitelist), is(false));
    assertThat("Disallowed Protocol", Jsoup.isValid("<a z='b://somewhere'>test</a>", whitelist), is(false));

    final Document.OutputSettings settings = new Document.OutputSettings().prettyPrint(false);

    assertThat("Clean Disallowed Attribute",
      Jsoup.clean("<blockquote x='test'>test</blockquote>", "", whitelist, settings),
      is("<blockquote>test</blockquote>"));

    assertThat("Clean Enforced Attribute",
      Jsoup.clean("<a>test</a>", "", whitelist, settings),
      is("<a x=\"y\" rel=\"nofollow\">test</a>"));

    assertThat("Clean Disallowed Protocol",
      Jsoup.clean("<a href='whatevs://somewhere'>test</a>", "", whitelist, settings),
      is("<a x=\"y\" rel=\"nofollow\">test</a>"));
  }
}
