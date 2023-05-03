(ns tas.utils
  (:require [clj-yaml.core :as yaml]
            [tas.error :refer [error!]])
  (:import [java.io FileNotFoundException]))

(defn yml-file->-clj! [file-path]
  (try (yaml/parse-string (slurp file-path))
       (catch FileNotFoundException e
         (error! "File not found: "
                 {:file-path file-path
                  :category :not-found} e))
       (catch Exception e
         (error! "Can not parse yml file: "
                 {:file-path file-path
                  :category :incorrect} e))
       ))

^:rct/test
(comment
  (yml-file->-clj! "test/resources/f.yml")
  ;=> ({:name "TASIP", :swc "AA123654", :swcis ({:stage "Dev", :swci "AT112233"} {:stage "Te1", :swci "AT123242"})} 
  ;    {:name "Mastermind", :swc "AA44345", :swcis ({:stage "Dev", :swci "AT44345"} {:stage "Te1", :swci "AT412323"})}))) 
  :rcf)