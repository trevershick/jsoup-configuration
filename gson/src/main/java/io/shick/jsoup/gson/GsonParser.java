package io.shick.jsoup.gson;

import io.shick.jsoup.WhitelistConfiguration;
import io.shick.jsoup.WhitelistConfigurationParser;
import io.shick.jsoup.WhitelistConfigurationParserFactory;

import java.text.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class GsonParser implements WhitelistConfigurationParser {
  @Override
  public WhitelistConfiguration parse(CharSequence value) throws ParseException {
    try {
      return gson().fromJson(value.toString(), GsonWhitelistConfiguration.class);
    } catch (JsonSyntaxException jse) {
      throw new ParseException(jse.getMessage(),0);
    }
  }

  private Gson gson() {
    return new GsonBuilder().create();
  }
  
  static {
    WhitelistConfigurationParserFactory.register("gson", GsonParser::new);
  }
}
