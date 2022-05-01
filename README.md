# nouveau
Lucene 9 + DropWizard = Maybe a good search option for Apache CouchDB?

Nouveau is an experimental search extension for CouchDB 3.x.

What works? Not much yet but the basic shape is there. You can search and get the hits but
there's no support for facets, etc.

## Erlang side

there's some essential Erlang plumbing in https://github.com/rnewson/couchdb-nouveau


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

curl -X PUT "$URL/_design/foo" -d '{"indexes":{"bar":{"analyzer":"standard", "index":"function(doc) { index(\"foo\", \"bar\", {\"store\":true}); index(\"bar\", doc.bar, {\"store\":true,\"facet\":true}); }"}}}'

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
(`sort=["fieldnamehere<string>"] or sort=["fieldnamehere<number>"],
defaulting to number).


