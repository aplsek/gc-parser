(ns gc-parser.main
    (:require
                [gc-parser.const :refer :all]
                [gc-parser.matcher_g1 :refer :all]
                [gc-parser.matcher_ParOld :refer :all]
                [gc-parser.pre_formatter :refer :all]
                [gc-parser.core :refer :all]
                ))

(use '[clojure.string :only (join)])



(defn -main [infile outfile] 
  (process-gc-file-preformat infile TMP_GC_FILE )
  (process-gc-file TMP_GC_FILE outfile)  
  )


