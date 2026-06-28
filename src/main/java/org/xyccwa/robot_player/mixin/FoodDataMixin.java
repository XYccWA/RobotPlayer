package org.xyccwa.robot_player.mixin;

import net.minecraft.world.food.FoodData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodData.class)
public class FoodDataMixin {

    // 拦截消耗度的增加 - 这是最关键的！
    @Inject(method = "addExhaustion", at = @At("HEAD"), cancellable = true)
    private void onAddExhaustion(float exhaustion, CallbackInfo ci) {
        // 直接取消，不让任何消耗度积累
        ci.cancel();
    }

    // 拦截生命恢复逻辑（每 tick 检查是否回血）
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(Player player, CallbackInfo ci) {
        // 完全阻断 tick 中的食物逻辑，包括：
        // 1. 饱食度满时的生命恢复
        // 2. 饱食度归零时的饥饿伤害
        // 3. 饱和度降低的计时
        ci.cancel();
    }

    // 拦截食物效果的应用（吃食物时的恢复）
    @Inject(method = "eat", at = @At("HEAD"), cancellable = true)
    private void onEat(int foodLevel, float saturationModifier, CallbackInfo ci) {
        // 吃食物时，不增加任何饥饿值和饱和度
        ci.cancel();
    }

    // 拦截饱和度获取（可选，防止其他地方读取时做额外判断）
    @Inject(method = "getSaturationLevel", at = @At("HEAD"), cancellable = true)
    private void onGetSaturationLevel(CallbackInfoReturnable<Float> cir) {
        // 永远返回 0 饱和度
        cir.setReturnValue(0.0f);
    }

    // 拦截饥饿值获取（可选）
    @Inject(method = "getFoodLevel", at = @At("HEAD"), cancellable = true)
    private void onGetFoodLevel(CallbackInfoReturnable<Integer> cir) {
        // 可以返回一个固定值，比如 10（半饱），防止 UI 显示空槽
        // 或者返回实际值，但保持逻辑不饿死就行
        // 这里我们不拦截，让 UI 正常显示，但实际逻辑已被阻断
    }

    // 拦截是否饥饿的判断（防止触发饥饿状态）
    @Inject(method = "needsFood", at = @At("HEAD"), cancellable = true)
    private void onNeedsFood(CallbackInfoReturnable<Boolean> cir) {
        // 永远返回 false，表示不需要食物
        cir.setReturnValue(false);
    }

    // 拦截饱和度恢复（自然恢复，虽然实际上已经被 tick 取消了）
    @Inject(method = "setSaturation", at = @At("HEAD"), cancellable = true)
    private void onSetSaturation(float saturation, CallbackInfo ci) {
        // 阻止外部设置饱和度
        ci.cancel();
    }
}