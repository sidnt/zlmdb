package zlmdb

import zio._

object mdbEnvConfig {

  type MdbEnvConfig = Has[Service]
  trait Service {
    val envDirPath: String
    val envMaxMiBs: Int
  }

  /** defaultEnvConfigL is 1 of many possible instance of
    * an MdbEnvConfig wrapped in a ULayer
    * .
    * this instance can be fed to other layers 
    * which require an MdbEnvConfig (ie, a Has[MdbEnvConfig.Service])
    * eg, a downstream layer that needs these configuration
    * to construct a layer that can provide a managedMdbEnvHandle downstream services
    * .
    * succeed constructor internally wraps the supplied instance
    * in a Has type.
    * .
    * so to read it,
    * defaultEnvConfigL is a ZLayer that Has an MdbEnvConfig.Service implementation
    * */
  val defaultEnvConfigL: ULayer[MdbEnvConfig] = ZLayer.succeed {
    new Service {
      val envDirPath: String = "defaultEnv"
      val envMaxMiBs: Int = 10
    }
  }

  /** we can construct other instances of MdbEnvConfig
    * eg, those that derive their configuration from a configuration file
    */

}