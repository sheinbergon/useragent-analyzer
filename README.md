# useragent-analyzer

A non-opinionated User-Agent analysis proxy written in Java 8.

Key concepts :
- Provides both synchronous & fully asynchronous APIs.  
- Pluggable architecture for both caching & analysis modules.

Current implementations specs:
- Analysis is achieved using [ua-parser.js](https://github.com/faisalman/ua-parser-js)
- Javascript engine is [J2V8](https://github.com/eclipsesource/J2V8)
- Caching layer is implemented on top of [caffeine](https://github.com/ben-manes/caffeine)

Future plans (by order of importance):
- Add Windows/Mac support(for j2v8)
- Add Maven releases
- Add Unit Tests
- Add more cache implementations 
- Add [UA-Parser.org](http://www.uaparser.org/) support(not to be confused with ua-parser-js) support and make analyzer implementation/depedency inclusion configurable externally