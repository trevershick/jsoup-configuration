# jsoup-configuration


[![Maven Status](https://maven-badges.herokuapp.com/maven-central/io.shick.jsoup/jsoup-configuration/badge.svg?style=flat)](http://mvnrepository.com/artifact/com.github.javafaker/javafaker)
[![Build Status](https://travis-ci.org/trevershick/jsoup-configuration.svg?branch=master)](https://travis-ci.org/trevershick/jsoup-configuration)
[![Coverage Status](https://coveralls.io/repos/github/trevershick/jsoup-configuration/badge.svg?branch=master)](https://coveralls.io/github/trevershick/jsoup-configuration?branch=master)
[![Dependency Status](https://www.versioneye.com/user/projects/58253d34613b6801fb37e874/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/58253d34613b6801fb37e874)
[![License](http://img.shields.io/:license-mit-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)


This library was born from the need to externalize JSoup's HTML Whitelist.  Deploying code
for constant tweaks to the Whitelist's configuration was cumbersome.  This library allows
me to update the configuration outside the code (in JSON or JowliML).

Usage
-----
Pick your flavor, using JSON or JowliML and include the appropriate dependency.

In pom.xml, add the following:

```xml
<dependency>
    <groupId>io.shick.jsoup</groupId>
    <artifactId>jsoup-configuration-gson</artifactId>
    <version>1.0.1</version>
</dependency>
```


Then in your Java code

```java

final Whitelist whitelist = GsonWhitelistConfiguration.fromJson(json).whitelist();

```

Formats
----

JSON
```json
{
  "tags" : ["a","b"],
  "attributes" : {
    "blockquote": ["cite"]
    },
  "enforcedAttributes": {
    "a" : {
      "rel" : "nofollow"
      }
    },
  "protocols" : {
    "a" : { 
      "href":["ftp", "http", "https", "mailto"]
      }
    }
}
```

**JowliML**

The point of JowliML is to provide a very terse representation of the whitelist rules.
What you see below is the same as the above JSON but in a much more compact,
externalized configuration friendly format.


```
(all on one line)
t:a,b;
a:blockquote[cite],a[href,rel];
e:a[rel:nofollow];
p:a[href:[ftp,http,https,mailto]]
```
LICENSE
-------
MIT
