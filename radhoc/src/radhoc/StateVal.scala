package radhoc

import japgolly.scalajs.react.Callback
import monocle.Lens

case class StateVal[A](
  val get: A,
  val set: A => Callback
) {
  def modify(f: A => A): Callback = set(f(get))

  def zoom[B](lens: Lens[A, B]) = StateVal[B](
    lens.get(get),
    b => set(lens.set(b)(get))
  )
}
