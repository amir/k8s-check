package com.gluegadget

import org.scalatest.prop.{Generator, Randomizer, SizeParam}
import skuber.Volume

object SkuberGen {
  implicit val volumeGenerator: Generator[Volume] = (szp: SizeParam, edges: List[Volume], rnd: Randomizer) => {
    edges match {
      case head :: tail => (head, tail, rnd)
      case _ =>
        val (nv, _, nr) = Generator.stringGenerator.next(szp, Nil, rnd)
        val v = new Volume(name = nv, source = Volume.EmptyDir())

        (v, Nil, nr)
    }
  }
}

