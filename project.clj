(defproject nomisdraw "0.1.0-SNAPSHOT"
  :description "FIXME: Add description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License" ; FIXME -- is this what you want?
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [prismatic/schema "1.0.4"]
                 [org.clojure/core.async "0.2.374"]
                 [com.stuartsierra/component "0.3.1"]
                 [com.taoensso/timbre "4.3.1"]
                 [cljs-ajax "0.5.3"]
                 [environ "1.0.1"]
                 [reagent "0.6.0-alpha2"]
                 [re-com "0.8.3"]
                 [compojure "1.4.0"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-middleware-format "0.7.0"]
                 [clj-http "2.0.1"]
                 [cheshire "5.5.0"]]

  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-3"]]

  :main ^:skip-aot nomisdraw.system.main
  :repl-options {:init-ns user}

  :source-paths ["src/clj"]
  
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
             ;; :server-port 3449 ; that's the default -- FIXME: Probably change this
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
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [midje "1.7.0"]]
                   :plugins [[lein-midje "3.1.3"]]}
             :uberjar {:aot :all}})
