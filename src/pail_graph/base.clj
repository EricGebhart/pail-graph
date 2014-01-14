(ns pail-graph.base
  (:require [pail-graph.type :as ttype]
            [pail-graph.union :as tunion]
            [potemkin :as pt]
            [clj-thrift.base]))

; pull in clj-thrift base functionality. (ns-publics 'clj-thrift.base)
(pt/import-vars [clj-thrift.base
                    value!
                    build
                    value] )

; simple data extraction
(defn property-value
  "get the named field value from the current structure in the top level union.
   Union -> struct :<field-name> - value."
  [object field-name]
  (value (tunion/current-value object) field-name))

(defn property-union-value
  "get a value from a union, inside a struct inside a union.
   name is the property name inside the struct.
   Union -> Struct :<field-name> -> Union - value."
  [object field-name]
  (tunion/current-value (property-value object field-name)))

(defn field-keys
  "Give back an ordered vector of field keys for a struct or union."
  [object]
    (mapv #(keyword (:name %)) (ttype/field-meta-list (type object))))
