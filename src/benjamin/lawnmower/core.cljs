(ns benjamin.lawnmower.core)


;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (faces/mount)
  (rdom/render
   [:h1 "hurr"]
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
  (faces/unmount)
  (js/console.log "stop"))
