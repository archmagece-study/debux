(ns debux.macro-types
  (:require [clojure.set :as set]
            [debux.common.util :as ut]
            [debux.dbg :as d]))

(def macro-types*
  (atom {:def-type `#{def defonce}
         :defn-type `#{defn defn-}
         :fn-type `#{fn fn*}

         :let-type
         `#{let binding dotimes when-first when-let when-some with-in-str
            with-local-vars with-open with-out-str with-redefs}
         :if-let-type `#{if-let if-some}
         :letfn-type `#{letfn}
         :loop-type `#{loop}

         :for-type `#{for doseq}
         :case-type `#{case}

         :skip-arg-1-type `#{set! with-precision}
         :skip-arg-2-type `#{as->}
         :skip-arg-1-2-type `#{}
         :skip-arg-1-3-type `#{defmethod}
         :skip-arg-2-3-type `#{amap areduce}
         :skip-arg-1-2-3-type `#{}

         :skip-all-args-type
         `#{declare defmacro defmulti defstruct extend extend-protocol extend-type
            import memfn new ns proxy proxy-super quote refer-clojure reify sync var}

         :skip-form-itself-type
         `#{catch definline definterface defprotocol defrecord
            deftype finally gen-class gen-interface
            debux.core/dbg debux.core/dbgn debux.core/dbgt
            debux.core/dbg* debux.core/dbgn* debux.core/dbgt*
            debux.cs.core/clog debux.cs.core/clogn debux.cs.core/clogt
            debux.cs.core/clog* debux.cs.core/clogn* debux.cs.core/clogt*}

         :expand-type
         `#{clojure.core/.. -> ->> doto cond-> cond->> condp import
            some-> some->>}
         :dot-type `#{.} }))

(defn- merge-symbols [old-symbols new-symbols]
  (->> (map #(ut/ns-symbol %) new-symbols)
       set
       (set/union old-symbols) ))

(defmacro register-macros! [macro-type new-symbols]
  (-> (swap! macro-types* update macro-type
             #(merge-symbols % new-symbols))
      ut/quote-vals)
  (when (= macro-type :let-type)
    (-> (swap! d/dbg-macro-types* update :let
               #(merge-symbols % new-symbols))
        ut/quote-vals)))

(defmacro show-macros
  ([] (ut/quote-vals @macro-types*))
  ([macro-type] (-> (select-keys @macro-types* [macro-type])
                    ut/quote-vals)))
