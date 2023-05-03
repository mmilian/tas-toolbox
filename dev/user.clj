(ns user
  (:require
   [malli.dev :as dev]
   [malli.instrument :as mi]
   [malli.dev.pretty :as pretty])
  )

(comment
  (dev/start! {:report (pretty/reporter)})
  (mi/check)
  (dev/stop!)
  )