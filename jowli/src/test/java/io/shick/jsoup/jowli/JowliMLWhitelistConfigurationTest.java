package io.shick.jsoup.jowli;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import io.shick.jsoup.WhitelistConfiguration;

import java.text.ParseException;

import org.junit.Test;

/**
 * Created by tshick on 11/11/16.
 */
public class JowliMLWhitelistConfigurationTest {

  @Test
  public void toStringFormatsJowli() throws ParseException {
    String text = "t:a,b,c;a:dog[a,b,c]";
    final WhitelistConfiguration c = new JowliMLParser().parse(text);
    
    assertThat(c.toString(), is(new JowliMLFormatter().format(c)));
  }
}
