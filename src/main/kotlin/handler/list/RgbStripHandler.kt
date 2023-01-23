package top.e404.skiko.handler.list

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.IRect
import top.e404.skiko.ahsb
import top.e404.skiko.apt.annotation.ImageHandler
import top.e404.skiko.frame.Frame
import top.e404.skiko.frame.FramesHandler
import top.e404.skiko.frame.HandleResult.Companion.result
import top.e404.skiko.frame.common
import top.e404.skiko.frame.replenish
import top.e404.skiko.util.pmapIndexed
import top.e404.skiko.util.toBitmap
import top.e404.skiko.util.toImage

/**
 * 带状RGB
 */
@ImageHandler
object RgbStripHandler : FramesHandler {
    override val name = "RgbStrip"
    override val regex = Regex("(?i)rgbstrip")
    override suspend fun handleFrames(
        frames: MutableList<Frame>,
        args: MutableMap<String, String>,
    ) = frames.common(args).replenish(10, Frame::limitAsGif).result {
        val img = first().image
        val reverse = args.containsKey("r") // 反向
        if (args.containsKey("h")) { // 纵向渐变
            val unitHeight = img.height / size // 变换的单位宽度
            return@result pmapIndexed { index ->
                val startY = unitHeight * (if (reverse) size - index else index) // 变换起点高度
                handleImage {
                    val bitmap = it.toBitmap()
                    val result = Bitmap().apply {
                        allocPixels(it.imageInfo)
                        setAlphaType(ColorAlphaType.PREMUL)
                    }
                    for (y in 0 until img.height) {
                        val currentY = (y + startY) % img.height // 当前处理的y
                        val addH = y.toFloat() / img.height // 增加的h
                        for (x in 0 until img.width) {
                            val pixel = bitmap.getColor(x, currentY)
                            var (a, h, s, b) = pixel.ahsb()
                            if (a == 0) {
                                result.erase(0, IRect.makeXYWH(x, currentY, 1, 1))
                                continue
                            }
                            h = (h + addH) % 1
                            result.erase(ahsb(a, h, s, b), IRect.makeXYWH(x, currentY, 1, 1))
                        }
                    }
                    result.toImage()
                }
            }
        }
        val unitWidth = img.width / size // 变换的单位宽度
        pmapIndexed { index ->
            val startX = unitWidth * (if (reverse) size - index else index) // 变换起点宽度
            handleImage {
                val bitmap = it.toBitmap() // 原图
                val result = Bitmap().apply { // 画板
                    allocPixels(it.imageInfo)
                    setAlphaType(ColorAlphaType.PREMUL)
                }
                for (x in 0 until img.width) {
                    val currentX = (x + startX) % img.width // 当前处理的x
                    val addH = x.toFloat() / img.width // 增加的h
                    for (y in 0 until img.height) {
                        val pixel = bitmap.getColor(currentX, y)
                        var (a, h, s, b) = pixel.ahsb()
                        if (a == 0) {
                            result.erase(0, IRect.makeXYWH(currentX, y, 1, 1))
                            continue
                        }
                        h = (h + addH) % 1
                        result.erase(ahsb(a, h, s, b), IRect.makeXYWH(currentX, y, 1, 1))
                    }
                }
                result.toImage()
            }
        }
    }
}