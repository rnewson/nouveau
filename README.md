# nouveau
Lucene 9.10 + DropWizard = Maybe a good search option for Apache CouchDB?

Nouveau is an experimental search extension for CouchDB 3.x.

What works? Not much yet but the basic shape is there. You can search and get the hits but
there's no proper merging yet, no support for facets, etc.


## Erlang side

there's some essential Erlang plumbing in https://github.com/rnewson/couchdb-nouveau
