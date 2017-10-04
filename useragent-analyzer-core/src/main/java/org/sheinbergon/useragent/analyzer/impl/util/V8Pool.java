package org.sheinbergon.useragent.analyzer.impl.util;

import com.eclipsesource.v8.V8;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.sheinbergon.useragent.analyzer.impl.exception.V8PoolSetupException;
import org.sheinbergon.useragent.analyzer.impl.exception.V8ResourceAllocationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

/**
 * @author Idan Sheinberg
 */
@RequiredArgsConstructor
public class V8Pool {

    private boolean initialized = false;
    private GenericObjectPool<V8> pool;

    private final V8Factory factory = new V8Factory();
    private final String[] scripts;
    private final GenericObjectPoolConfig poolConfig;

    public final synchronized void setup() {
        if (!initialized) {
            pool = new GenericObjectPool<>(factory, poolConfig);
            initialized = true;
        } else {
            throw new IllegalStateException("V8 Runtime Pool has already been initiailized");
        }
    }

    public final synchronized void teardown() {
        if (initialized) {
            pool.close();
            initialized = false;
        } else {
            throw new IllegalStateException("V8 runtime pool hasn't been initiailized");
        }
    }

    public final V8 allocate() throws V8ResourceAllocationException {
        try {
            return pool.borrowObject();
        } catch (Exception x) {
            throw new V8ResourceAllocationException("Couldn't allocate V8 runtime instance", x);
        }
    }

    public final V8 allocate(long allocateTimeoutMs) throws V8ResourceAllocationException {
        try {
            return pool.borrowObject(allocateTimeoutMs);
        } catch (Exception x) {
            throw new V8ResourceAllocationException("Couldn't allocate V8 runtime instance(waiting for " + allocateTimeoutMs + " at most)", x);
        }
    }

    public final void release(V8 v8Runtime) {
        pool.returnObject(v8Runtime);
    }

    private class V8Factory extends BasePooledObjectFactory<V8> {

        @Override
        public V8 create() throws Exception {
            V8 v8 = V8.createV8Runtime();
            // Read passed javascripts paths to each V8 runtime instantiated 
            for (String script : scripts) {
                v8.executeVoidScript(script);
            }
            // Release the runtime locker from the current thread
            v8.getLocker().release();
            return v8;
        }

        @Override
        public PooledObject<V8> wrap(V8 v8Runtime) {
            return new DefaultPooledObject<>(v8Runtime);
        }
    }


    public static V8Pool create(int runtimeInstances, String[] scriptPaths) throws V8PoolSetupException {
        try {
            GenericObjectPoolConfig v8PoolConfig = new GenericObjectPoolConfig();
            v8PoolConfig.setMaxIdle(runtimeInstances);
            v8PoolConfig.setMaxTotal(runtimeInstances);
            v8PoolConfig.setMinIdle(runtimeInstances);
            v8PoolConfig.setBlockWhenExhausted(true);

            String[] scripts = Stream.of(scriptPaths)
                    .map(path -> {
                        try {
                            return IOUtils.toString(UaParserJsUtils.class.getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
                        } catch (IOException iox) {
                            throw new RuntimeException(iox);
                        }
                    }).toArray(String[]::new);

            V8Pool v8Pool = new V8Pool(scripts, v8PoolConfig);
            v8Pool.setup();
            return v8Pool;
        } catch (RuntimeException x) {
            throw new V8PoolSetupException("Could not setup V8 runtime pool");
        }
    }
}