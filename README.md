# radhoc

DEPRECATED: I folded this into the jjm-ui module at [jjm](https://github.com/julianmichael/jjm)
since I was using jjm as a dependency anyway.

---

Rad Higher-Order Components (or, Rendering Ad-Hoc style in React!)

This is a tiny scala.js library for higher-order components that I use throughout projects
that involve UIs written with React in Scala.js. The HOC style of writing interfaces I find much
easier to use & reason about in comparison to the monolithic state / reducer approach advocated by
Redux... I think. (I haven't actually used redux, just checked out its docs a bit.)

I might write something about the motivation and general use of these components at some point.

## Usage

To include it in your project, add
```
  ivy"org.julianmichael::radhoc::0.1.0"
```
to your `ivyDeps` in your Mill build. You will also need `react` and `react-dom` 15.6.1 loaded in JS
when running React to use these components.  More documentation coming if anyone requests it.
