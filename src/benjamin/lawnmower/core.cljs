(ns
    benjamin.lawnmower.core
    (:require
     [reagent.dom :as rdom]
     [re-frame.core :as rf]))

(def lawn-dimensions [3 3])

(defn make-lawn
  [[x y]]
  (into
   []
   (for
       [x (range x)]
       (into
        []
        (for
            [y (range y)]
            {:lawn-piece/pos [x y]
             :lawn-piece/visited? false})))))

(def init-db
  {:game/lawn (make-lawn lawn-dimensions)
   :game/player [0 0]})

(rf/reg-event-db
  :initialize-db
  (fn [_ _]
    init-db))

(defn piece-of-lawn
  [{:lawn-piece/keys [pos visited?]} player]
  [:div
   {:key pos
    :style
    {:height 100
     :width 100
     :background
     (if (= pos player)
       "red"
       (if visited?
         "yellowgreen" "green"))}}])

(defn lawn-grid [lawn player]
  (doall
   (map
    (fn [row]
      (doall (map #(piece-of-lawn % player) row)))
    lawn)))

(comment
  (piece-of-lawn
   (ffirst (make-lawn [1 1])) [0 0])
  (lawn-grid (make-lawn [3 3]) [0 0]))

(rf/reg-sub :game/lawn :game/lawn)
(rf/reg-sub :game/player :game/player)

(defn game []
  (let [lawn @(rf/subscribe [:game/lawn])
        player @(rf/subscribe [:game/player])]
    [:h1 "Mawn the lawn"
     [:div
      {:style
       {:display :grid
        :width 600
        :grid-template-rows "repeat(3, 100px)"
        :grid-template-columns "repeat(3, 100px)"
        :grid-auto-flow :column}}
      (lawn-grid lawn player)]]))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (rdom/render
   [game]
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

(comment
  re-frame.db/app-db
  (rf/dispatch-sync [:initialize-db])


  )
