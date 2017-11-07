# useragent-analyzer
[![Build Status](https://travis-ci.org/sheinbergon/governator-vault.svg?branch=master)](https://travis-ci.org/sheinbergon/useragent-analyzer)

A non-opinionated User-Agent analysis proxy written in Java 8.

## Key concepts :
- Provides both synchronous & fully asynchronous APIs.  
- Pluggable architecture for both caching & procesing(parsing) modules.

## Dependencies :

Simply add the 'useragent-analyzer-dist' dependency.

Note that this distribution jar relies on the user providing the j2v8 jar for the targeted architecture.
The following examples targets 64 bit linux execution environments, but other operating systems are
supported as well. See [Maven Central](https://mvnrepository.com/artifact/com.eclipsesource.j2v8) for additional information. 

Maven:

```xml
<dependencies>
    ...
    <dependency>
        <groupId>org.sheinbergon</groupId>
        <artifactId>useragent-analyzer-dist</artifactId>
        <version>0.0.1</version>
    </dependency>
    <dependency>
        <groupId>com.eclipsesource.j2v8</groupId>
        <artifactId>j2v8_linux_x86_64</artifactId>
        <version>4.8.0</version>
    </dependency>    
    ...
</dependencies>

```
Gradle :

```groovy
    compile group: 'com.eclipsesource.j2v8', name: 'j2v8_linux_x86_64', version: '4.8.0'
    compile group: 'org.sheinbergon', name: 'useragent-analyzer-dist', version: '0.0.1'
```

## Getting Started :

Setting up a synchronous analyzer :

```java
 UserAgentAnalyzer analyzer = UserAgentAnalyzer.builder()
                .processor(UaParserJsProcessor.build())
                .cache(CaffeineCache.builder().build())
                .build();

UserAgentIngredients ingredients = analyzer.analyze("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1")
ingredients.getOsName() // iOS
ingredients.getDeviceType() // MOBILE
```

Or, if you require asynchronous access :

```java
AsyncUserAgentAnalyzer analyzer = AsyncUserAgentAnalyzer.builder()
                .processor(UaParserJsAsyncProcessor.builder().build())
                .cache(CaffeineAsyncCache.builder().build())
                .build();

analyzer.analyze("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1")
                .thenCompose(UserAgentIngredients::getDeviceModel)
                ...
```

## Implemntation Specs
### Processors
#### ua-paresr-js
- Uses the popular javascript useragent parsing library [ua-parser.js](https://github.com/faisalman/ua-parser-js)
- Javascript code is executed uses [J2V8](https://github.com/eclipsesource/J2V8)
- Requires an explicit J2V8 dependency defintion per operating system/architecture. See supported architectures [here](https://mvnrepository.com/artifact/com.eclipsesource.j2v8) 

### Caches
#### caffeine
- Caching module implemented on top of [caffeine](https://github.com/ben-manes/caffeine)

## Future plans:
- Add more cache implementations 
- Add [UA-Parser.org](http://www.uaparser.org/) support(not to be confused with ua-parser-js) support and make analyzer implementation/depedency inclusion configurable externally