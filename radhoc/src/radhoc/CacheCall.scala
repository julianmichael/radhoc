package radhoc

import cats.Monad
import cats.StackSafeMonad

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

sealed trait CacheCall[A] {

  def toFuture: Future[A] = this match {
    case Cached(a)   => Future.successful(a)
    case Remote(fut) => fut
  }
}
case class Cached[A](value: A) extends CacheCall[A]
case class Remote[A](future: Future[A]) extends CacheCall[A]

object CacheCall {
  implicit def cacheCallInstance(implicit ec: ExecutionContext): Monad[CacheCall] =
    new StackSafeMonad[CacheCall] {

      override def pure[A](a: A): CacheCall[A] = Cached(a)

      override def flatMap[A, B](fa: CacheCall[A])(f: A => CacheCall[B]): CacheCall[B] = fa match {
        case Cached(a) => f(a)
        case Remote(futA) =>
          Remote(
            futA.flatMap { a =>
              f(a) match {
                case Cached(b)    => Future.successful(b)
                case Remote(futB) => futB
              }
            }
          )
      }
    }
}
