#!/bin/bash -e

lein nomis-ns-graph :platform clj  :filename nomis-ns-graph-clj :exclusions "user|midje"
lein nomis-ns-graph :platform clj  :filename nomis-ns-graph-clj-with-externals :show-non-project-deps :exclusions "user|midje"
lein nomis-ns-graph :platform cljs :filename nomis-ns-graph-cljs
lein nomis-ns-graph :platform cljs :filename nomis-ns-graph-cljs-with-externals :show-non-project-deps
