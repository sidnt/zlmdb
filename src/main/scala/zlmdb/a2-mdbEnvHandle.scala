package zio.lmdb

import zio._
import org.lmdbjava._
import java.nio.ByteBuffer

import helpers._

object mdbEnvHandle {

  type MdbEnvHandle = Has[Service]

  /** opening an mdbEnvHandle can fail as:
    * http://www.lmdb.tech/doc/group__mdb.html#ga32a193c6bf4d7d5c5d579e71f22e9340
    * so the acquisition of an mdbEnvHandle got to be an effect
    * at the same time, once acquired, the handle mustn't be acquired again
    * across the process's life
    * .
    * so RT of mdbEnvHandle better literally be Env[ByteBuffer]
    * and the acquisition effect, be done in the layer somewhere
    * .
    * in 1 of many cases, we want the creation of MdbEnvHandle service to depend on MdbEnvConfig
    * and that requirement would be encoded when we declare a ZLayer that models that exact dependency graph
    * like so: ZLayer[MdbEnvConfig, Throwable, MdbEnvHandle]
    * and then we will instantiate that layer with an MdbEnvConfig instance
    * .
    * also,
    * when zio-lmdb client app shuts down,
    * the handle must be discarded with env.close
    * so this requirement must be encoded in our default implementation
    */
  trait Service {
    val mdbEnvHandle: Env[ByteBuffer]
  }

  /** What is the managed effect that can instantiate an MdbEnvHandle service instance?
    * Managed to close the handle in the release block?
    * .
    * This underneath effect, goes from mdbEnvConfig.Service to mdbEnvHandle.Service
    */
  def instantiateMdbEnvHandle(fromMecs: mdbEnvConfig.Service) = ZManaged.make(
    for {
      envDirFile      <-      createOrGetDirFile(fromMecs.envDirPath)
      mehs            =       new Service {
                                val mdbEnvHandle: Env[ByteBuffer] = Env.open(envDirFile, fromMecs.envMaxMiBs)
                              }
    } yield mehs
  )(
    mehs => UIO(mehs.mdbEnvHandle.close())
  )
  /** this is where the power of FP shines
    * in instantiateMdbEnvHandle we don't care about the ZLayer implementation details
    * we just care about an effect that goes from a service to another service
    * it can fail of course in between, in case we haven't taken care of some edge case
    * that might happen when we actually call the upstream library in the managed effect
    * .
    * later we can pass this effect to one of the combinators that can use this effect
    * in the creation of the actual ZLayer that can be fed to an effect, representing our app
    * that requires a concrete instance 
    * and the types will be there to guardrail us
    * the types will align the input outputs
    * .
    * and we will be saved the trouble of solving more problems at once
    * probably this is what is meant by composability, and snapping together like lego building blocks
    */

  
    val defaultMdbEnvHandleL = ZLayer.fromServiceManaged(instantiateMdbEnvHandle)
    /** notice how we don't even pass any argument to `instantiateMdbEnvHandle`
      * because fromServiceManaged is a Higher Order function, that consume other function
      * provided the input/outputs align according to the types
      * .
      * so the beauty is, the argument of `instantiateMdbEnvHandle` is transferred
      * to the input layer of the constructed ZLayer, and not just that, it is wrapped in `Has`
      * the implementation of `fromServiceManaged` takes care of that for us :) 
      */

}