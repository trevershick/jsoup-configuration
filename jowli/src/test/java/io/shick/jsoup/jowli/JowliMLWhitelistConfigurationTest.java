package io.shick.jsoup.jowli;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import io.shick.jsoup.WhitelistConfiguration;

import java.text.ParseException;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.junit.Test;

public class JowliMLWhitelistConfigurationTest {

  @Test
  public void toStringFormatsJowli() throws ParseException {
    String text = "b:n;t:a,b,c;a:dog[a,b,c]";
    final WhitelistConfiguration c = new JowliMLParser().parse(text);

    assertThat(c.toString(), is(new JowliMLFormatter().format(c)));
  }

  @Test
  public void baseCanBeSpecified() throws ParseException {
    String text = "b:b"; // use 'basic'
    final WhitelistConfiguration c = new JowliMLParser().parse(text);

    Whitelist wl = c.whitelist();
    Whitelist basic = Whitelist.basic();
    String html = "<a href=\"http://x\">To X</a>";
    assertThat("Basic will add rel=nofollow to links.",
      Jsoup.clean(html, wl),
      is(Jsoup.clean(html, basic)));
    assertThat("Basic will add rel=nofollow to links.",
      Jsoup.clean(html, wl),
      containsString("nofollow"));


    assertThat(c.toString(), is(new JowliMLFormatter().format(c)));
  }

  @Test
  public void rootAdditivity() throws ParseException {
    String text = "b:i;t:a,b,c;a:dog[a,b,c]";
    String textAdditive = "b:n;b:i;t:a;t:b;t:c;a:dog[a];a:dog[b];a:dog[c]";

    String jowli = new JowliMLParser().parse(text).toString();
    String jowliAdditive = new JowliMLParser().parse(textAdditive).toString();

    assertThat(jowli, is(jowliAdditive));
  }

}
