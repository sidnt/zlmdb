package zio.lmdb

import zio._
import mdbEnvConfig._
import mdbEnvHandle._
import mdbDbi._
import mdbKvs._
import zio.blocking._

object layers {

  val layer: ZLayer[Any, Throwable, MdbKvs] =
    (defaultEnvConfigL ++ Blocking.live) >>>
    defaultMdbEnvHandleL >>>
    defaultMdbDbiL >>>
    defaultMdbKvs

}