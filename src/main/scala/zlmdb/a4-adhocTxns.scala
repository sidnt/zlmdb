package zlmdb

import zio._
import java.nio.ByteBuffer
import org.lmdbjava.Env

object adhocTxns {

  val mRwTxn = (env:Env[ByteBuffer]) => ZManaged.make(UIO(env.txnWrite()))(txn => UIO(txn.commit()))
  
  val mRoTxn = (env:Env[ByteBuffer]) => ZManaged.make(UIO(env.txnRead()))(txn => UIO(txn.commit()))

}