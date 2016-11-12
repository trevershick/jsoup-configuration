package io.shick.jsoup.jowli;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import io.shick.jsoup.MutableWhitelistConfiguration;
import io.shick.jsoup.WhitelistConfigurationParserFactory;
import io.shick.jsoup.jowli.ast.AllowedAttributes;
import io.shick.jsoup.jowli.ast.AllowedTags;
import io.shick.jsoup.jowli.ast.ConfigConsumer;
import io.shick.jsoup.jowli.ast.EnforcedAttributes;
import io.shick.jsoup.jowli.ast.Protocols;

import java.text.ParseException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.junit.Test;

public class JowliMLParserTest {

  @Test(expected = ParseException.class)
  public void invalidSyntaxYieldsParseException() throws ParseException {
    WhitelistConfigurationParserFactory.newParser("jowli").parse("xxx");
  }

  @Test
  public void whitelist() throws ParseException {
    final StringBuilder jowliml = new StringBuilder()
      .append("t:a,b")
      .append(";")
      .append("a:blockquote[cite],a[href,rel]")
      .append(";")
      .append("e:a[rel:nofollow,x:y]")
      .append(";")
      .append("p:a[href:[ftp,http,https],z:[d]]");
    final Whitelist whitelist = new JowliMLParser().parse(jowliml.toString()).whitelist();

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

  @Test
  public void allowedTags() {

    final AllowedTags tags = JowliMLParser.ALLOWED_TAGS_DIRECTIVE.parse("t:a,b,c");

    MutableWhitelistConfiguration c = mock(MutableWhitelistConfiguration.class);
    tags.accept(c);

    assertThat(tags.toString(), is("[a, b, c]"));
    verify(c).allowTag("a");
    verify(c).allowTag("b");
    verify(c).allowTag("c");
    verifyNoMoreInteractions(c);
  }

  @Test
  public void allowedAttributes() {

    final AllowedAttributes o = JowliMLParser.ALLOWED_ATTRIBUTES_DIRECTIVE
      .parse("a:dog[a,b,c],cat[color,size]");

    MutableWhitelistConfiguration c = mock(MutableWhitelistConfiguration.class);
    o.accept(c);

    assertThat(o.toString(), is("[(dog, [a, b, c]), (cat, [color, size])]"));
    verify(c).allowAttribute("dog", "a");
    verify(c).allowAttribute("dog", "b");
    verify(c).allowAttribute("dog", "c");

    verify(c).allowAttribute("cat", "color");
    verify(c).allowAttribute("cat", "size");
    verifyNoMoreInteractions(c);
  }

  @Test
  public void enforcedAttributes() {

    final EnforcedAttributes o = JowliMLParser.ENFORCED_ATTRIBUTES_DIRECTIVE
      .parse("e:a[rel:nofollow,x:y],font[family:arial,size:bold]");

    MutableWhitelistConfiguration c = mock(MutableWhitelistConfiguration.class);
    o.accept(c);

    assertThat(o.toString(), is("[(a, [(rel, nofollow), (x, y)]), (font, [(family, arial), (size, bold)])]"));

    verify(c).enforceAttribute("a", "rel", "nofollow");
    verify(c).enforceAttribute("a", "x", "y");

    verify(c).enforceAttribute("font", "family", "arial");
    verify(c).enforceAttribute("font", "size", "bold");
    verifyNoMoreInteractions(c);
  }

  @Test
  public void protocols() {

    final Protocols o = JowliMLParser.PROTOCOLS_DIRECTIVE.parse("p:a[href:[ftp,http,https],b:[a]]");

    MutableWhitelistConfiguration c = mock(MutableWhitelistConfiguration.class);
    o.accept(c);

    final Protocols protocols = JowliMLParser.PROTOCOLS_DIRECTIVE.parse("p:a[href:[ftp,http,https],b:[a]]");
    assertThat(protocols.toString(), is("[(a, [(href, [ftp, http, https]), (b, [a])])]"));

    verify(c).allowProtocol("a", "href", "ftp");
    verify(c).allowProtocol("a", "href", "http");
    verify(c).allowProtocol("a", "href", "https");
    verify(c).allowProtocol("a", "b", "a");

    verifyNoMoreInteractions(c);
  }

  @Test
  public void root() {
    String text = "t:a,b,c;a:dog[a,b,c]";
    final List<ConfigConsumer> o = JowliMLParser.ROOT.parse(text);

    MutableWhitelistConfiguration c = mock(MutableWhitelistConfiguration.class);
    o.forEach(consumer -> consumer.accept(c));

    assertThat(o.toString(), is("[[a, b, c], [(dog, [a, b, c])]]"));
    verify(c).allowTag("a");
    verify(c).allowTag("b");
    verify(c).allowTag("c");

    verify(c).allowAttribute("dog", "a");
    verify(c).allowAttribute("dog", "b");
    verify(c).allowAttribute("dog", "c");

    verifyNoMoreInteractions(c);
  }
}
