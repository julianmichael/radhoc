package radhoc

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react._

class SpanSelection[Index] {

  sealed trait SpanSelectionStatus
  case object NoSpan extends SpanSelectionStatus
  case class Selecting(index: Index, anchor: Int, endpoint: Int)
      extends SpanSelectionStatus

  object SpanSelectionStatus

  case class SpanSelectionState(
    spans: Map[Index, List[Span]],
    status: SpanSelectionStatus
  )

  object SpanSelectionState {
    def initial = SpanSelectionState(Map.empty[Index, List[Span]].withDefaultValue(Nil), NoSpan)
  }

  case class SpanSelectionContext(
    setSpan: Map[Index, List[Span]] => Callback,
    hover: Index => Int => Callback,
    touch: Index => Int => Callback,
    cancel: Callback
  )

  case class SpanSelectionProps(
    isEnabled: Boolean,
    enableSpanOverlap: Boolean = false,
    update: SpanSelectionState => Callback,
    render: (SpanSelectionState, SpanSelectionContext) => VdomElement
  )

  class SpanSelectionBackend(scope: BackendScope[SpanSelectionProps, SpanSelectionState]) {

    def setSpan(spans: Map[Index, List[Span]]): Callback =
      scope.modState(_.copy(spans = spans))

    // not sure why I can't just sequence the update after a modState call. but it seemed to get stuck on the stale state
    // for some reason during the update even though it was sequenced after.
    private[this] def modStateWithUpdate(
      f: SpanSelectionState => SpanSelectionState
    ): Callback =
      scope.props >>= { props =>
        if (!props.isEnabled) Callback.empty
        else
          scope.state >>= { state =>
            val newState = f(state)
            if (newState != state) {
              scope.setState(newState) >> props.update(newState)
            } else Callback.empty
          }
      }

    def hover(props: SpanSelectionProps)(index: Index)(endpoint: Int) = modStateWithUpdate {
      case SpanSelectionState(spans, Selecting(`index`, anchor, _)) =>
        val relevantSpans = if (props.enableSpanOverlap) {
          spans.get(index).getOrElse(Nil)
        } else spans.values.toList.flatten
        val range =
          if (anchor <= endpoint) (anchor to endpoint)
          else (anchor to endpoint by -1)
        val newExtremum = range.takeWhile(i => !relevantSpans.exists(_.contains(i))).last
        SpanSelectionState(spans, Selecting(index, anchor, newExtremum))
      case x => x
    }

    def touch(props: SpanSelectionProps)(index: Index)(wordIndex: Int): Callback =
      modStateWithUpdate {
        case SpanSelectionState(spans, NoSpan) =>
          spans(index).zipWithIndex.find(_._1.contains(wordIndex)).map(_._2) match {
            case None =>
              val relevantSpans = if (props.enableSpanOverlap) {
                spans.get(index).getOrElse(Nil)
              } else spans.values.toList.flatten
              if (relevantSpans.exists(_.contains(wordIndex)))
                SpanSelectionState(spans, NoSpan) // do nothing
              else
                SpanSelectionState(spans, Selecting(index, wordIndex, wordIndex)) // start highlighting
            case Some(i) => // remove span
              SpanSelectionState(
                spans.updated(index, spans(index).take(i) ++ spans(index).drop(i + 1)),
                NoSpan
              )
          }
        case SpanSelectionState(spans, Selecting(`index`, x, y)) =>
          val relevantSpans = if (props.enableSpanOverlap) {
            spans.get(index).getOrElse(Nil)
          } else spans.values.toList.flatten
          if (relevantSpans.exists(_.contains(wordIndex)))
            SpanSelectionState(spans, Selecting(index, x, y)) // do nothing
          else
            SpanSelectionState(spans.updated(index, Span(x, y) :: spans(index)), NoSpan) // finish span
        case x => x
      }

    def cancel = modStateWithUpdate(_.copy(status = NoSpan))

    def render(props: SpanSelectionProps, state: SpanSelectionState) =
      props.render(state, SpanSelectionContext(setSpan, hover(props), touch(props), cancel))
  }

  val SpanSelection = ScalaComponent
    .builder[SpanSelectionProps]("Span Selection")
    .initialState(SpanSelectionState.initial)
    .renderBackend[SpanSelectionBackend]
    .build
}
