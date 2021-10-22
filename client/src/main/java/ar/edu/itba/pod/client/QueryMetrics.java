package ar.edu.itba.pod.client;

import java.time.Instant;

public final class QueryMetrics {

    public final Instant inputProcessingStart;
    public final Instant inputProcessingEnd;
    public final Instant mapReduceJobStart;
    public final Instant mapReduceJobEnd;

    public static Builder build() {
        return new Builder();
    }

    private QueryMetrics(final Builder builder) {
        inputProcessingStart    = builder.inputProcessingStart;
        inputProcessingEnd      = builder.inputProcessingEnd;
        mapReduceJobStart       = builder.mapReduceJobStart;
        mapReduceJobEnd         = builder.mapReduceJobEnd;
    }

    public static final class Builder {
        public Instant inputProcessingStart;
        public Instant inputProcessingEnd;
        public Instant mapReduceJobStart;
        public Instant mapReduceJobEnd;

        private Builder() {
            // builder
        }

        public QueryMetrics build() {
            return new QueryMetrics(this);
        }

        public Builder withInputProcessingStart(final Instant inputProcessingStart) {
            this.inputProcessingStart = inputProcessingStart;
            return this;
        }
        public Builder recordInputProcessingStart() {
            return withInputProcessingStart(Instant.now());
        }

        public Builder withInputProcessingEnd(final Instant inputProcessingEnd) {
            this.inputProcessingEnd = inputProcessingEnd;
            return this;
        }
        public Builder recordInputProcessingEnd() {
            return withInputProcessingEnd(Instant.now());
        }

        public Builder withMapReduceJobStart(final Instant mapReduceJobStart) {
            this.mapReduceJobStart = mapReduceJobStart;
            return this;
        }
        public Builder recordMapReduceJobStart() {
            return withMapReduceJobStart(Instant.now());
        }

        public Builder withMapReduceJobEnd(final Instant mapReduceJobEnd) {
            this.mapReduceJobEnd = mapReduceJobEnd;
            return this;
        }
        public Builder recordMapReduceJobEnd() {
            return withMapReduceJobEnd(Instant.now());
        }
    }
}
