package tw.edu.ncku.iim.rsliu.setcard

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.log
import kotlin.random.Random

// 定義可能的形狀、形狀數量、陰影和顏色
val shapes =
    arrayOf(SetCardView.Shape.OVAL, SetCardView.Shape.DIAMOND, SetCardView.Shape.WORM)
val shapeCounts = intArrayOf(1, 2, 3)
val shadings =
    arrayOf(SetCardView.Shading.EMPTY, SetCardView.Shading.SOLID, SetCardView.Shading.STRIP)
val colors = arrayOf(
    Color.RED,
    Color.GREEN,
    Color.parseColor("#800080")
)

class SharedViewModel : ViewModel() {
    var rows = 4
    val columns = 3
    val deck = mutableListOf<CardsData>() // 存儲牌組
    var cards = mutableListOf<MutableList<CardsData>>() // 存儲桌面上的牌組
    var backgroundColor = mutableListOf<MutableList<Int>>() // 背景顏色
    val selectedCardIdxs = mutableListOf<Int>() //選中的三張

    var history = mutableListOf<MutableList<CardsData>>() // 存歷史紀錄的
    val historyLiveData: MutableLiveData<List<List<CardsData>>> = MutableLiveData() // livedata

    // 初始化 ViewModel
    init {
        init_deck()
        init_cards()
        selectedCardIdxs.addAll(listOf(-1, -1, -1))
        // 初始history
        for(i in 0 until rows){
            val arr = mutableListOf<Int>()
            arr.add(Color.WHITE)
            arr.add(Color.WHITE)
            arr.add(Color.WHITE)
            backgroundColor.add(arr)
        }
    }

    private fun init_deck() {
        for (color in colors) {
            for (shapeCount in shapeCounts) {
                for (shape in shapes) {
                    for (shading in shadings) {
                        deck.add(CardsData(color, shapeCount, shape, shading))
                    }
                }
            }
        }
    }
    private fun init_cards() {
        val random = Random
        for(i in 0 until rows){
            val drawnCards = mutableListOf<CardsData>()
            for(j in 0 until columns){
                val drawnIndex = random.nextInt(deck.size)
                val drawnCard = deck[drawnIndex]
                drawnCards.add(drawnCard)
                deck.removeAt(drawnIndex)
            }
            cards.add(drawnCards)
        }
    }
    fun update_selectedCardsIdxs(selectedCardIdx: Int){
        if(backgroundColor[selectedCardIdx / columns][selectedCardIdx % columns] != Color.BLACK){
            val existSameIdx = selectedCardIdxs.indexOf(selectedCardIdx)
            if(existSameIdx == -1){ //沒有重複
                val index = selectedCardIdxs.indexOfFirst { it < 0 }
                if (index != -1) {
                    selectedCardIdxs[index] = selectedCardIdx
                    val isThreePositiveIntegers = selectedCardIdxs.all { it >= 0 }
                    if(isThreePositiveIntegers){
                        if(deck.isNotEmpty()){
                            if(checkSelectedCardValidation(selectedCardIdxs)){ //合格
                                add_historys(selectedCardIdxs)
                                redealThreeCard(selectedCardIdxs)
                                selectedCardIdxs.fill(-1)
                            }else{ //不合格
                                selectedCardIdxs.fill(-1)
                            }
                        }else{
                            if(checkSelectedCardValidation(selectedCardIdxs)){ //合格
                                add_historys(selectedCardIdxs)
                                turnBlack_three_backgroundColor(selectedCardIdxs)
                                selectedCardIdxs.fill(-1)
                            }else{ //不合格
                                selectedCardIdxs.fill(-1)
                            }
                        }
                    }
                }
            }else{
                selectedCardIdxs[existSameIdx] = -1
            }
            update_backgroundColor(selectedCardIdxs);
        }

    }

