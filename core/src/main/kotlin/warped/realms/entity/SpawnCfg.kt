package warped.realms.entity

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.utils.Scaling
import ktx.math.vec2
import warped.realms.component.AnimationModel

const val DEFAULT_SPEED = 3f
const val DEFAULT_ATTACK_DAMAGE = 5f
const val DEFAULT_LIFE = 15

class SpawnCfg(
    val model: AnimationModel,
    val speedScaling: Float = 1f,
    val canAttack: Boolean = true,
    val attackScaling: Float = 1f,
    val attackDelay: Float = 0.2f,
    val attackExtraRange: Float = 0f,
    val lifeScaling: Float = 1f,
    val physicScaling: Vector2 = vec2(1f,1f),
    val physicOffset: Vector2 = vec2(0f, 0f),
    val bodyType: BodyType = BodyType.DynamicBody
)

class SpawnComponent(
    var type: String = "",
    var location: Vector2 = vec2()
)
