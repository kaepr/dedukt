(ns dedukt.core
  (:require [clojure.string :as str]))

(def db
  [[11 :user/name "richhickey"]
   [22 :user/name "tonsky"]
   [33 :user/name "pithyless"]])

(def q
  [:find '?name
   :where
   [11 :user/name '?name]])

(defn parse-query
  "Returns query in map form.

  Each clause becomes a key inside map."
  [q]
  (let [find-index (.indexOf q :find)
        where-index (.indexOf q :where)
        vars (subvec q (inc find-index) where-index)
        wheres (subvec q (inc where-index))]
    {:find vars
     :where wheres}))

(defn variable? [v]
  (and (symbol? v)
       (str/starts-with? (str v) "?")))

(defn match
  "Returns the fact, if it succesfully matches to the pattern.
   Otherwise nil."
  [pattern fact]
  (let [term-match? (fn [p f] (or (= '_ p) (variable? p) (= p f)))
        matched? (reduce (fn [acc [p f]]
                           (and acc (term-match? p f)))
                         true
                         (map vector pattern fact))]
    (when matched? fact)))

(defn select [find-vars pattern fact]
  (let [pattern-to-fact (map vector pattern fact)
        f (fn [fv]
            (some (fn [p-f]
                    (when (= (first p-f) fv) (second p-f))) pattern-to-fact))]
    (mapv f find-vars)))

(defn run [db q]
  (let [{:keys [find where]} (parse-query q)
        facts (filter #(match (first where) %) db)
        values (mapv #(select find (first where) %) facts)]
    (set values)))

(run db [:find '?attr
         :where [22 '?attr "tonsky"]])

(run db [:find '?id
         :where ['?id :user/name "tonsky"]])

(run db [:find '?id
         :where ['?id :user/name '_]])

(run db [:find '?id '?name
         :where ['?id :user/name '?name]])

(run db [:find '?name
         :where ['_ '?name '_]])

(comment

  (def db
    [[11 :user/name "richhickey"]
     [22 :user/name "tonsky"]
     [33 :user/name "pithyless"]])

  [:find '?attr
   :where [11 '?attr "tonsky"]] ;; #{[:user/name]}

  [:find '?id
   :where ['?id :user/name "tonsky"]] ;; #{[22]}

  [:find '?id '?name
   :where ['?id :user/name '?name]] ;; #{[22 "tonsky"]}

  (match ['?id '_ "tonsky"] [22 :user/name "tonsky"])

  (run db [:find '?id '?name
           :where ['?id :user/name '?name]])

  ())
