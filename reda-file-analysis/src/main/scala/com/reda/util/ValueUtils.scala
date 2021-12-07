package com.reda.util


import java.nio.charset.StandardCharsets
import java.nio.{ByteBuffer, ByteOrder}


object ValueUtils {


  private def valueWrap(rawValue: Array[Byte]): ByteBuffer = {
    val buffer = ByteBuffer.wrap(rawValue)
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer
  }

  def byte2int(rawValue: Array[Byte]): Int = {
    valueWrap(rawValue).getInt
  }

  def byte2float(rawValue: Array[Byte]): Float = {
    /*if(rawValue.length>= 4){
      valueWrap(rawValue).getFloat
    }else{
      0.0F
    }*/
    valueWrap(rawValue).getFloat()

    /*DataInputStream dis=new DataInputStream(new ByteArrayInputStream(b));
    float f=dis.readFloat();*/
    /*val buffer = valueWrap(rawValue).flip()
    valueWrap(rawValue).getFloat*/
  }

  def byte2short(rawValue: Array[Byte]): Short = {
    valueWrap(rawValue).getShort
  }

  def byte2char(rawValue: Array[Byte]): Char = {
    valueWrap(rawValue).getChar
  }

  def byte2string(rawValue: Array[Byte]): String = {
    StandardCharsets.UTF_8.decode(valueWrap(rawValue)).toString.trim
  }
}
