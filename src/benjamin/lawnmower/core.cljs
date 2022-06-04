(ns
    benjamin.lawnmower.core
    (:require
     [reagent.dom :as rdom]
     [re-frame.core :as rf]))

(def lawn-size 3)

(defn piece-of-lawn [id]
  [:div
   {:key id
    :style
    {:height 100
     :width 100
     :background "green"}}])

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (rdom/render
   [:h1 "Mawn the lawn"
    [:div
     {:style
      {:display
       :grid
       :width 600
       :grid-template-rows "repeat(3, 100px)"
       :grid-template-columns "repeat(6, 100px)"
       :grid-auto-flow :column
       }}

     (doall (for [x (range lawn-size)
                  y (range lawn-size)]
              (piece-of-lawn [x y])))]]
   (.getElementById js/document "lawnmovergame")))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (rf/dispatch-sync [:initialize-db])
  (js/console.log "start")
  (start))

; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
