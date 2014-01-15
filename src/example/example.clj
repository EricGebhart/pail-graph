(ns pail-graph.example
  (:require
   [pail-graph.type :as type]
   [pail-graph.base :as thrift]
   [pail-graph.core :as pail]
   )
  (:use cascalog.api)
  (:import [people DataUnit]
           [pail-graph DataUnitPailStructure]
           [pail-graph UnionPailStructure]))


(def du1-1 (thrift/build DataUnit {:property {:id "123"
                                              :property {:first_name "Eric"}}}))

(def du1-2 (thrift/build DataUnit {:property {:id "123"
                                              :property {:last_name "Gebhart"}}}))

(def  du1-3 (thrift/build DataUnit {:property {:id "123"
                                               :property { :location {:address "1 Pack Place"
                                                                      :city "Asheville"
                                                                      :state "NC"}}}}))

(def du2-1 (thrift/build DataUnit {:property {:id "abc"
                                              :property {:first_name "Frederick"}}}))

(def du2-2 (thrift/build DataUnit {:property {:id "abc"
                                              :property {:last_name "Gebhart"}}}))

(def  du2-3 (thrift/build DataUnit {:property {:id "abc"
                                               :property { :location {:address "1 Wall Street"
                                                                      :city "Asheville"
                                                                      :state "NC"}}}}))

(def du3 (thrift/build DataUnit {:friendshipedge {:id1 "123" :id2 "abc"}}))

(def objectlist [du1-1 du1-2 du1-3 du2-1 du2-2 du2-3 du3])


; simple property deconstruction; age, first_name, last_name, etc.
(defmapfn sprop [du]
    "Deconstruct a simple property object that only has an id and one value which
     is a union named 'property'."
     [(thrift/property-value du :id) (thrift/property-union-value du :property) ])

;location property deconstruction.
(defmapfn locprop
  "Deconstruct a property object, which has an id and a location struct
   Top Data Union -> Property-struct {:id :property} -> Property-union -> location structure"
  [du]
  (into [(thrift/property-value du :id)]
        (map #(thrift/value (thrift/property-union-value du :property) %)
             [:address :city :county :state :country :zip])))

(defmapfn structprop
  "Deconstruct a property structure object such as location, which has an id and a structure
   this is only good for one layer below the property union.
   Top Data Union -> Property-struct {:id :property} -> Property-union -> target structure"
  [du]
  (into [(thrift/property-value du :id)]
        (let [th-structure (thrift/property-union-value du :property)]
            (map #(thrift/value th-structure %) (thrift/field-keys th-structure)))))


(def mypail (pail/find-or-create ( DataUnitPailStructure.) "example_output"))
(def yourpail (pail/find-or-create ( UnionPailStructure.) "other_output"))


(defn get-names [pail-connection]
    (let [fntap (pail/get-tap pail-connection :first_name)]
      (??<- [?id ?first-name]
            (fntap _ ?fn-data)
            (sprop ?fn-data :> ?id ?first-name))))


(defn get-full-names [pail-connection]
  (let [fntap (pail/get-tap pail-connection :first_name)
        lntap (pail/get-tap pail-connection :last_name)]
    (??<- [?first-name ?last-name]
          (fntap _ ?fn-data)
          (lntap _ ?ln-data)
          (sprop ?fn-data :> ?id ?first-name)
          (sprop ?ln-data :> ?id ?last-name))))

(defn get-location [pail-connection]
  (let [loctap (pail/get-tap pail-connection :location)]
    (??<- [!address !city !county !state !country !zip]
          (loctap _ ?loc-data)
          (locprop ?loc-data :> ?id !address !city !county !state !country !zip))))

(??<- [!address !city !county !state !country !zip]
      (loctap _ ?data)
      (locprop ?data :> !id !address !city !county !state !country !zip))

(defn get-slocation [pail-connection]
  (let [loctap (pail/get-tap pail-connection :location)]
    (??<- [?id ?data]
          (loctap _ ?loc-data)
          (sprop ?loc-data :> ?id !data))))

(defn get-everything [pail-connection]
  (let [fntap (pail/get-tap pail-connection :first_name)
        lntap (pail/get-tap pail-connection :last_name)
        loctap (pail/get-tap pail-connection :location)]
    (??<- [?first-name ?last-name !address !city !county !state !country !zip]
          (fntap _ ?fn-data)
          (lntap _ ?ln-data)
          (loctap _ ?loc-data)
          (sprop ?fn-data :> ?id ?first-name)
          (sprop ?ln-data :> ?id ?last-name)
          (locprop ?loc-data :> ?id !address !city !county !state !country !zip))))


(defn tests []
  (let [pail-struct (DataUnitPailStructure.)]
    ; see which partitioner we have
    (println (.getPartitioner pail-struct))
    (println (.getTapMapper pail-struct))
    ; print target partitions
    (prn-str [
              (.getTarget pail-struct du1-1)
              (.getTarget pail-struct du1-2)
              (.getTarget pail-struct du1-3)
              (.getTarget pail-struct du3)
              ])
   )
  (let [pc (pail/find-or-create (DataUnitPailStructure.) "example_output")
        fntap (pail/get-tap pc :first_name)
        loctap (pail/get-tap pc :location)]
    ;print objects and their deconstructed values
    (println du1-1)
    (println (sprop du1-1))
    (println du1-2)
    (println (sprop du1-2))
    (println du1-3)
    (println (structprop du1-3))

    ; write the objects to the pail
    (pail/write-objects pc objectlist)

    ; Query the data back out.
    (def names (??<- [?id ?first-name]
                     (fntap _ ?fn-data)
                     (sprop ?fn-data :> ?id ?first-name)))

    (def locs (??<- [?id !address !city !county !state !country !zip]
                    (loctap _ ?loc-data)
                    (locprop ?loc-data :> ?id !address !city !county !state !country !zip)))

    (println "Names===========================")
    (println names)
    (println "Locations===========================")
    (println locs)


))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (println "Hello, World!")
  (tests))
