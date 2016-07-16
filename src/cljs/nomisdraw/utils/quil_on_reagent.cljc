(ns nomisdraw.utils.quil-on-reagent)

(defmacro sketch [& args]
  "Wraps `quil.core/sketch` and plays nicely with Reagent.
  Differs from `quil.core/sketch` as follows:
  - An additional `canvas-id` argument is needed; this is the first argument.
  - No :host argument is permitted. This function creates its own canvas.
  - The :size argument must be either `nil` or a [width height] vector.
  (You might think we could create our own unique canvas id, but
  that would break re-rendering.)
  TODO: Ah! -- This can be done with a macro that creates the canvas id at
               compile time. Now that now that you have unmounting this will be
               ok.
        Ah!! -- No. The same call site can create multiple sketches."
  (let [canvas-id (random-canvas-id)]
    `(sketch* ~canvas-id ~@args)))
