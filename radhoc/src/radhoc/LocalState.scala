package radhoc

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react._

object LocalState {
  type State[A] = A
  type Context[A] = A => Callback
  case class Props[A](
    initialValue: A,
    shouldRefresh: A => Boolean = (_: A) => true,
    render: StateVal[A] => VdomElement
  )

  class Backend[A](scope: BackendScope[Props[A], State[A]]) {
    def set(a: A): Callback = scope.setState(a)

    def render(props: Props[A], state: State[A]) =
      props.render(StateVal(state, set _))
  }

  def Component[A] = ScalaComponent
    .builder[Props[A]]("Local")
    .initialStateFromProps(_.initialValue)
    .renderBackend[Backend[A]]
    .componentWillReceiveProps(
      context =>
        if (context.currentProps.initialValue != context.nextProps.initialValue &&
            context.nextProps.shouldRefresh(context.state)) {
          context.backend.set(context.nextProps.initialValue)
        } else Callback.empty
    )
    .build

  def make[A](initialValue: A, shouldRefresh: A => Boolean = (_: A) => true)(
    render: StateVal[A] => VdomElement
  ) = {
    Component[A](Props[A](initialValue, shouldRefresh, render))
  }
}
