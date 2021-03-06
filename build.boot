(set-env!
 :source-paths #{"src/clj" "src/cljs" "src/cljc"}
 :resource-paths #{"html"}

 :dependencies '[
                 [org.clojure/clojure "1.7.0"]         ;; add CLJ
                 [org.clojure/clojurescript "1.7.228"] ;; add CLJ
                 [adzerk/boot-cljs "1.7.228-1" :scope "test"]
                 [pandeiro/boot-http "0.7.0" :scope "test"]
                 [adzerk/boot-reload "0.4.12" :scope "test"]
                 [adzerk/boot-cljs-repl "0.3.2" :scope "test"] ;; add bREPL
                 [com.cemerick/piggieback "0.2.1":scope "test"] ;; needed by bREPL 
                 [weasel "0.7.0" :scope "test"]                   ;; needed by bREPL
                 [org.clojure/tools.nrepl "0.2.12" :scope "test"] ;; needed by bREPL
                 [org.clojars.magomimmo/domina "2.0.0-SNAPSHOT"]
                 [javax.servlet/servlet-api "3.0-alpha-1"]
                 [hiccups "0.3.0"]
                 [compojure "1.4.0"] ;; for routing
                 [enlive "1.1.6"]
                 [adzerk/boot-test "1.1.0" :scope "test"]
                 [crisptrutski/boot-cljs-test "0.2.1" :scope "test"]
                 [reagent "0.6.0-rc"]
                 [cljsjs/snapsvg "0.4.1-0"]
                 [hickory "0.6.0"]
                 [clj-tagsoup "0.3.0"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
         '[adzerk.boot-test :refer [test]]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]]
         'boot.repl)


(swap! boot.repl/*default-dependencies*
       concat '[[cider/cider-nrepl "0.13.0"]])

(swap! boot.repl/*default-middleware*
       conj 'cider.nrepl/cider-middleware)



(def defaults {:test-dirs #{"test/cljc" "test/clj" "test/cljs"}
               :output-to "main.js"
               :testbed :phantom
               :namespaces '#{}})

(deftask add-source-paths
  "Add paths to :source-paths environment variable"
  [t dirs PATH #{str} ":source-paths"]
  (merge-env! :source-paths dirs)
  identity)

(deftask tdd
  "Launch a customizable TDD Environment"
  [e testbed        ENGINE kw     "the JS testbed engine (default phantom)" 
   k httpkit               bool   "Use http-kit web server (default jetty)"
   n namespaces     NS     #{sym} "the set of namespace symbols to run tests in"
   o output-to      NAME   str    "the JS output file name for test (default main.js)"
   O optimizations  LEVEL  kw     "the optimization level (default none)"
   p port           PORT   int    "the web server port to listen on (default 3000)"
   t dirs           PATH   #{str} "test paths (default test/clj test/cljs test/cljc)"   
   v verbose               bool   "Print which files have changed (default false)"]
  (let [dirs (or dirs (:test-dirs defaults))
        output-to (or output-to (:output-to defaults))
        testbed (or testbed (:testbed defaults))
        namespaces (or namespaces (:namespaces defaults))]
    (comp
     (serve :handler 'cosmodrom.core/app
            :resource-root "target"
            :reload true
            :httpkit httpkit
            :port port)
     (add-source-paths :dirs dirs)
     (watch :verbose verbose)
     (reload)
     (cljs-repl)
     (cljs :compiler-options {:out-file output-to 
                              :optimizations optimizations})
     (target :dir #{"target"}))))


(deftask dev 
  "Launch immediate feedback dev environment"
  []
  (comp
   (serve :handler 'cosmodrom.core/app ;; ring hanlder
          :resource-root "target"      ;; root classpath
          :reload true)                ;; reload ns
   (watch)
   (reload)
   (cljs-repl) ;; before cljs
   (cljs)
   (target :dir #{"target"})))
