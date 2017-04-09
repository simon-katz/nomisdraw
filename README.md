# No real doc here yet FIXME

# A problem with the `quil.util` ns

Hmmm...

Problem: If you do a `cider-jack-in-clojurescript`, then doing a `(reset)`
in the CLJ REPL gives an error: namespace 'quil.util' not found.

I don't know why.

Workaround: In the CLJS REPL, jump to definition of "quil.util" (by typing
it and using M-.), then load the file. Then a `(refresh)` and all is ok.
