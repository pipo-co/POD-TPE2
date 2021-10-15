package ar.edu.itba.pod.client;

import java.io.IOException;
import java.nio.file.Path;

import com.hazelcast.core.HazelcastInstance;

@FunctionalInterface
public interface Query {
    void execute(
        final HazelcastInstance hazelcast,
        final Path treeCsv, final Path hoodCsv,
        final Path queryOut, final Path timeOut) throws IOException;
}
