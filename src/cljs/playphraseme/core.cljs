(ns playphraseme.core
  (:require [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [playphraseme.common.config :as config]
            [playphraseme.common.phrases :as phrases]
            [playphraseme.common.responsive :as responsive]
            [playphraseme.common.rest-api :as rest-api]
            [playphraseme.common.route :as route]
            [playphraseme.common.util :as util :refer [or-str]]
            [playphraseme.layout :as layout]
            [playphraseme.views.login.view :as login-page]
            [playphraseme.views.mobile-app.view :as mobile-app-page]
            [playphraseme.views.not-found.view :as not-found-page]
            [playphraseme.views.playlist.view :as playlist-page]
            [playphraseme.views.register.view :as register-page]
            [clojure.string :as string]
            [playphraseme.views.reset-password.view :as reset-password-page]
            [cljs.core.async :as async :refer [<! >! put! chan timeout]]
            [playphraseme.views.search.view :as search-page]
            [playphraseme.views.support.view :as support-page]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [secretary.core :as secretary])
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]])
  (:import goog.History))

(defn ^:export ready-for-prerender? []
  (and @(rf/subscribe [:first-render])
       (util/id->elem "phrase-text-0")))

(def pages
  (merge
   {:login          #'login-page/page
    :register       #'register-page/page
    :reset-password #'reset-password-page/page
    :not-found      #'not-found-page/page
    :support        #'support-page/page
    :mobile-app     #'mobile-app-page/page
    :playlist       #'playlist-page/page}
   (when-not config/disable-search?
     {:search #'search-page/page})))

(defn page []
  (let [page-id @(rf/subscribe [:page])
        params  @(rf/subscribe [:params])]
    [layout/root
     ^{:key [page-id params]}
     [(-> pages page-id) params]]))

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" [query-params]
  (if config/disable-search?
    (util/go-url! "/#/mobile-app")
    (if-not (string/blank? js/searchedPhrase)
      (route/goto-page! :search {:q js/searchedPhrase})
      (util/go-url! (str "/#/search")))))

(secretary/defroute "/search" [query-params]
  (let [{:keys [q]} query-params
        q             (some-> q (string/replace #"\+" " "))]
   (if config/disable-search?
     (util/go-url! (str "playphraseme://search/" q))
     (route/goto-page! :search (merge
                                query-params
                                {:q (or-str
                                     q
                                     @(rf/subscribe [:search-text])
                                     (phrases/random-phrase))})))))


(secretary/defroute "/register" []
  (route/goto-page! :register))

(secretary/defroute "/reset-password" []
  (route/goto-page! :reset-password))

(secretary/defroute "/login" []
  (route/goto-page! :login))

(secretary/defroute "/logout" []
  (rest-api/logout)
  (util/go-url! "/#/"))

(secretary/defroute "/auth" [query-params]
  (let [{:keys [auth-token]} query-params]
    (rest-api/auth auth-token)
    (util/go-url! "/#/search")))

(secretary/defroute "/article" []
  (route/goto-page! :article))

(secretary/defroute "/support" []
  (route/goto-page! :support))

(secretary/defroute "/mobile-app" []
  (route/goto-page! :mobile-app))

(secretary/defroute "/playlist/:id" {id :id}
  (route/goto-page! :playlist {:playlist id}))

(secretary/defroute "*" []
  (route/goto-page! :not-found))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     HistoryEventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (hook-browser-navigation!)
  (responsive/start)
  ;; (ga/start)
  (rest-api/authorize!)
  (mount-components))
