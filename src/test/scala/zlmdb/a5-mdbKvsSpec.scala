package zio.lmdb

import zio.test._
import zio.test.Assertion._
import zio.test.TestAspect._

import mdbKvs._
import helpers._

object mdbKvsSpec extends DefaultRunnableSpec {

  def spec = suite("mdb key value store spec")(

    testM("put & get test")(
      (for {
        mdbKvs          <-  accessDefaultMdbKvs
        payLoadInKbb    =   stringToUtf8DByteBuffer("key")
        payLoadInVbb    =   stringToUtf8DByteBuffer("value")
        _               <-  mdbKvs.put(payLoadInKbb,payLoadInVbb)
        payloadOutVbb   <-  mdbKvs.get(payLoadInKbb)
      } yield assert(dByteBufferToUtf8String(payloadOutVbb))(equalTo("value"))).provideCustomLayer(layers.layer)
    
    ),

    testM("put & get test2")(
      (for {
        mdbKvs          <-  accessDefaultMdbKvs
        payLoadInKbb    =   stringToUtf8DByteBuffer("key2")
        payLoadInVbb    =   stringToUtf8DByteBuffer("value2")
        _               <-  mdbKvs.put(payLoadInKbb,payLoadInVbb)
        payloadOutVbb   <-  mdbKvs.get(payLoadInKbb)
      } yield assert(dByteBufferToUtf8String(payloadOutVbb))(equalTo("value2"))).provideCustomLayer(layers.layer)
    
    )

  ) @@ sequential
  /* PS: this test would become non-deterministic and fail if you were to not use the sequential test aspect
   * because then the same environment's opening second time could be interleaved randomly with its closing from the first time 
   * */

}