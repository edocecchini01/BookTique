package com.example.booktique

import kotlinx.parcelize.Parcelize


object BookHolder {
    var book: VolumeDet? = null
    var libroInc: LibriInC?= null
    var libroDaL: LibriDaL?= null
    var libroL: LibriL?= null
}

object BooksHolder {
    var books: List<VolumeDet>? = null

}

/*
object LibriInc{
    var libroInc: LibriInC?= null
}

object LibriLetti{
    var libroLetto: LibriL?= null
}
*/
