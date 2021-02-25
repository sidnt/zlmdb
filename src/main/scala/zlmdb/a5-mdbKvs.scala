package zio.lmdb

import zio._
import java.nio.ByteBuffer
import mdbDbi._

object mdbKvs {

  type MdbKvs = Has[Service]
  trait Service {
    def put(key:ByteBuffer, value:ByteBuffer):Task[Boolean]
    def get(key:ByteBuffer): Task[ByteBuffer]
  }

  val defaultMdbKvs: URLayer[MdbDbi, MdbKvs] = ZLayer.fromService( (mds:mdbDbi.Service) => new mdbKvs.Service {
    import adhocTxns._
    def put(key: ByteBuffer, value: ByteBuffer): Task[Boolean] = for {
      mRwTxn    <-  UIO(mRwTxn(mds.dbisEnvHandle))
      wasPut      <-  mRwTxn.use(txn => Task(mds.mdbDbiHandle.put(txn, key, value)))
    } yield wasPut
    def get(key: ByteBuffer): Task[ByteBuffer] = for {
      mRoTxn    <-  UIO(mRoTxn(mds.dbisEnvHandle))
      bb        <-  mRoTxn.use(txn => Task(mds.mdbDbiHandle.get(txn, key)))
    } yield bb
  })
  
  val accessDefaultMdbKvs = ZIO.access[MdbKvs](_.get)

}