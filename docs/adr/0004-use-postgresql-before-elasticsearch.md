# ADR 0004: Use PostgreSQL Before Elasticsearch

## Status

Accepted

## Context

Product search is required, but the first priority is a working core purchase flow. Introducing Elasticsearch too early would add operational complexity before the domain and APIs are stable.

PostgreSQL is already required as the source-of-truth database.

## Decision

Use PostgreSQL for initial product search and defer Elasticsearch to a later phase.

Elasticsearch may be introduced after the core purchase flow works and search requirements justify it.

## Consequences

Benefits:

- Reduces early infrastructure complexity.
- Keeps local development simpler.
- Allows the catalog model and search requirements to evolve before adding a search engine.

Tradeoffs:

- Advanced search relevance and indexing features are deferred.
- A later migration may be needed for search projections.
- Search APIs should avoid exposing PostgreSQL-specific assumptions that would make future Elasticsearch adoption difficult.
