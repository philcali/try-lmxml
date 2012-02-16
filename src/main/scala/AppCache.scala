package lmxml
package web

import cache.{
  DefaultPacker,
  FileHashStorage
}

import java.util.{ HashMap => JMap }
import java.io.{
  File,
  ByteArrayOutputStream,
  ByteArrayInputStream
}

import com.google.appengine.api.memcache.{
  Expiration,
  MemcacheServiceFactory
}

import scalendar._

object AppEngineCache extends FileHashStorage {
  val cache = MemcacheServiceFactory.getMemcacheService("try-lmxml")

  def defaultExpiration = {
    Expiration.byDeltaMillis(1.month.into.milliseconds.toInt)
  }

  def store(source: File, nodes: Seq[ParsedNode]) {
    val byteStream = new ByteArrayOutputStream() 

    DefaultPacker.serialize(nodes, byteStream)

    cache.put(hashFilename(source), byteStream.toByteArray, defaultExpiration)
  }

  def retrieve(source: File) = {
    val bytes = cache.get(hashFilename(source)).asInstanceOf[Array[Byte]]
    DefaultPacker.unserialize(new ByteArrayInputStream(bytes))
  }

  def contains(source: File) = cache.contains(hashFilename(source))

  def changed(source: File) = !contains(source)

  def remove(source: File) = cache.delete(hashFilename(source))

  def clear() = cache.clearAll()
}
