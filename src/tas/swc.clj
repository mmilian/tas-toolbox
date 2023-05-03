(ns tas.swc
  (:require
   [malli.core :as m]))


(def In-Swci?
  [:map [:stage string?] [:swci string?]])

(def In-Swc?
  [:map
   [:name string?]
   [:swc string?]
   [:swcis [:sequential In-Swci?]]])


^:rct/test
(comment
  (m/validate In-Swci? {:stage "Dev" :swci "AT112233"}) ;=> true
  (m/validate In-Swc? {:name "TASIP", :swc "AA123654", :swcis '({:stage "Dev", :swci "AT112233"} {:stage "Te1", :swci "AT123242"})}) ;=> true
  :rcf)


(def Swci?
  [:map
   [:name string?]
   [:stage string?]
   [:swci string?]
   [:swc string?]])


(defn flatten-swci
  {:malli/schema [:=> [:cat In-Swc?]  [:sequential Swci?]]}
  [item]
  (let [{:keys [name swc]} item]
    (map #(assoc % :name name :swc swc) (:swcis item))))


^:rct/test
(comment

  (let [item {:name "TASIP", :swc "AA123654", :swcis '({:stage "Dev", :swci "AT112233"} {:stage "Te1", :swci "AT123242"})}]
    (flatten-swci item))
  ;=> ({:stage "Dev", :swci "AT112233", :name "TASIP", :swc "AA123654"}
  ;     {:stage "Te1", :swci "AT123242", :name "TASIP", :swc "AA123654"})

  :rcf)


(defn format-swci 
  {:malli/schema [:=> [:cat Swci?]  string?]}
  [swci]
  (let [{:keys [name swc stage swci]} swci]
    (format "%s (%s); %s; %s" name swc stage swci)))

^:rct/test
(comment
  (format-swci {:stage "Dev", :swci "AT112233", :name "TASIP", :swc "AA123654"})
  ;=> "TASIP (AA123654); Dev; AT112233"
  :rcf)

(defn render-swci
  {:malli/schema [:=> [:cat [:sequential In-Swc?]]  [:sequential string?]]}
  [swcs]
  (let [swcis (flatten (map flatten-swci swcs))]
    (map format-swci swcis)))

^:rct/test
(comment
  (let [swcs '({:name "TASIP", :swc "AA123654", :swcis ({:stage "Dev", :swci "AT112233"} {:stage "Te1", :swci "AT123242"})}
               {:name "Mastermind", :swc "AA44345", :swcis ({:stage "Dev", :swci "AT44345"} {:stage "Te1", :swci "AT412323"})})]
    (render-swci swcs))
  ;=> ("TASIP(AA123654); Dev; AT112233"
  ;    "TASIP(AA123654); Te1; AT123242"
  ;    "Mastermind(AA44345); Dev; AT44345"
  ;    "Mastermind(AA44345); Te1; AT412323") 
  :rcf)