package ar.edu.itba.pod.client;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;

import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;

@FunctionalInterface
public interface Query {
    void execute(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Path queryOut, final Path timeOut) throws IOException;
}
