package kr.co.hs.businfo.data.model.daejeonbusrouteinfo

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "msgHeader", strict = false)
data class Header(
    @field:Element(name = "currentPage")
    var currentPage: Int,
    @field:Element(name = "headerCd")
    var headerCd: Int,
    @field:Element(name = "headerMsg")
    var headerMsg: String,
    @field:Element(name = "itemCnt")
    var itemCnt: Int,
    @field:Element(name = "itemPageCnt")
    var itemPageCnt: Int
) {
    constructor() : this(0, 0, "", 0, 0)
}