    fun update_backgroundColor(cardIdxs: MutableList<Int>){
        turnWhite_backgroundColor()
        for(i in 0 until 3){
            if(cardIdxs[i] != -1){
                if(backgroundColor[cardIdxs[i] / columns][cardIdxs[i] % columns] != Color.BLACK){
                    backgroundColor[cardIdxs[i] / columns][cardIdxs[i] % columns] = Color.YELLOW
                }

            }
        }
    }
    fun turnWhite_backgroundColor(){
        for(i in 0 until rows){
            for(j in 0 until columns){
                if(backgroundColor[i][j] != Color.BLACK){
                    backgroundColor[i][j] = Color.WHITE
                }

            }
        }
    }
    fun turnBlack_three_backgroundColor(cardIdxs: MutableList<Int>){
        for(i in 0 until 3){
            backgroundColor[cardIdxs[i] / columns][cardIdxs[i] % columns] = Color.BLACK
        }
    }

    // 隨機從牌堆抽出一張
    private fun update_cards(row:Int, column:Int){
        val random = Random
        val drawnIndex = random.nextInt(deck.size)
        val drawnCard = deck[drawnIndex]
        cards[row][column] = drawnCard
        deck.removeAt(drawnIndex)
    } // 對桌面其中三張發牌
    fun redealThreeCard(cardIdxs: MutableList<Int>){
        for(i in 0 until 3){
            update_cards(cardIdxs[i] / columns, cardIdxs[i] % columns)
        }
    }

    // 加一排
    fun addCards(){
        if(!deck.isEmpty()){
            rows += 1;

            val random = Random
            val drawnCards = mutableListOf<CardsData>()
            for(i in 0 until columns){
                val drawnIndex = random.nextInt(deck.size)
                val drawnCard = deck[drawnIndex]
                drawnCards.add(drawnCard)
                deck.removeAt(drawnIndex)
            }

            cards.add(drawnCards)

            val arr = mutableListOf<Int>()
            arr.add(Color.WHITE)
            arr.add(Color.WHITE)
            arr.add(Color.WHITE)
            backgroundColor.add(arr)
        }
    }

    // history加一排
    fun add_historys(cardIdxs: MutableList<Int>){
        val arr = mutableListOf<CardsData>()
        for(i in 0 until 3){
            arr.add(cards[cardIdxs[i] / columns][cardIdxs[i] % columns])
        }
        history.add(arr)
        historyLiveData.value = history.toList()
    }

