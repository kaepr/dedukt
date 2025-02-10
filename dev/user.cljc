(ns user)

(comment
  ;; hotload libs

  #_(require '[clojure.deps.repl] :refer [add-lib add-libs sync-deps])

  #_(add-libs '{org.ring-clojure/ring-websocket-middleware {:mvn/version "0.2.0"}})

  #_(add-libs '{org.clojure/data.json {:mvn/version "2.5.1"}})

  #_(sync-deps)

  ())
