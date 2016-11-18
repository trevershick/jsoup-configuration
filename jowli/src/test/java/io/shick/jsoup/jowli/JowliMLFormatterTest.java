package io.shick.jsoup.jowli;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import io.shick.jsoup.BasicWhitelistConfiguration;
import io.shick.jsoup.WhitelistConfiguration;

import java.text.ParseException;

import org.junit.Test;

public class JowliMLFormatterTest {

  @Test(expected = NullPointerException.class)
  public void nullThrowsException() {
    new JowliMLFormatter().format(null);
  }

  @Test
  public void fullCircleWithNothing() throws ParseException {
    WhitelistConfiguration wlc = new BasicWhitelistConfiguration();
    final String jowliml = new JowliMLFormatter().format(wlc).toString();
    assertThat(jowliml, is(""));
    final WhitelistConfiguration wlc2 = new JowliMLParser().parse(jowliml);
    assertThat(wlc, is(wlc2));
  }

  @Test
  public void fullCircleWithTags() throws ParseException {
    WhitelistConfiguration wlc = new BasicWhitelistConfiguration()
      .allowTag("blockquote")
      .allowTag("b");
    final String jowliml = new JowliMLFormatter().format(wlc).toString();
    assertThat(jowliml, is("t:blockquote,b"));
    final WhitelistConfiguration wlc2 = new JowliMLParser().parse(jowliml);
    assertThat(wlc, is(wlc2));
  }

  @Test
  public void fullCircleWithAllowedAttributes() throws ParseException {
    WhitelistConfiguration wlc = new BasicWhitelistConfiguration()
      .allowAttribute("blockquote", "cite")
      .allowAttribute("a", "href");
    final String jowliml = new JowliMLFormatter().format(wlc).toString();
    assertThat(jowliml, is("a:a[href],blockquote[cite]"));
    final WhitelistConfiguration wlc2 = new JowliMLParser().parse(jowliml);
    assertThat(wlc, is(wlc2));
  }

  @Test
  public void fullCircleWithEnforcedAttributes() throws ParseException {
    WhitelistConfiguration wlc = new BasicWhitelistConfiguration()
      .enforceAttribute("a", "rel", "nofollow");
    final String jowliml = new JowliMLFormatter().format(wlc).toString();
    assertThat(jowliml, is("e:a[rel:nofollow]"));
    final WhitelistConfiguration wlc2 = new JowliMLParser().parse(jowliml);
    assertThat(wlc, is(wlc2));
  }

  @Test
  public void fullCircleWithProtocols() throws ParseException {
    final WhitelistConfiguration wlc = new BasicWhitelistConfiguration()
      .allowProtocol("a", "href", "mailto");

    final String jowliml = new JowliMLFormatter().format(wlc).toString();
    final WhitelistConfiguration wlc2 = new JowliMLParser().parse(jowliml);

    assertThat(jowliml, is("p:a[href:[mailto]]"));
    assertThat(wlc, is(wlc2));
  }

  @Test
  public void formatJustBase() throws ParseException {
    final String jowliml = "b:r";

    verifyInAndOutAreSame(jowliml);
  }

  @Test
  public void formatJustTags() throws ParseException {
    final String jowliml = "t:a,b";

    verifyInAndOutAreSame(jowliml);
  }

  @Test
  public void formatAll() throws ParseException {
    final String jowliml
      = "b:n;t:a,b;a:a[href,rel],blockquote[cite];e:a[rel:nofollow,x:y];p:a[z:[d],href:[ftp,http,https]]";

    verifyInAndOutAreSame(jowliml);
  }

  @Test
  public void formatJustProtocols() throws ParseException {
    final String jowliml = "p:a[z:[d],href:[ftp,http,https]]";

    verifyInAndOutAreSame(jowliml);
  }

  @Test
  public void formatJustAllowedAttributes() throws ParseException {
    final String jowliml = "a:a[href,rel],blockquote[cite]";

    verifyInAndOutAreSame(jowliml);
  }

  @Test
  public void formatJustEnforcedAttributes() throws ParseException {
    final String jowliml = "e:a[rel:nofollow,x:y]";

    verifyInAndOutAreSame(jowliml);
  }

  private void verifyInAndOutAreSame(String jowliml) throws ParseException {
    final WhitelistConfiguration config = new JowliMLParser().parse(jowliml);
    final String actual = new JowliMLFormatter().format(config).toString();

    assertThat(actual, is(equalTo(jowliml)));

    final WhitelistConfiguration config2 = new JowliMLParser().parse(actual);
    assertThat(config, is(config2));
  }
}
