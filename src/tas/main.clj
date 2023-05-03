(ns tas.main
  (:require [babashka.cli :as cli]
            [babashka.fs :as fs]
            [bblgum.core :as b]
            [clojure.string :as str]
            [tas.error :refer [pprint-ex-info!]]
            [tas.swc :refer [render-swci]]
            [tas.utils :refer [yml-file->-clj!]])
  (:gen-class))


(def cli-opts
  {:coerce {:login :string}})

(defn parse-opts [args]
  (cli/parse-opts
   args
   cli-opts))

(defn gum-choose-swci! [swci]
  (->
   (b/gum :choose swci)
   (:result)
   (first)
   (str/split #";")
   (last)
   (str/trim)))


(def spec {:swci {:desc "SWCI"}})

(defn print-login-help! []
  (println "")
  (println "Usage: tas login <swci>")
  (println "")
  (println "where:")
  (println "<swci> is the SWCI  you want to login to (a.k.a AT number).")
  (println "")
  (println "or <swci> can be also provided as option:")
  (println (cli/format-opts
            {:spec spec})))


(defn print-help [_]
  (println (str/trim "
Usage: tas <subcommand> <options>

Subcommands:

login
  <swci> SWCI (a.k.a AT code), can be provided as option as well (--swci <swci>)
")))

(defn validate-exec-env! []
  (let [TAS_TOOLBOX_HOME (System/getenv "TAS_TOOLBOX_HOME")]
    (when (and (nil? TAS_TOOLBOX_HOME) (not (fs/exists? "~/tas-toolbox")))
      (binding [*out* *err*]
        (println "\n --- \n TAS_TOOLBOX_HOME is not set. Please set it to the root of the tas-toolbox project. \n")
        (System/exit 1)))
    (println "TAS_TOOLBOX_HOME is set to " TAS_TOOLBOX_HOME)))

(defn choose-swci! []
  (let [TAS_TOOLBOX_HOME (System/getenv "TAS_TOOLBOX_HOME")
        swcs (yml-file->-clj! (str TAS_TOOLBOX_HOME "/swc/swcs.yml"))
        swcis (render-swci swcs)]
    (gum-choose-swci! swcis)))

(defn login-az! [swci]
  (let [az-login (future (println "Logging in to " swci " ...") (Thread/sleep 3000))]
    (while (not (future-done? az-login))
      (b/gum :spin ["sleep" "1"] :spinner "line" :title (str "Logging in to " swci)))))



(defn login! [{:keys [opts]}]
  (validate-exec-env!)
  (if (or (:help opts) (:h opts))
    (print-login-help!)
    (try
      (login-az! (or (:swci opts) (choose-swci!)))
      (catch clojure.lang.ExceptionInfo e
        (pprint-ex-info! e)
        (System/exit 1))))
  (System/exit 0))


(defn -main [& args]
  (-> (Runtime/getRuntime) (.addShutdownHook (Thread. #(println "\n Thank you for using Tas Toolbox. \n Good bye!"))))
  (cli/dispatch
   [{:cmds ["login"] :fn login! :args->opts [:swci]}
    {:cmds []        :fn print-help}] args))