package zio.lmdb

import zio._
import zio.test._
import zio.test.Assertion._
import zio.test.TestAspect._

import mdbKvs._
import helpers._

object mdbKvsSpec extends DefaultRunnableSpec {

  val getPutTest1: ZIO[MdbKvs, Throwable, TestResult] =
    for {
      mdbKvs          <-  accessDefaultMdbKvs
      payLoadInKbb    =   stringToUtf8DByteBuffer("key1")
      payLoadInVbb    =   stringToUtf8DByteBuffer("value1")
      _               <-  mdbKvs.put(payLoadInKbb,payLoadInVbb)
      payloadOutVbb   <-  mdbKvs.get(payLoadInKbb)
    } yield assert(dByteBufferToUtf8String(payloadOutVbb))(equalTo("value1"))
  
  val getPutTestM1: ZSpec[MdbKvs, Throwable] = 
    testM("put & get test1")(
      getPutTest1
    )

  val getPutTestM1P: ZSpec[ZEnv, Throwable] =
    testM("put & get test1")(
      getPutTest1.provideCustomLayer(layers.layer)
    )

  val getPutTest2 = for {
        mdbKvs          <-  accessDefaultMdbKvs
        payLoadInKbb    =   stringToUtf8DByteBuffer("key2")
        payLoadInVbb    =   stringToUtf8DByteBuffer("value2")
        _               <-  mdbKvs.put(payLoadInKbb,payLoadInVbb)
        payloadOutVbb   <-  mdbKvs.get(payLoadInKbb)
      } yield assert(dByteBufferToUtf8String(payloadOutVbb))(equalTo("value2"))

  val getPutTestM2 = testM("put & get test1")(
    getPutTest2
  )

  val getPutTestM2P = testM("put & get test2")(
    getPutTest2.provideCustomLayer(layers.layer) 
  )

  val suite1 = suite("mdb key value store specs")(getPutTestM1, getPutTestM2)//.provideCustomLayer(layers.layer)

  def spec = suite("mdb key value store specs")(

    getPutTestM1P,

    getPutTestM2P

  ) @@ sequential
  /* PS: this test would become non-deterministic and fail if you were to not use the sequential test aspect
   * because then the same environment's opening second time could be interleaved randomly with its closing from the first time 
   * */

}