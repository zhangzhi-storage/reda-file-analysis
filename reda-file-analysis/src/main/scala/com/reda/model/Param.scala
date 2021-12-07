package com.reda.model

import com.reda.util.ValueUtils

import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Try}

object UnpackType extends Enumeration {
    val Bytes = Value("s")
    val Float = Value("f")
    val Int = Value("i")
    val UnsignedInt = Value("I")
    val StatusCode = Value("b")
    val statusCodeShort = Value("h")
}

class Param(val paramByte: Array[Byte],  val name: String = "") {
    var isValid = true
    val length = 0
    val isString = false
    var totalSeconds = 0

    var originalName = ""
    var freq = 0.0F
    var superFreq = 0
    var dataLength = 0
    var validDataStartPos = 0
    var dataType: UnpackType.Value = UnpackType.Bytes
    var rawDataType = 0
    var unitSize = 0
    var offset = 0
    var rawData: Array[Byte] = _
    var unpackFunc: (Array[Byte]) => Any = _
    var rawDataLength = 0
    init()

    private def init(): Unit = {
        if (null == paramByte || paramByte.length == 0) {
            isValid = false
            return
        }
        val tuples = Param.parse(paramByte)
        originalName = tuples._1
        freq = tuples._2
        superFreq = tuples._3
        dataLength = tuples._4
        validDataStartPos = tuples._5
        dataType = tuples._6
        rawDataType = tuples._7
        unitSize = tuples._8
        offset = tuples._9
        rawData = tuples._10

        dataType match {
            case UnpackType.Bytes => unpackFunc = ValueUtils.byte2string
            case UnpackType.Float => unpackFunc = ValueUtils.byte2float
            case UnpackType.Int => unpackFunc = ValueUtils.byte2int
            case UnpackType.UnsignedInt => unpackFunc = ValueUtils.byte2int
            case UnpackType.StatusCode => unpackFunc = ValueUtils.byte2int // maybe char?
            case UnpackType.statusCodeShort => unpackFunc = ValueUtils.byte2short
        }
        rawDataLength = rawData.length
        Try(totalSeconds = (rawDataLength / (freq * unitSize)).toInt) match {
            case Failure(_) => isValid = false
            case _ => None
        }
    }

    def apply(second: Int, position: Int = 0): Any = {
        var sec = second
        var pos = 0
        if (!isValid) {
            return null
        }
        if (freq < 1.0 || position < 0 || position >= freq) {
            pos = 0
        } else {
            pos = position
        }
        sec -= offset
        if (sec < 0) {
            sec = 0
        }
        val startOffset = (sec * freq + pos).toInt * unitSize
        val endOffset = startOffset + unitSize
        val sliceData = rawData.slice(startOffset, endOffset)
        unpackFunc(sliceData)
    }

    /**
     * get all frequency value between startSec to endSec
     *
     * @param startSec start second, start from 0
     * @param endSec   end second, maximum is totalSeconds
     * @return
     */
    def getAllValue(startSec: Int = 0, endSec: Int = -1): List[Any] = {
        val _startSec = math.max(0, startSec)
        val _endSec = if (endSec > 0) {
            endSec
        } else {
            totalSeconds - 1
        }
        val _finish = math.max(freq, 1.0).toInt
        val values = new ArrayBuffer[Any]()
        for (s <- _startSec to _endSec) {
            for (p <- 0 until _finish) {
                values += apply(s, p)
            }
        }
        values.toList
    }
}

object Param {
    private def parse(paramByte: Array[Byte]): (String, Float, Int, Int, Int, UnpackType.Value, Int, Int, Int, Array[Byte]) = {
        val originalName = ValueUtils.byte2string(paramByte.slice(0, 32))
        val rawDataType = paramByte(52).toInt
        val unitSize = paramByte(53).toInt
        val offset = paramByte(54).toInt - 1
        var dataType = UnpackType.Bytes
        rawDataType match {
            case 1 if unitSize == 4 => dataType = UnpackType.Float
            case 2 if unitSize == 4 => dataType = UnpackType.UnsignedInt
            case 3 if unitSize == 4 => dataType = UnpackType.Int
            case 5 if unitSize == 1 => dataType = UnpackType.Bytes
            case 5 if unitSize == 2 => dataType = UnpackType.statusCodeShort
            case 5 if unitSize == 4 => dataType = UnpackType.Int
            case 4 => dataType = UnpackType.Bytes
            case unknownType => println(s"Match error: $originalName,$unknownType,$unitSize")
        }
        var freq = ValueUtils.byte2int(paramByte.slice(32, 36)) / 64.0
        if (freq > 2048.0) {
            freq = freq / 1050628.0
        }
        //freq = if (freq >= 1.0) {
        //    freq.toInt
        //} else freq

        val dataLength = ValueUtils.byte2int(paramByte.slice(40, 48)) - 128
        val validDataStartPos = ValueUtils.byte2int(paramByte.slice(48, 52))
        val superFreq = ValueUtils.byte2int(paramByte.slice(36, 40))
        (
          originalName,
          freq.toFloat,
          superFreq,
          dataLength,
          validDataStartPos,
          dataType,
          rawDataType,
          unitSize,
          offset,
          paramByte.slice(128, paramByte.length)
        )
    }
}
