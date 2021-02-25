package zio.lmdb

import zio._
import zio.nio.file._
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets.UTF_8

object helpers {

  def createOrGetDirFile(dirPath: String) = for {
    path        <-  UIO(FileSystem.default.getPath(dirPath))
    pathExists  <-  Files.exists(path)
    _           <-  if(pathExists) IO.unit else Files.createDirectory(path)
  } yield path.toFile

  def byteArrayToDByteBuffer(ba: Array[Byte]): ByteBuffer = {
    val dbb: ByteBuffer = ByteBuffer.allocateDirect(ba.length).put(ba)
    dbb.flip()
    dbb
  }

  def stringToUtf8DByteBuffer(s:String): ByteBuffer = byteArrayToDByteBuffer(s.getBytes(UTF_8))
  def dByteBufferToUtf8String(bb:ByteBuffer): String = UTF_8.decode(bb).toString
  
}