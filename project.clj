;(defproject gc-parser "0.1.0-SNAPSHOT"
; :description "FIXME: write description"
; :url "http://example.com/FIXME"
; :license {:name "Eclipse Public License"
;           :url "http://www.eclipse.org/legal/epl-v10.html"}
; :dependencies [[org.clojure/clojure "1.5.1"]])



(defproject gc-parser "0.1.0"
  :description "Hotspot GC log parser."
  :url "https://github.com/aplsek/gc-parser"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[ring/ring-devel "1.2.0"]]}}
  :test-selectors {:default (complement :integration)
                  :integration :integration
                  :all (fn [_] true)}
  :main gc-parser.main
  :aot [gc-parser.main])