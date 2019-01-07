package radhoc

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.StateSnapshot

class LocalState[A] {
  type State = A
  type Context = A => Callback
  case class Props(
    initialValue: A,
    shouldRefresh: A => Boolean = (_: A) => true,
    render: StateSnapshot[A] => VdomElement
  )

  val Component = ScalaComponent
    .builder[Props]("LocalState")
    .initialStateFromProps(_.initialValue)
    .render { $ => $.props.render(StateSnapshot.of($)) }
    .componentWillReceiveProps { $ =>
      if ($.currentProps.initialValue != $.nextProps.initialValue &&
            $.nextProps.shouldRefresh($.state)) {
        $.setState($.nextProps.initialValue)
      } else Callback.empty
    }
    .build

  def make(initialValue: A, shouldRefresh: A => Boolean = (_: A) => true)(
    render: StateSnapshot[A] => VdomElement
  ) = {
    Component(Props(initialValue, shouldRefresh, render))
  }
}
