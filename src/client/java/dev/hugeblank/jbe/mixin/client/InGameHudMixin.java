package dev.hugeblank.jbe.mixin.client;

import dev.hugeblank.jbe.MainInit;
import dev.hugeblank.jbe.entity.StaminaMount;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;
import net.minecraft.entity.JumpingMount;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    private static final Identifier STAMINA_BAR_BACKGROUND_TEXTURE = new Identifier(MainInit.ID, "hud/stamina_bar_background");
    @Unique
    private static final Identifier STAMINA_BAR_PROGRESS_TEXTURE = new Identifier(MainInit.ID, "hud/stamina_bar_progress");

    @Shadow @Final private MinecraftClient client;

    @Shadow private int scaledHeight;

    @Unique private static final int jbe$RENDER_MOUNT_OFFSET = 6;

    // Hell. Nudge everything up a bit
    @ModifyConstant(constant = @Constant(intValue = 39), method = "renderMountHealth")
    private int jbe$shiftMountHealthUp(int k) {
        if (this.client.player != null && this.client.player.getJumpingMount() != null) {
            return k + jbe$RENDER_MOUNT_OFFSET;
        } else {
            return k;
        }
    }

    @ModifyConstant(constant = @Constant(intValue = 32), method = "renderMountJumpBar")
    private int jbe$shiftJumpBarUp(int k) {
        if (this.client.player != null && this.client.player.getJumpingMount() != null) {
            return k + jbe$RENDER_MOUNT_OFFSET;
        } else {
            return k;
        }
    }

    @Unique
    public void jbe$renderMountStaminaBar(StaminaMount mount, DrawContext context, int x) {
        this.client.getProfiler().push("staminaBar");
        float f = (float) mount.jbe$getStamina()/6000;
        int j = (int)(f * 183.0F);
        int k = this.scaledHeight - 32 + 3;
        context.drawGuiTexture(STAMINA_BAR_BACKGROUND_TEXTURE, x, k, 182, 5);
        if (j > 0) {
            context.drawGuiTexture(STAMINA_BAR_PROGRESS_TEXTURE, 182, 5, 0, 0, x, k, 1, j, 5);
        }

        this.client.getProfiler().pop();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountJumpBar(Lnet/minecraft/entity/JumpingMount;Lnet/minecraft/client/gui/DrawContext;I)V"), method = "render", locals = LocalCapture.CAPTURE_FAILHARD)
    private void jbe$addStaminaBar(DrawContext context, float tickDelta, CallbackInfo ci, Window window, TextRenderer textRenderer, float f, float g, int i, JumpingMount jumpingMount) {
        jbe$renderMountStaminaBar((StaminaMount) jumpingMount, context, i);
    }
}
