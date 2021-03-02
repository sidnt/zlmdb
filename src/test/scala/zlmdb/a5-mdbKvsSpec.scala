package zlmdb

import zio._
import zio.test._
import zio.test.Assertion._
import zio.test.TestAspect._
import zio.test.environment._
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
      payloadOut      =   dByteBufferToUtf8String(payloadOutVbb)
    } yield assert(payloadOut)(equalTo("value1"))
  
  val getPutTestM1: ZSpec[MdbKvs, Throwable] = 
    testM("put & get test1")(
      getPutTest1
    )
  
  val getPutTestM1p: ZSpec[ZEnv, Throwable] =
    testM("put & get test1")(
      getPutTest1.provideCustomLayer(layers.layer)
    )

  val getPutTest2: ZIO[MdbKvs, Throwable, TestResult] =
    for {
      mdbKvs          <-  accessDefaultMdbKvs
      payLoadInKbb    =   stringToUtf8DByteBuffer("key2")
      payLoadInVbb    =   stringToUtf8DByteBuffer("value2")
      _               <-  mdbKvs.put(payLoadInKbb,payLoadInVbb)
      payloadOutVbb   <-  mdbKvs.get(payLoadInKbb)
      payloadOut      =   dByteBufferToUtf8String(payloadOutVbb)
    } yield assert(payloadOut)(equalTo("value2"))

  val getPutTestM2: ZSpec[MdbKvs, Throwable] =
    testM("put & get test1")(
      getPutTest2
    )

  val getPutTestM2p: ZSpec[ZEnv, Throwable] =
    testM("put & get test2")(
      getPutTest2.provideCustomLayer(layers.layer)
    )

  def spec: ZSpec[ZEnv, Throwable] =
    suite("mdb key value store specs")(
      getPutTestM1p,
      getPutTestM2p
    ) @@ sequential
  /** PS: this test would become non-deterministic and fail if the `sequential` test aspect were not used
    * as the layer is provided individually to both getPutTestM1p and getPutTestM2p, it would be acquired and released
    * for both the tests, in parallel; and since the layer operates on the same resource (ie, the same lmdb environment)
    * lmdbjava would throw as the same environment's opening second time could be interleaved non deterministically
    * with its closing from the first time
    * .
    * this style of testing is inefficient, done sequentially, because the resource would be acquired and released twice
    * when it should ideally have been acquired once before both the tests, and released once, after both the tests
    * */

}

object mdbKvsSpec2 extends DefaultRunnableSpec {
  import mdbKvsSpec._

  /** Here, we have composed both the tests into a suite,
    * but both the tests have their layer dependency unsatisfied
    * .
    * this fact gets translated into the dependency type of the Spec itself
    * but providing the fallible layer to this suite has weird interactions in the error channel
    */
  val suite1: Spec[MdbKvs, TestFailure[Throwable], TestSuccess] =
    suite("mdb key value store specs")(getPutTestM1, getPutTestM2)//.provideCustomLayer(layers.layer)

  val suite1p1: Spec[Any, Object, TestSuccess]              = suite1.provideLayer(layers.layer)
  val suite1p2: Spec[Any, Object, TestSuccess]              = suite1.provideLayerShared(layers.layer)
  val suite1p3: Spec[TestEnvironment, Object, TestSuccess]  = suite1.provideCustomLayer(layers.layer)
  val suite1p4: Spec[TestEnvironment, Object, TestSuccess]  = suite1.provideCustomLayerShared(layers.layer)
  
  //type ZSpec[-R, +E] = Spec[R, TestFailure[E], TestSuccess]
  override def spec = ???
}