    fun checkSelectedCardValidation(selectedCardIdxs: MutableList<Int>): Boolean {
        var selectionValidation = false
        if (checkShapeCountValidation(selectedCardIdxs) && checkShapeValidation(selectedCardIdxs) && checkCardColorValidation(selectedCardIdxs) && checkShadingValidation(selectedCardIdxs)) {
            selectionValidation = true
        }
        return selectionValidation
    }
    fun checkShapeCountValidation(selectedCardIdxs: MutableList<Int>): Boolean {
        val card1 = cards[selectedCardIdxs[0] / columns][selectedCardIdxs[0] % columns]
        val card2 = cards[selectedCardIdxs[1] / columns][selectedCardIdxs[1] % columns]
        val card3 = cards[selectedCardIdxs[2] / columns][selectedCardIdxs[2] % columns]

        var selectionValidation = false

        if (card1.shapeCount == card2.shapeCount && card2.shapeCount == card3.shapeCount) {
            selectionValidation = true
        }

        if (card1.shapeCount != card2.shapeCount && card2.shapeCount != card3.shapeCount && card1.shapeCount != card3.shapeCount) {
            selectionValidation = true
        }

        return selectionValidation
    }
    fun checkShapeValidation(selectedCardIdxs: MutableList<Int>): Boolean {
        val card1 = cards[selectedCardIdxs[0] / columns][selectedCardIdxs[0] % columns]
        val card2 = cards[selectedCardIdxs[1] / columns][selectedCardIdxs[1] % columns]
        val card3 = cards[selectedCardIdxs[2] / columns][selectedCardIdxs[2] % columns]

        var selectionValidation = false

        if (card1.shape == card2.shape && card2.shape == card3.shape) {
            selectionValidation = true
        }

        if (card1.shape != card2.shape && card2.shape != card3.shape && card1.shape != card3.shape) {
            selectionValidation = true
        }

        return selectionValidation
    }
    fun checkCardColorValidation(selectedCardIdxs: MutableList<Int>): Boolean {
        val card1 = cards[selectedCardIdxs[0] / columns][selectedCardIdxs[0] % columns]
        val card2 = cards[selectedCardIdxs[1] / columns][selectedCardIdxs[1] % columns]
        val card3 = cards[selectedCardIdxs[2] / columns][selectedCardIdxs[2] % columns]

        var selectionValidation = false

        if (card1.cardColor == card2.cardColor && card2.cardColor == card3.cardColor) {
            selectionValidation = true
        }

        if (card1.cardColor != card2.cardColor && card2.cardColor != card3.cardColor && card1.cardColor != card3.cardColor) {
            selectionValidation = true
        }

        return selectionValidation
    }
    fun checkShadingValidation(selectedCardIdxs: MutableList<Int>): Boolean {
        val card1 = cards[selectedCardIdxs[0] / columns][selectedCardIdxs[0] % columns]
        val card2 = cards[selectedCardIdxs[1] / columns][selectedCardIdxs[1] % columns]
        val card3 = cards[selectedCardIdxs[2] / columns][selectedCardIdxs[2] % columns]

        var selectionValidation = false

        if (card1.shading == card2.shading && card2.shading == card3.shading) {
            selectionValidation = true
        }

        if (card1.shading != card2.shading && card2.shading != card3.shading && card1.shading != card3.shading) {
            selectionValidation = true
        }

        return selectionValidation
    }

    // 幫我找
    fun findValidSelection(){
        var existValidation:Boolean = false
        for (i in 0 until rows * columns) {
            for (j in 0 until rows * columns) {
                for (k in 0 until rows * columns) {
                    if (i != j && i != k && j != k) {
                        val arr = mutableListOf<Int>(0, 0, 0)
                        arr[0] = i
                        arr[1] = j
                        arr[2] = k
                        if (checkSelectedCardValidation(arr)) {
                            existValidation = true
                            println("valid set: ${i + 1} ${j + 1} ${k + 1}")
                            Log.i("i",(i + 1).toString())
                            Log.i("j",(j + 1).toString())
                            Log.i("k",(k + 1).toString())
                        }
                    }
                }
            }
        }
        if(!existValidation){ //找不到
            println("No valid set\n")
            addCards()
        }
    }

    fun restartGame(){
        deck.clear()
        for (color in colors) {
            for (shapeCount in shapeCounts) {
                for (shape in shapes) {
                    for (shading in shadings) {
                        deck.add(CardsData(color, shapeCount, shape, shading))
                    }
                }
            }
        }

        rows = 4;

        if (cards.size > 4) {
            val subListsToRemove = cards.subList(4, cards.size)
            subListsToRemove.clear()
        }
        if (backgroundColor.size > 4) {
            val subListsToRemove = backgroundColor.subList(4, backgroundColor.size)
            subListsToRemove.clear()
        }

        val random = Random
        for(i in 0 until rows){
            val drawnCards = mutableListOf<CardsData>()
            for(j in 0 until columns){
                val drawnIndex = random.nextInt(deck.size)
                val drawnCard = deck[drawnIndex]
                drawnCards.add(drawnCard)
                deck.removeAt(drawnIndex)
            }
            cards[i] = drawnCards
        }

        for(i in 0 until rows){
            val arr = mutableListOf<Int>()
            arr.add(Color.WHITE)
            arr.add(Color.WHITE)
            arr.add(Color.WHITE)
            backgroundColor[i] = arr
        }

        history.clear()
        historyLiveData.value = history.toList()
    }
}