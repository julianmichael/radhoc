package radhoc

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react._

class LocalState[A] {
  type State = A
  type Context = A => Callback
  case class Props(
    initialValue: A,
    shouldRefresh: A => Boolean = (_: A) => true,
    render: StateVal[A] => VdomElement
  )

  class Backend(scope: BackendScope[Props, State]) {
    def set(a: A): Callback = scope.setState(a)

    def render(props: Props, state: State) =
      props.render(StateVal(state, set _))
  }

  val Component = ScalaComponent
    .builder[Props]("Local")
    .initialStateFromProps(_.initialValue)
    .renderBackend[Backend]
    .componentWillReceiveProps(
      context =>
        if (context.currentProps.initialValue != context.nextProps.initialValue &&
            context.nextProps.shouldRefresh(context.state)) {
          context.backend.set(context.nextProps.initialValue)
        } else Callback.empty
    )
    .build

  def make(initialValue: A, shouldRefresh: A => Boolean = (_: A) => true)(
    render: StateVal[A] => VdomElement
  ) = {
    Component(Props(initialValue, shouldRefresh, render))
  }
}
