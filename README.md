# pail-graph


A library to help with Serialization, Pail Partitioning and Cascalog use with a Graph Schema.
Many thanks to the work of David Cuddeback. This library is a specialized wrapper around his more generic libraries.
[Thrift](https://thrift.apache.org/).
[Pail](https://github.com/nathanmarz/dfs-datastores)
[Cascalog 2] (https://github.com/nathanmarz/cascalog)
Based on the work of [David Cuddeback](https://github.com/dcuddeback/)
[`clj-thrift`](https://github.com/dcuddeback/clj-thrift
[`clj-pail`](https://github.com/dcuddeback/clj-pail).
[`pail-thrift`](https://github.com/dcuddeback/pail-thrift)
[`pail-cascalog`](https://github.com/dcuddeback/pail-cascalog)

This library extends clj-thrift with functionality to aid in the extraction of data from
Graph Schema/thrift objects. It extends clj-pail by adding tap mapping functionality to the PailStructure.
It extends Pail Cascalog a few functions that make it easy to get taps from a Vertically Partitioned Pail.

## Usage

There is a comprehensive [working example here.](https://github.com/EricGebhart/pail-graph/blob/master/src/example/example.clj)

Add `pail-graph` to your project's dependencies. If you're using Leiningen, your `project.clj`
should look something like this:

~~~clojure
(defproject ...
  :dependencies [[pail-graph VERSION]])
~~~

Where `VERSION` is the latest version on [Clojars](https://clojars.org/pail-graph).

### Defining a `PailStructure`

This is the same as the clj-pail PailStructure with an additional entry.

`PailStructure` classes are defined with the `gen-structure` macro from `pail-graph`.
`pail-thrift` provides serializers and partitioners that can be used with the `gen-structure` macro.
`pail-graph` provides additional partitioners that could be useful or at the very least useful models
for new partitioners. `pail-graph` also provides tap mappers which match all the partitioners provided
with `pail-thrift` and `pail-graph`

~~~clojure
(ns example.pail
  (:require [pail-graph.structure :refer [gen-structure]]
            [pail-thrift.serializer :as s]
            [pail-thrift.partitioner :as p]
            [pail-graph.partitioner :as pg]
            [pail-graph.tapmapper :as t])
  (:import (example.thrift DataUnit))
  (:gen-class))

(gen-structure example.pail.PailStructure
               :type DataUnit
               :serializer (s/thrift-serializer DataUnit)
               :partitioner (p/union-partitioner DataUnit))
               :tapmapper (t/union-tap-mapper))
~~~

In the above example, we define a `PailStructure` that serializes the `example.thrift.DatUnit` type
using the default Thrift serialization protocol. The `PailStructure` will also be vertically
partitioned by the active field of each union.

#### Controlling the Serialization Protocol

The previous example uses the default Thrift serialization protocol. The protocol can be specified
as an additional argument to the `thrift-serializer` function. The protocols are defined in
[`clj-thrift`](https://github.com/dcuddeback/clj-thrift):

~~~clojure
(require '[clj-thrift.protocol.factory :as protocol])

(s/thrift-serializer DataUnit (protocol/compact))
~~~

#### Vertical Partitioning

A `PailStructure` is vertically partitioned according to the partitioner supplied as the
`:partitioner` keyword argument of `gen-structure`. `pail-thrift` provides 2 generic partitioners,
pail-graph provides 2 more that are more specific to Graph Schema models.
But you may want the partitioner to be specific to your application. Creating your own partitoner,
tapmapper and PailStructure is encouraged.

Generalized partitioners are defined in
[`pail-thrift.partitioner`](https://github.com/dcuddeback/pail-thrift/blob/master/src/clojure/pail_thrift/partitioner.clj).
Currently, there are 2 partitioners.
Both partition data based on the fields in a union. One partitioner uses field ids the other uses field names.
[`pail-graph.partitioner`](https://github.com/EricGebhart/pail-graph/blob/master/src/pail_graph/partitioner.clj). There are two partitioners supplied with
pail-graph. Both Look for `property` fields to provide a two level partitioning scheme. One uses field id's the
other uses field names. They may work for you,however it is quite likely you will want to create your own.

Generalized tap mappers are defined in
[`pail-graph.tapmapper`](https://github.com/EricGebhart/pail-graph/blob/master/src/pail_graph/tapmapper.clj) There are 5 tap mappers counting the null default.
Each of them corresponds to one of the four partitioners mentioned above. If you desire the TapMapper functionality,
and you write a custom partitioner you will also need to create a tapmapper function to match.

## License

Copyright © 2014 Eric Gebhart

Distributed under the [MIT License](LICENSE).
