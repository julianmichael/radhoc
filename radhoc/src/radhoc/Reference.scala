package radhoc

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react._

// allows you to easily use refs inline in DOM creation, if, for example,
// you need to set the location of some element (e.g., a dropdown menu)
// on the basis of the location of another.
class ReferenceComponent[A <: vdom.TopNode] {

  case class Props(
    referencedTag: VdomTagOf[A],
    render: (VdomTagOf[A], Option[A]) => VdomElement
  )

  type State = Option[A]

  class Backend(scope: BackendScope[Props, State]) {

    var isRefOld: Boolean = true
    var referenceOpt: Option[A] = None

    def expireRef: Callback = Callback(isRefOld = true)

    def setReference: Callback =
      Callback(isRefOld = false) >>
      scope.setState(referenceOpt)

    val setReferenceFn = () => setReference.runNow

    def render(props: Props, state: Option[A]) =
      props.render(props.referencedTag.ref(node => referenceOpt = Some(node)), state)
  }

  import scala.scalajs.js

  val Component = ScalaComponent
    .builder[Props]("Reference")
    .initialState(None: State)
    .renderBackend[Backend]
    .componentDidMount(
      context =>
        context.backend.setReference >>
        Callback(
          js.Dynamic.global.window.addEventListener(
            "resize",
            context.backend.setReferenceFn
          )
      )
    )
    .componentWillUnmount(
      context =>
        Callback(
          js.Dynamic.global.window.removeEventListener(
            "resize",
            context.backend.setReferenceFn
          )
      )
    )
    .componentWillReceiveProps(_.backend.expireRef)
    .componentDidUpdate(
      context =>
        if (context.backend.isRefOld) {
          context.backend.setReference
        } else Callback.empty
    )
    .build

  def make(referencedTag: VdomTagOf[A])(
    render: (VdomTagOf[A], Option[A]) => VdomElement
  ) = {
    Component(Props(referencedTag, render))
  }

}
