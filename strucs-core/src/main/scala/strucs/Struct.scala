package strucs

/**
 * Extensible data structure with type safety
 * @tparam F mixin of all the fields types
 */
case class Struct[F](private[strucs] val fields: Map[StructKey, Any]) {
  type Mixin = F

  def +[T](value: T)(implicit k: StructKeyProvider[T], ev: F <:!< T ) = add[T](value)

  /** Adds a field. Pass an Option[T] if the field is optional */
  def add[T](value: T)(implicit k: StructKeyProvider[T], ev: F <:!< T ) : Struct[F with T] = new Struct[F with T](fields + (k.key -> value))

  /** Updates an existing field */
  def update[T >: F](value: T)(implicit k: StructKeyProvider[T]) : Struct[F] = new Struct[F](fields + (k.key -> value))

  def ++[F2](rec: Struct[F2]): Struct[F with F2] = merge[F2](rec)

  /** Add all fields from another Struct */
  def merge[F2](rec: Struct[F2]): Struct[F with F2] = new Struct[F with F2](fields ++ rec.fields)

  /** Get a field */
  def get[T >: F](implicit k: StructKeyProvider[T]): T = fields(k.key).asInstanceOf[T]

  /** Get a subset of the fields */
  def shrink[F2](implicit ev: F <:< F2): Struct[F2] = {
    // the internal Map is kept untouched, but the evidence parameters of all accessors guarantee that
    // removed fields will not be accessible anymore
    this.asInstanceOf[Struct[F2]]
  }

}

object Struct {
  def apply[T](t: T)(implicit k: StructKeyProvider[T]): Struct[T with Nil] = new Struct(Map(k.key -> t))
  def empty: Struct[Nil] = new Struct[Nil](Map.empty)
}





