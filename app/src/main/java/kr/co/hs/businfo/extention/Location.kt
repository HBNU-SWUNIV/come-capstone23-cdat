package kr.co.hs.businfo.extention

import android.location.Location

object Location {
    fun Location.toFormattedString(): String = "위도 : ${this.latitude}, 경도 : ${this.longitude}"
}