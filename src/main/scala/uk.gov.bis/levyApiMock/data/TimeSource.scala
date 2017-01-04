package uk.gov.bis.levyApiMock.data

import com.google.inject.ImplementedBy

@ImplementedBy(classOf[SystemTimeSource])
trait TimeSource {
  def currentTimeMillis(): Long
}

class SystemTimeSource extends TimeSource {
  override def currentTimeMillis() = System.currentTimeMillis()
}