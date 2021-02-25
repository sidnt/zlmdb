package zio.lmdb

import zio._
import org.lmdbjava.Dbi
import java.nio.ByteBuffer
import org.lmdbjava.DbiFlags.MDB_CREATE

import mdbEnvHandle._
import org.lmdbjava.Env

object mdbDbi {

  type MdbDbi = Has[Service]
  trait Service {
    val mdbDbiHandle: Dbi[ByteBuffer]
    val dbisEnvHandle: Env[ByteBuffer]
    /* dbi's Env Handle is passed in the service as its reference is required in transactions 
     * we need to thus, couple the dbi handle with the env handle used to create/open it */
  }

  val defaultMdbDbiL: ZLayer[MdbEnvHandle, Throwable, MdbDbi] =
    ZLayer.fromServiceM((mehs: mdbEnvHandle.Service) =>
      for {
        mdbDbiHandle1 <- Task(mehs.mdbEnvHandle.openDbi(null: String, MDB_CREATE))
        mdbDbiHandleS  = new Service {
                               val mdbDbiHandle: Dbi[ByteBuffer] = mdbDbiHandle1
                               val dbisEnvHandle: Env[ByteBuffer] = mehs.mdbEnvHandle
                             }
      } yield mdbDbiHandleS
    )

  /* PS:
   * MDB_CREATE -  create DB if not already existing
   * http://www.lmdb.tech/doc/group__mdb__dbi__open.html#gafd47620cff55fb3ec7cd7501d4d1cb4a */

}
