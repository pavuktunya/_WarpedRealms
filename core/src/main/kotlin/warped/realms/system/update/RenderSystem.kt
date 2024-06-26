package warped.realms.system.update

import PutComponent
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.log.logger
import warped.realms.component.ImageComponent
import warped.realms.event.Event
import warped.realms.event.IHandleEvent
import warped.realms.event.MapChangeEvent
import warped.realms.screen.GameScreen.Companion.UNIT_SCALE
import System
import Update
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.assets.toInternalFile
import warped.realms.system.Logger
import warped.realms.system.debug

@System
@Update(0)
@PutComponent(ImageComponent::class)
class RenderSystem : IHandleEvent {
    ///
    private var images = mutableListOf<ImageComponent>()

    private val bgdLayers = mutableListOf<TiledMapTileLayer>()
    private val fgdLayers = mutableListOf<TiledMapTileLayer>()
    private val imgLayers = mutableListOf<TiledMapImageLayer>()

    private val mapRenderer = OrthogonalTiledMapRenderer(null, UNIT_SCALE, stage.batch)
    private val orthoCam = stage.camera as OrthographicCamera

    fun Update(deltaTime: Float) {
//      println("TIME: ${1 / deltaTime}")
        with(stage) {
            viewport.apply()
            AnimatedTiledMapTile.updateAnimationBaseTime()
            mapRenderer.setView(orthoCam)
            //mapRenderer.render()
            renderImageLayer(imgLayers)
            renderTileLayer(bgdLayers)
            act(deltaTime)
            draw()
            renderTileLayer(fgdLayers)
        }
    }

    fun PutComponent(component: ImageComponent) {
        stage.addActor(
            component.image
        )
        images.add(component)
        images = images.sorted().toMutableList()
        images.forEach { it.image.toFront() }
        println("[DEBUG] Put component ${component::class.simpleName} in ${this::class.simpleName}")
    }
    fun DeleteComponent(component: ImageComponent) {
        stage.root.removeActor(component.image)
        println("[DEBUG] Delete component ${component::class.simpleName} in ${this::class.simpleName}")
    }
    fun Dispose() {
        println("[DISPOSE] ${this::class.simpleName}")
        textureAtlas.disposeSafely()
        stage.disposeSafely()
        mapRenderer.disposeSafely()
    }
    fun resize(width: Int, height: Int){
        stage.viewport.update(width,height, true)
    }
    override fun handle(event: Event): Boolean {
        when(event){
            is MapChangeEvent -> {
                bgdLayers.clear()
                fgdLayers.clear()
                event.mapLoader.layers.forEach { layer ->
                    if(layer !is (TiledMapTileLayer)) {
                        println("Layer not is tiledMapLayer")
                        if(layer is TiledMapImageLayer)
                        {
                            imgLayers.add(layer)
                        }
                        return@forEach
                    }
                    if(layer.name.startsWith("fgd_")){
                        Logger.debug { "Add fgdLayer." }

                        fgdLayers.add(layer)
                    }
                    else if(layer.name.startsWith("bgd_")){
                        Logger.debug { "Add bgdLayer." }

                        bgdLayers.add(layer)
                    }
                    else {
                        Logger.debug { "Not found layer in switch construction: ${layer.name}" }
                    }
                }
                return true;
            }
        }
        return false;
    }
    fun Stage.renderTileLayer(list: MutableList<TiledMapTileLayer>){
        if(list.isNotEmpty()){
            stage.batch.use(orthoCam.combined){
                list.forEach {
                    mapRenderer.renderTileLayer(it)
                }
            }
        }
    }
    fun Stage.renderImageLayer(list: MutableList<TiledMapImageLayer>){
        if(list.isNotEmpty()){
            stage.batch.use(orthoCam.combined){
                list.forEach {
                    mapRenderer.renderImageLayer(it)
                }
            }
        }
    }
    companion object{
        val textureAtlas: TextureAtlas = TextureAtlas("graphics/gameObject.atlas".toInternalFile())
        val stage: Stage = Stage(ExtendViewport(16f, 9f, 1920f, 1080f))
    }
}
