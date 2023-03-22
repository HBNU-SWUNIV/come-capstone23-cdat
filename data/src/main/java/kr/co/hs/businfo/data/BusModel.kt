package kr.co.hs.businfo.data

interface BusModel {
    val busStation: String
    val busNo: Int
    val name: String

    val cleanCode: Int
}