package radhoc

sealed trait Span {
  def begin: Int
  def end: Int
  def length = end - begin + 1
  def contains(i: Int) = begin <= i && i <= end
}

object Span {
  private[this] case class SpanImpl(override val begin: Int, override val end: Int) extends Span {
    override def toString = s"Span($begin, $end)"
  }
  def apply(x: Int, y: Int): Span = SpanImpl(math.min(x, y), math.max(x, y))
  def unapply(cs: Span): Option[(Int, Int)] = SpanImpl.unapply(cs.asInstanceOf[SpanImpl])

}
