# useragent-analyzer
[![Build Status](https://travis-ci.org/sheinbergon/governator-vault.svg?branch=master)](https://travis-ci.org/sheinbergon/useragent-analyzer)

A non-opinionated User-Agent analysis proxy written in Java 8.

## Key concepts
- Provides both synchronous & fully asynchronous APIs.  
- Pluggable architecture for both caching & procesing(parsing) modules.

## Getting Started

### Dependencies

Simply add the 'useragent-analyzer-dist' dependency.

Note that this distribution jar relies on the user providing the j2v8 jar for the targeted architecture.
The following examples targets 64 bit linux execution environments, but other operating systems are
supported as well. See [Maven Central](https://mvnrepository.com/artifact/com.eclipsesource.j2v8) for additional information. 

#### Maven:

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
#### Gradle :

```groovy
    compile group: 'com.eclipsesource.j2v8', name: 'j2v8_linux_x86_64', version: '4.8.0'
    compile group: 'org.sheinbergon', name: 'useragent-analyzer-dist', version: '0.0.1'
```
### Simple setup
##### Setting-up a synchronous analyzer
```java
 UserAgentAnalyzer analyzer = UserAgentAnalyzer.builder()
                .processor(UaParserJsProcessor.builder().build())
                .build();

UserAgentIngredients ingredients = analyzer.analyze("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1")
ingredients.getOsName() // iOS
ingredients.getDeviceType() // MOBILE
```
##### Setting-up an asynchronous analyzer
```java
AsyncUserAgentAnalyzer analyzer = AsyncUserAgentAnalyzer.builder()
                .processor(UaParserJsAsyncProcessor.builder().build())
                .build();

analyzer.analyze("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1")
                .thenApply(UserAgentIngredients::getDeviceModel)
                ...               
```
##### Lifecycle management/shutdown
```java
analyzer.teardown();
```

### Advanced setup

See Implementation Specs below


## Implemntation Specs
### Processors
#### ua-paresr-js
- Uses the popular javascript useragent parsing library [ua-parser.js](https://github.com/faisalman/ua-parser-js)
- Javascript code is executed uses [J2V8](https://github.com/eclipsesource/J2V8)
- Requires an explicit J2V8 dependency defintion per operating system/architecture. See supported architectures [here](https://mvnrepository.com/artifact/com.eclipsesource.j2v8)
- As V8Engine are not thread-safe, it uses an object-pool to support concurrency with confiugrable size allocation timeout (milliseconds)
- Async version also supports executor pools size for V8 Engine allocation and ingestion
```java
// Sync
UserAgentAnalyzer.builder()
    .processor(UaParserJsProcessor.builder()
        .v8AllocationTimeout(1000L)
        .v8PoolSize(5)
        .build())
    ...

// Async
AsyncUserAgentAnalyzer.builder()
    .processor(UaParserJsAsyncProcessor.builder()           
        .ingestionConcurrency(4)
        .v8AllocationConcurrency(16)
        .v8AllocationTimeout(1000L)
        .v8PoolSize(5)
        .build())
    ...
```

### Caches
#### psuedo
- Default cache implementation, never caches anything (always misses)
#### caffeine
- Caching module implemented on top of [caffeine](https://github.com/ben-manes/caffeine)
- Supports max entries configuration
```java
// Sync
UserAgentAnalyzer.builder()
    .cache(CaffeineCache.builder()
        .maxEntries(100000)
        .build())
    ...

// Async
AsyncUserAgentAnalyzer.builder()
    .cache(CaffeineAsyncCache.builder()
        .maxEntries(100000)
        .build())
    ...
```

## Future plans:
- Add concurrency tests 
- Add more cache implementations 
- Add more parser implementations (suggestions are welcomed!)
