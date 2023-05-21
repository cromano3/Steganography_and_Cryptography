package cryptography
import java.awt.image.BufferedImage
import java.io.File
import java.util.Scanner
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Color.blue
import java.awt.Color.green
import java.awt.Color.red
import java.io.IOException
import kotlin.experimental.xor

fun main() {
    val scanner = Scanner(System.`in`)
    while(true){
        println("Task (hide, show, exit):")
        when(scanner.nextLine()){
            "hide" -> hide()
            "show" -> show()
            "exit" -> {println("Bye!"); break}
            else -> println("Wrong task: [input String]")
        }

    }
}

fun show(){

    val scanner = Scanner(System.`in`)
    println("Input image file:")
    val inputFileName = scanner.nextLine()
    println("Password:")
    val password = scanner.nextLine()

    try{

        val inputFile = File(inputFileName)
        val myImage: BufferedImage = ImageIO.read(inputFile)

        var currentByte = ""
        var bitCounter = 0

        var result = mutableListOf<String>()


        var potentialEnd = 0
        var isEnd = false

        end@for(y in 0 until myImage.height){
            for(x in 0 until myImage.width){

                val color = Color(myImage.getRGB(x,y))

                val bit = color.blue and 1

                currentByte += bit

                if(bitCounter == 7){
                    bitCounter = 0
                    result.add(currentByte)
                    if(currentByte == "00000000"){
                        if(potentialEnd < 2){
                            potentialEnd += 1
                        }
                    }
                    else if (currentByte == "00000011"){
                        if(potentialEnd == 2){
                            isEnd = true
                            break@end
                        }
                        else{
                            potentialEnd = 0
                        }
                    }
                    else{
                        potentialEnd = 0
                    }
                    currentByte = ""
                }
                else{
                    bitCounter++
                }

            }
        }

        var counter = 0
        var output = ""
        val passwordByteArray = password.encodeToByteArray()

        repeat(3) {
            result.removeLast()
        }

        for(item in result){
            val r = item.toInt(2).toByte() xor passwordByteArray[counter]
            val byteArray = byteArrayOf(r)
            output += byteArray.toString(Charsets.UTF_8)
            counter++
            if(counter >= passwordByteArray.size){
                counter = 0
            }
        }

        println("Message:")
        println(output)

    }
    catch(e: IOException){
        println("Can't read input file!")
    }










}

fun hide(){
    val scanner = Scanner(System.`in`)

    println("Input image file:")
    val inputFileName = scanner.nextLine()
    println("Output image file:")
    val outputFileName = scanner.nextLine()

    println("Message to hide:")
    val secretMessage = scanner.nextLine()

    println("Password:")
    val password = scanner.nextLine()

    var messageByteArray = secretMessage.encodeToByteArray()
    val passwordByteArray = password.encodeToByteArray()

    var counter = 0

    for(x in messageByteArray.indices){
       messageByteArray[x] = messageByteArray[x] xor passwordByteArray[counter]
        counter++
        if(counter >= passwordByteArray.size){
            counter = 0
        }
    }

    messageByteArray = messageByteArray + 0.toByte() + 0.toByte() + 3.toByte()

    val bitsets = ArrayList<String>()

    for (i in messageByteArray) {
        bitsets.add((String.format("%8s", i.toInt().toString(2)).replace(' ', '0')))
    }


    val bitString = bitsets.joinToString(separator = "")


    try{

        val inputFile = File(inputFileName)
        val myImage: BufferedImage = ImageIO.read(inputFile)
        val imageSize = myImage.width * myImage.height

        if(imageSize < bitString.length){
            println("The input image is not large enough to hold this message.")
        }
        else{
            var bitCounter = 0
            var currentBit = bitString[bitCounter]

            start@for(y in 0 until myImage.height){
                for(x in 0 until myImage.width){

                    val color = Color(myImage.getRGB(x,y))

                    var blue = color.blue shr 1
                    blue = blue shl 1
                    blue = blue or currentBit.toString().toInt()

                    val newColor = Color(color.red, color.green, blue)
                    myImage.setRGB(x, y, newColor.rgb)


                    bitCounter++
                    if(bitCounter < bitString.length){
                        currentBit = bitString[bitCounter]
                    }
                    else{
                        break@start
                    }

                }
            }

            val outputFile = File(outputFileName)
            ImageIO.write(myImage, "png", outputFile)

            println("Message saved in $outputFileName image.")
        }

    }
    catch(e: IOException){
        println("Can't read input file!")
    }



}