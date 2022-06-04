(ns
    benjamin.lawnmower.core
    (:require
     [reagent.dom :as rdom]
     [re-frame.core :as rf]
     [goog.events.KeyCodes :as keycodes]
     [goog.events :as gev])
    (:import [goog.events KeyHandler]))

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

(defn capture-key
  "Given a `keycode`, execute function `f` "
  [keycode-map]
  (let [key-handler (KeyHandler. js/document)
        press-fn (fn [key-press]
                   (when-let [f (get keycode-map (.. key-press -keyCode))]
                     (f)))]
    (gev/listen key-handler
                (-> KeyHandler .-EventType .-KEY)
                press-fn)))
;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (capture-key
   {keycodes/J
    (fn [] (js/console.log "hi1")
      (rf/dispatch [:player/mv :down]))
    keycodes/DOWN
    (fn [] (js/console.log "hi1")
      (rf/dispatch [:player/mv :down]))
    keycodes/UP
    (fn []
      (rf/dispatch [:player/mv :up]))
    keycodes/K
    (fn []
      (rf/dispatch [:player/mv :up]))
    keycodes/H
    (fn []
      (rf/dispatch [:player/mv :left]))
    keycodes/L
    (fn []
      (rf/dispatch [:player/mv :right]))
    keycodes/LEFT
    (fn []
      (rf/dispatch [:player/mv :left]))
    keycodes/RIGHT
    (fn []
      (rf/dispatch [:player/mv :right]))})
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

;; (defn mv-player  )

(def wrap-player-on-board
  (rf/enrich
   (fn [{:game/keys [lawn player] :as db} _]
     (let [max-y (dec (count (first lawn)))
           max-x (dec (count lawn))
           [player-x player-y] player]
       (doto
           (assoc
            db
            :game/player
            [(cond
               (< max-x player-x) 0
               (< player-x 0) max-x
               :else player-x)
             (cond
               (< max-y player-y) 0
               (< player-y 0) max-y
               :else player-y)])
         js/console.log)))))


(rf/reg-event-db
 :player/mv
 [wrap-player-on-board]
 (fn [db [_ direction]]
   (let [mv
         (fn [[x y]]
           (case direction
             :left [(dec x) y] :right [(inc x) y]
             :down [x (inc y)] :up [x (dec y)]))]
     (update db :game/player mv))))

(comment
  (require '[re-frame.db])
  (rf/dispatch-sync [:initialize-db])
  (rf/dispatch-sync [:player/mv :down])
  (do (rf/dispatch-sync [:player/mv :left])
      (:game/player @re-frame.db/app-db))
  (do (rf/dispatch-sync [:player/mv :down])
      (:game/player @re-frame.db/app-db))



  )
