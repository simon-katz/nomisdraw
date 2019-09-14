(defproject nomisdraw "0.1.0-SNAPSHOT"
  :description "A drawing app of some kind"
  :url "https://github.com/simon-katz/nomisdraw"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [prismatic/schema "1.1.12"]
                 [org.clojure/core.async "0.4.500"]
                 [com.stuartsierra/component "0.4.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [cljs-ajax "0.8.0"]
                 [environ "1.1.0"]
                 [reagent "0.9.0-rc1"]
                 [re-com "2.5.0"]
                 [compojure "1.6.1"]
                 [ring "1.7.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring-middleware-format "0.7.4"]
                 [clj-http "3.10.0"]
                 [cheshire "5.9.0"]
                 [quil "2.8.0"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.19"]]

  :main ^:skip-aot nomisdraw.system.main
  :repl-options {:init-ns user
                 :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}

  :source-paths ["src/clj"]
  :test-paths   ["test/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds
              [{;; This build is a compressed minified build for
                ;; production. You can build this with:
                ;;   lein cljsbuild once min
                ;; This should be the first build in the :builds vector, because
                ;; it is the one used when creating uberjars.
                ;; - See https://github.com/emezeske/lein-cljsbuild/issues/213
                ;; - (sheesh!)
                :id "min"
                :source-paths ["src/cljs"]
                :compiler {:main nomisdraw.main
                           :output-to "resources/public/js/compiled/nomisdraw.js"
                           :optimizations :advanced
                           :pretty-print false}}
               {:id "dev"
                :source-paths ["src/cljs"]
                :compiler {:main nomisdraw.main
                           :output-to "resources/public/js/compiled/nomisdraw.js"
                           :asset-path "js/compiled/out"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}
                :figwheel {:on-jsload "nomisdraw.main/on-js-reload"}}]}

  :figwheel {;; :http-server-root "public" ;; default and assumes "resources"
             :server-port 26740
             ;; :server-ip "127.0.0.1"

             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             ;; :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own server :)
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log"
             }

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.3.1"]
                                  [midje "1.9.9"]
                                  [cider/piggieback "0.4.1"]
                                  [figwheel-sidecar "0.5.19"]]
                   :plugins [[lein-midje "3.2.1"]]}
             :uberjar {:aot :all}}

  :target-path "target/%s/")
