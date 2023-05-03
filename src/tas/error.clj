(ns tas.error)

(defn error!
  
  ([message]
   (throw (ex-info message {})))

  ([message data]
   (throw (ex-info message data)))

  ([message data cause]
   (throw (ex-info message data cause))))

(defn pprint-ex-info! [e]
  (binding [*out* *err*]
    (println "\nSomething went wrong! \n")
    (println (.getMessage e))
    (println (ex-data e)))
  )