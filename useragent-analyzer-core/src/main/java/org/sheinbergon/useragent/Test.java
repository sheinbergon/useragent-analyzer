package org.sheinbergon.useragent;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import org.sheinbergon.useragent.analyzer.Analyzer;
//import org.sheinbergon.useragent.analyzer.AsyncAnalyzer;
//import org.sheinbergon.useragent.cache.impl.AsyncPseudoCache;
//
//import java.io.IOException;
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//
//public class Test {
//
//    public static void main(String[] args) throws Exception {
//
//        ObjectWriter jackson = new ObjectMapper().writer();
//
//        Analyzer<?> analyzer = Analyzer.Builders.uaParserJs().build();
//        AsyncAnalyzer<?> asyncAnalyzer = AsyncAnalyzer.Builders.uaParserJs().cache(
//                AsyncPseudoCache.builder().build()
//        ).build();
//
//        try {
//                .cache(Cache
//                        .Builders
//                        .caffeine()
//                        .maxEntries(20)
//                        .build())
//                .build();
//
//
//            for (int index = 0; index < 40; index++) {
//
//                final int p = index;
//
//                System.out.println(p + "-SYNC-" + jackson.writeValueAsString(analyzer.analyze("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")));
//
//                asyncAnalyzer.analyze("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
//                        .whenComplete((i, t) -> {
//                            try {
//                                Optional.ofNullable(t).ifPresent(Throwable::printStackTrace);
//                                System.out.println(p + "-ASYNC-" + jackson.writeValueAsString((i)));
//                            } catch (IOException iox) {
//                                t.printStackTrace();
//                            }
//                        }).get(6, TimeUnit.SECONDS);
//            }
//        } finally {
//            analyzer.teardown();
//            asyncAnalyzer.teardown();
//        }
//    }
//}