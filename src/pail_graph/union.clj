(ns pail-graph.union
  "Functions for working with Thrift unions."
  (:require [clj-thrift.union]
            [potemkin :as pt]))

; bring in clj-thrift.union functionality. (ns-publics 'clj-thrift.union)
(pt/import-vars [clj-thrift.union
                    current-field-id
                    current-field-name
                    current-value] )
