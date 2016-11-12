package io.shick.jsoup.gson;

import static java.util.Objects.requireNonNull;

import io.shick.jsoup.WhitelistConfiguration;
import io.shick.jsoup.WhitelistConfigurationFormatter;
import io.shick.jsoup.util.Func;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonFormatter implements WhitelistConfigurationFormatter {
  @Override
  public CharSequence format(WhitelistConfiguration configuration) {
    return gson().toJson(requireNonNull(configuration, "configuration cannot be null"));
  }

  Gson gson() {
    return new GsonBuilder()
      .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter())
      .registerTypeHierarchyAdapter(Map.class, new MapAdapter())
      .create();
  }

  static class CollectionAdapter implements JsonSerializer<Collection<?>> {
    @Override
    public JsonElement serialize(Collection<?> src, Type typeOfSrc, JsonSerializationContext context) {
      if (Func.empty(src)) {
        return null;
      }

      final JsonArray array = new JsonArray();
      src.forEach(value -> {
        array.add(context.serialize(value));
      });
      return array;
    }
  }

  static class MapAdapter implements JsonSerializer<Map<String, ?>> {
    @Override
    public JsonElement serialize(Map<String, ?> src, Type typeOfSrc, JsonSerializationContext context) {
      if (Func.empty(src)) {
        return null;
      }

      final JsonObject map = new JsonObject();

      src.forEach((k, v) -> {
        JsonElement value = context.serialize(v);
        map.add(k, value);
      });

      return map;
    }
  }
}
