(ns nomisdraw.utils.quil-utils)

;; Without this, a CLJS require of nomisdraw.utils.quil-utils causes
;; an exception.
;; Weird.
;; Ah! -- It's because macros are expanded in CLJ land, or something.
;;     -- Note that when you had conditional compilation in the macro,
;;        the CLJ bit was used.
