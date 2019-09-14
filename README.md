# No real doc here yet FIXME

# Using figwheel-sidecar

You don't have to do `lein figwheel`.

From a Clojure REPL you can do:

```
(use 'figwheel-sidecar.repl-api)
(start-figwheel!)
```

You will need to refresh the browser.

FWIW, the `lein-figwheel` dependency isn't needed for this to work.


# A problem with the `quil.util` ns

Hmmm...

Problem: If you do a `cider-jack-in-clojurescript`, then doing a `(reset)`
in the CLJ REPL gives an error: namespace 'quil.util' not found.

I don't know why.

Workaround: In the CLJS REPL, jump to definition of "quil.util" (by typing
it and using M-.), then load the file. Then a `(refresh)` and all is ok.
