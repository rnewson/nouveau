# nouveau
Lucene 9 + DropWizard = Maybe a good search option for Apache CouchDB?

Nouveau is an experimental search extension for CouchDB 3.x.

## What works?

* you can define a default analyzer and different analyzers by field name.
* sorting on text and numbers
* classic lucene query syntax
* count and range facets
* cursor support for paginating efficiently through large results sets
* indexes automatically deleted if database is deleted (as long as nouveau is running!)
* integration with ken

## What doesn't work yet?

* No support for results grouping
* No support for stale=ok
* No integration with mango
* No support to configure stop words for analyzers

## Why is this better than dreyfus/clouseau?

* No scalang (or Scala!)
* Supports any version of Java that Lucene 9 supports
* memory-mapped I/O for performance
* direct I/O used for segment merging (so we don't evict useful data from disk cache)
* It's new and shiny.

## Erlang side

You'll need to run a fork of couchdb: https://github.com/rnewson/couchdb-nouveau

## Getting started

Build Nouveau with;

`mvn package`

Run Nouvea with;

`java -jar target/nouveau-1.0-SNAPSHOT.jar server nouveau.yaml`

Now run CouchDB using the 'nouveau' branch of my fork at https://github.com/rnewson/couchdb-nouveau;

`make && dev/run --admin=foo:bar`

Make a database with some data and an index definition;

```
#!/bin/sh

URL="http://foo:bar@127.0.0.1:15984/foo"

curl -X DELETE "$URL"
curl -X PUT "$URL?n=3&q=16"

curl -X PUT "$URL/_design/foo" -d '{"indexes":{"bar":{"default_analyzer":"standard", "field_analyzers":{"foo":"english"}, "index":"function(doc) { index(\"foo\", \"bar\", {\"store\":true}); index(\"bar\", doc.bar, {\"store\":true,\"facet\":true}); }"}}}'

for I in {1..100}; do
    DOCID=$RANDOM
    DOCID=$[ $DOCID % 100000 ]
    BAR=$RANDOM
    BAR=$[ $BAR % 100000 ]
    curl -X PUT "$URL/doc$DOCID" -d "{\"bar\": $BAR}"
done
```

In order not to collide with `dreyfus` I've hooked Nouveau in with some uglier paths for now;

`curl 'foo:bar@localhost:15984/foo/_design/foo/_nsearch/bar?q=*:*'`

This will cause Nouveau to build indexes for each copy (N) and each
shard range (Q) and then perform a search and return the results. Lots
of query syntax is working as is sorting on strings and numbers
(`sort=["fieldnamehere&lt;string&gt;"] or sort=["fieldnamehere&lt;number&gt;"],
defaulting to number).

Facet support

Counts of string fields and Ranges for numeric fields;

```
curl 'foo:bar@localhost:15984/foo/_design/foo/_nsearch/bar?q=*:*&limit=1&ranges={"bar":[{"label":"cheap","min":0,"max":100}]}&counts=["foo"]' -g
```
