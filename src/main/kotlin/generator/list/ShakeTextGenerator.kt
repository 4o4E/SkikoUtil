package top.e404.skiko.generator.list

import org.jetbrains.skia.*
import top.e404.skiko.*
import top.e404.skiko.draw.Pointer
import top.e404.skiko.generator.ImageGenerator
import kotlin.random.Random

object ShakeTextGenerator : ImageGenerator {
    private const val fontSpace = 5
    private const val padding = 10
    private const val fontSize = 60
    private val font = FontType.YAHEI.getSkijaFont(fontSize.toFloat())


    /**
     * 生成抖动gif
     *
     * @param s 文字
     * @param fontColor 文字颜色
     * @param bgColor 背景颜色
     * @param shakeSize 抖动幅度
     * @param f 生成的gif的总帧数
     * @return gif
     */
    private fun shakeGif(
        s: String,
        fontColor: Int,
        bgColor: Int,
        shakeSize: Int,
        f: Int,
    ) = (0..f).map {
        Frame(60, shake(s, fontColor, bgColor, shakeSize))
    }.encodeToBytes()

    /**
     * 获得一张抖动过的图片
     *
     * @param text 文本输入
     * @param fontColor 文字颜色
     * @param bgColor 背景颜色
     * @return 图片
     */
    private fun shake(text: String, fontColor: Int, bgColor: Int, shakeSize: Int): Bitmap {
        val map = text.map {
            TextLine.make(it.toString(), font)
        }.associateWith {
            it.width
        }
        val w = fontSpace + map.values.sumOf { it.toDouble() + fontSpace }.toInt()
        return Surface.makeRasterN32Premul(w + 2 * (padding + shakeSize), fontSize + 2 * (padding + shakeSize)).run {
            val p = Pointer(padding + 5, padding + fontSize - 10)
            val paint = Paint().apply { color = bgColor }
            canvas.apply {
                drawRect(Rect.makeXYWH(0F, 0F, width.toFloat(), height.toFloat()), paint)
                for (c in map.keys) {
                    drawTextLine(c, p.x + random(shakeSize), p.y + random(shakeSize), paint.apply {
                        color = fontColor
                    })
                    p.x += map[c]!!.toInt() + fontSpace
                }
            }
            makeImageSnapshot().toBitmap()
        }
    }

    private fun random(shakeSize: Int) = Random.Default.nextInt(shakeSize * 2).toFloat()

    override suspend fun generate(data: ExtraData?): ByteArray {
        val (text, color, bg, size, frameCount) = data as ShakeTextData
        return shakeGif(text, color, bg, size, frameCount)
    }

    data class ShakeTextData(
        val text: String,
        val color: Int,
        val bg: Int,
        val size: Int,
        val frameCount: Int,
    ) : ExtraData
}