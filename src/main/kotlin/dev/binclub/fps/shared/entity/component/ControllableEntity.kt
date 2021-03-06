@file:Suppress("NON_EXHAUSTIVE_WHEN")

package dev.binclub.fps.shared.entity.component

import cookiedragon.eventsystem.EventDispatcher.register
import dev.binclub.fps.client.input.KeyEvent
import dev.binclub.fps.client.input.MouseMoveEvent
import dev.binclub.fps.client.utils.glfw.Key
import dev.binclub.fps.shared.entity.Entity
import dev.binclub.fps.shared.utils.clampAssign
import dev.binclub.fps.shared.utils.clampOverflow
import dev.binclub.fps.shared.utils.clampOverflowAssign
import glm_.d
import glm_.f
import glm_.func.common.clamp
import glm_.func.cos
import glm_.func.rad
import glm_.func.sin

/**
 * @author cookiedragon234 09/Jul/2020
 */
class ControllableEntity: TickableEntity() {
	private var strafeForwards = 0f
	private var strafeRight = 0f
	var jump = 0f
	
	private val friction = 50f
	
	override fun attachTo(entity: Entity) {
		val positioned = entity.requireComponent<PositionedEntity>() // only allow attaching to positioned entities
		register { event: MouseMoveEvent ->
			positioned.rotation.x = (event.delta.x + positioned.rotation.x).clamp(-90f, 90f)
			positioned.rotation.y = (event.delta.y + positioned.rotation.y).clampOverflow(0f, 360f)
		}
	}
	
	fun updateControls() {
		strafeForwards = Key.W.power - Key.S.power
		strafeRight = Key.D.power - Key.A.power
		jump = Key.SPACE.power - Key.LEFT_CONTROL.power
	}
	
	fun move(offsetX: Float, offsetY: Float, offsetZ: Float) {
		val positioned = entity.requireComponent<PositionedEntity>()
		if (offsetX != 0f) {
			positioned.position.x += (positioned.rotation.y - 90f).d.rad.sin.f * -1.0f * offsetX / friction
			positioned.position.z += (positioned.rotation.y - 90f).d.rad.cos.f * offsetX / friction
		}
		if (offsetZ != 0f) {
			positioned.position.x += positioned.rotation.y.d.rad.sin.f * -1.0f * offsetZ / friction
			positioned.position.z += positioned.rotation.y.d.rad.cos.f * offsetZ / friction
		}
		if (offsetY != 0f) {
			positioned.position.y += offsetY / friction
		}
	}
	
	override fun tick() {
		updateControls()
		move(strafeRight, jump, -strafeForwards)
	}
}
