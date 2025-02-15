# dedukt

A simple [datalog](https://en.wikipedia.org/wiki/Datalog) query engine.

Based on [Domain Modeling With Datalog by Norbert Wojtowicz](https://www.youtube.com/watch?v=oo-7mN9WXTw).

I made this to run the examples in the above talk.

It's currently very basic.
It supports variables, joins and not much else.
It's yet to implement rules and recursively querying on facts.

### Run Locally

Make sure Clojure is installed.

``` shell
clojure -M:repl # starts up a nREPL server
```

### References

- [A shallow dive into DataScript internals by Nikita Prokopov](https://tonsky.me/blog/datascript-internals/)
- [Writing the Worst Datalog Ever in 26loc by Christophe Grand   ](https://buttondown.com/tensegritics-curiosities/archive/writing-the-worst-datalog-ever-in-26loc/)
- [Datalog in Javascript by Stepan Parunashvili](https://www.instantdb.com/essays/datalogjs)
- [Structure and Interpretation of Computer Programs](https://sarabander.github.io/sicp/html/4_002e4.xhtml#g_t4_002e4)

