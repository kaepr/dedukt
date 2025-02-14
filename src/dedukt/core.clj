(ns dedukt.core
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(def db
  [[11 :user/name "richhickey"]
   [22 :user/name "tonsky"]
   [33 :user/name "pithyless"]
   [11 :user/email "rich@e.com"]
   [22 :user/email "nikita@e.com"]
   [33 :user/email "norbert@e.com"]
   [44 :org/name "clojure"]
   [55 :repo/slug "clojure/clojure"]
   [55 :repo/owner 44]
   [66 :repo/slug "tonsky/datascript"]
   [66 :repo/owner 22]])

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

(defn find-variable-facts
  "Returns subset of fact, which are assigned as variables in the pattern."
  [pattern fact]
  (keep (fn [[p f]] (when (variable? p) f))
        (map vector pattern fact)))

(defn variable->fact
  "Return a map from variable to fact, with a given pattern and fact."
  [pattern fact]
  (zipmap (filter variable? pattern)
          (find-variable-facts pattern fact)))

(comment
  (variable->fact
   '[?id :user/name ?name]
   '[11 :user/name "richhickey"]))

(defn all-matches
  "Returns variable->fact mapping from all facts, with a given pattern."
  [pattern facts]
  (map #(variable->fact pattern %)
       (filter #(match pattern %) facts)))

(comment

  (all-matches
   '[?id :user/email ?email]
   db)

  ())

(defn merge-variable->facts
  "Given two lists of variable->fact bindings.

  Keep a variable->fact map, if the common variable b/w two maps have the same value."
  [variable->facts-1 variable->facts-2]
  (for [vf1 variable->facts-1
        vf2 variable->facts-2
        :when (every? (fn [k] (= (get vf1 k)
                                 (get vf2 k)))
                      (set/intersection (set (keys vf1))
                                        (set (keys vf2))))]
    (merge vf1 vf2)))

(comment

  (merge-variable->facts
   '[{?id 11}]
   '[{?id 11 ?email "rich@e.com"}
     {?id 22 ?email "tonsky@e.com"}])

  ())

(defn process-patterns [patterns facts]
  (let [initial-matches (all-matches (first patterns) facts)
        result (reduce (fn [acc pattern]
                         (merge-variable->facts acc
                                                (all-matches pattern facts)))
                       initial-matches
                       (rest patterns))]
    result))

(comment

  (process-patterns
   '[[?id :user/name "richhickey"]
     [?id :user/email ?email]]
   db)

  ())

(defn select
  [variables result]
  (->> result
       (map #(vals (select-keys % variables)))
       set))

(defn run [db q]
  (let [{:keys [find where]} (parse-query q)
        result (process-patterns where db)]
    (->> result
         (select find))))

(run db q)

(run db
     '[:find ?email
       :where
       [?id :user/email ?email]
       [?id :user/name "richhickey"]])

(run db
     '[:find ?email
       :where
       [?id :user/email ?email]
       [?id :user/name "asd"]])

(run db
     '[:find ?repo
       :where
       [?p :user/name "tonsky"]
       [?r :repo/owner ?p]
       [?r :repo/slug ?repo]])

(run db
     '[:find ?name
       :where
       [?p :user/name ?name]
       [?r :repo/owner ?p]
       [?r :repo/slug "tonsky/datascript"]])

(run db
     '[:find ?name ?repo
       :where
       [?p :user/name ?name]
       [?r :repo/owner ?p]
       [?r :repo/slug ?repo]])

(run db
     '[:find ?name ?repo
       :where
       [?p :org/name ?name]
       [?r :repo/owner ?p]
       [?r :repo/slug ?repo]])
