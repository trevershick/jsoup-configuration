package io.shick.jsoup;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.shick.jsoup.util.Func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.junit.Test;

public class BasicWhitelistConfigurationTest {

  final BasicWhitelistConfiguration config = new BasicWhitelistConfiguration();

  @Test(expected = IllegalStateException.class)
  public void invalidBase() {
    config.base("invalid").whitelist();
  }
  
  @Test
  public void tags() {
    assertThat(config.allowsTag("a"), is(false));
    config.allowTag("a");
    assertThat(config.allowsTag("a"), is(true));

    config.allowTag("b");
    config.allowTag("c");
    List<String> x = new ArrayList();
    config.allowedTags(x::add);
    assertThat(x, is(Func.list("a", "b", "c")));
  }

  @Test
  public void allowedAttributes() {
    assertThat(config.allowsAttribute("a", "href"), is(false));
    config.allowAttribute("a", "href");
    assertThat(config.allowsAttribute("a", "href"), is(true));
    assertThat(config.allowsAttribute("a", "not-href"), is(false));

    config.allowAttribute("blockquote", "cite");
    config.allowAttribute("a", "rel");

    Map<String, List<String>> x = new HashMap();
    config.allowedAttributes(x::put);

    assertThat(x.size(), is(2));
    assertThat(x.containsKey("a"), is(true));
    assertThat(x.get("a"), is(Func.list("href", "rel")));

    assertThat(x.containsKey("blockquote"), is(true));
    assertThat(x.get("blockquote"), is(Func.list("cite")));
  }

  @Test
  public void enforceAttribute() {
    assertThat(config.enforcesAttribute("a", "rel", "nofollow"), is(false));
    config.enforceAttribute("a", "rel", "nofollow");
    assertThat(config.enforcesAttribute("a", "rel", "nofollow"), is(true));
    assertThat(config.enforcesAttribute("a", "rel", "not-nofollow"), is(false));

    assertThat(config.enforcesAttribute("a", "rel"), is(true));
    assertThat(config.enforcesAttribute("a", "not-rel"), is(false));

    config.enforceAttribute("a", "href", "z");
    config.enforceAttribute("blockquote", "cite", "noone");

    Map<String, Map<String, String>> x = new HashMap();
    config.enforcedAttributes(x::put);

    assertThat(x.size(), is(2));
    assertThat(x.containsKey("a"), is(true));
    assertThat(x.containsKey("blockquote"), is(true));

    assertThat(x.get("a").get("href"), is("z"));
    assertThat(x.get("a").get("rel"), is("nofollow"));

    assertThat(x.get("blockquote").get("cite"), is("noone"));
  }

  @Test
  public void protocol() {
    assertThat(config.allowsProtocol("a", "href", "mailto"), is(false));
    config.allowProtocol("a", "href", "mailto");
    assertThat(config.allowsProtocol("a", "href", "mailto"), is(true));
    assertThat(config.allowsProtocol("a", "href", "not-mailto"), is(false));

    assertThat(config.hasAllowedProtocols("a", "href"), is(true));
    assertThat(config.hasAllowedProtocols("a", "not-href"), is(false));

    config.allowProtocol("a", "href", "http");
    config.allowProtocol("img", "src", "https");

    Map<String, Map<String, List<String>>> x = new HashMap();
    config.allowedProtocols(x::put);

    assertThat(x.size(), is(2));
    assertThat(x.containsKey("a"), is(true));
    assertThat(x.containsKey("img"), is(true));

    assertThat(x.get("a").get("href"), is(Func.list("mailto", "http")));
    assertThat(x.get("img").get("src"), is(Func.list("https")));
  }

  @Test
  public void apply() {
    final Whitelist wl = mock(Whitelist.class);

    final MutableWhitelistConfiguration c = new BasicWhitelistConfiguration()
      .allowTag("a")
      .allowTag("b")
      .allowAttribute("blockquote", "cite")
      .enforceAttribute("a", "rel", "nofollow")
      .allowProtocol("a", "href", "ftp")
      .allowProtocol("a", "href", "http")
      .allowProtocol("a", "href", "https")
      .allowProtocol("a", "href", "mailto");

    c.apply(wl);

    verify(wl).addTags(new String[]{"a", "b"});
    verify(wl).addAttributes("blockquote", new String[]{"cite"});
    verify(wl).addEnforcedAttribute("a", "rel", "nofollow");
    verify(wl).addProtocols("a", "href", new String[]{"ftp", "http", "https", "mailto"});
  }

  @Test
  public void bigThree() {
    final MutableWhitelistConfiguration o1 = new BasicWhitelistConfiguration()
      .allowTag("a")
      .allowAttribute("blockquote", "cite")
      .enforceAttribute("a", "rel", "nofollow")
      .allowProtocol("a", "href", "mailto");

    final MutableWhitelistConfiguration o2 = new BasicWhitelistConfiguration()
      .allowTag("a")
      .allowAttribute("blockquote", "cite")
      .enforceAttribute("a", "rel", "nofollow")
      .allowProtocol("a", "href", "mailto");

    final MutableWhitelistConfiguration o3 = new BasicWhitelistConfiguration()
      .allowTag("b")
      .allowAttribute("blockquote", "cite")
      .enforceAttribute("a", "rel", "nofollow")
      .allowProtocol("a", "href", "mailto");

    assertThat(o1.equals(o1), is(true));
    assertThat(o1.equals(null), is(false));
    assertThat(o1.equals("non config"), is(false));

    assertThat(o1, is(equalTo(o2)));
    assertThat(o1.hashCode(), is(o2.hashCode()));
    assertThat(o1.toString(), is(o2.toString()));

    assertThat(o1, is(not(o3)));
    assertThat(o1.hashCode(), is(not(o3.hashCode())));
    assertThat(o1.toString(), is(not(o3.toString())));
  }

  @Test
  public void whitelist() {

    final Whitelist whitelist = new BasicWhitelistConfiguration()
      .base("none")
      .allowTag("a")
      .allowTag("b")
      .allowAttribute("blockquote", "cite")
      .enforceAttribute("a", "rel", "nofollow")
      .allowProtocol("a", "href", "ftp")
      .allowProtocol("a", "href", "http")
      .allowProtocol("a", "href", "https")
      .allowProtocol("a", "href", "mailto")
      .whitelist();

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
