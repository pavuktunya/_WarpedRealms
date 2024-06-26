package warped.realms.system.update

import PutComponent
import com.badlogic.gdx.graphics.Camera
import ktx.tiled.height
import ktx.tiled.width
import warped.realms.component.ImageComponent
import warped.realms.event.Event
import warped.realms.event.IHandleEvent
import warped.realms.event.MapChangeEvent
import System
import Update
import kotlin.math.absoluteValue

@System
@Update(1)
class CameraSystem : IHandleEvent {
    private val camera: Camera = RenderSystem.stage.camera
    private val imageCmps: MutableList<ImageComponent> = mutableListOf()

    private var maxW = 0f
    private var maxH = 0f

    fun addTrecker(component: ImageComponent) {
        imageCmps.add(component)
    }
    private var prevX = 0f
    private var prevY = 0f

    private var newX = 0f
    private var newY = 0f

    private var setX = 0f
    private var setY = 0f
    fun Update(deltaTime: Float) {
//        val x = this.javaClass.getAnnotation(Update::class.java)?.priority
//        println("[UPDATE] ${this::class.simpleName} $x")

        val viewW = camera.viewportWidth * 0.5f
        val viewH = camera.viewportHeight * 0.5f

        prevX = camera.position.x
        prevY = camera.position.y

        if (imageCmps.isNotEmpty()) {
            with(imageCmps.last()) {
                newX = image.x + image.width * 0.5f
                newY = image.y + image.height * 0.5f
                if((prevX-newX).absoluteValue > 0.001f)
                {
                    setX = newX + (prevX-newX)*0.6f
                    setY = newY + (prevY-newY)*0.6f
                }
                else
                {
                    setX = newX
                    setY = newY
                }
                camera.position.set(
                    (setX).coerceIn(viewW, maxW - viewW),
                    (setY).coerceIn(viewH, maxH - viewH),
                    camera.position.z
                )
            }
        }
    }
    fun deleteComponent(component: ImageComponent) {
        imageCmps.remove(component)
        println("[DEBUG] Delete component ${component::class.simpleName} in ${this::class.simpleName}")
    }
    fun Dispose() {
        println("[DISPOSE] ${this::class.simpleName}")
    }
    override fun handle(event: Event): Boolean {
        when (event) {
            is MapChangeEvent -> {
                maxW = event.mapLoader.width.toFloat()
                maxH = event.mapLoader.height.toFloat()
                return true
            }
        }
        return false
    }
}
