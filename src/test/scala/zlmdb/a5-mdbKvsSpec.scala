package zio.lmdb

import zio.test._
import zio.test.Assertion._

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
    
    )
  )

}