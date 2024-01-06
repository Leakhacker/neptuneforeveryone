package me.neptune.mixin;

import me.neptune.Neptune;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.screen.ChatScreen;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
	// 
	
	@Inject(at = {
			@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addToMessageHistory(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.AFTER) }, method = "sendMessage(Ljava/lang/String;Z)Z", cancellable = true)
	public void onSendMessage(String message, boolean addToHistory, CallbackInfoReturnable<Boolean> cir) {
		if (message.startsWith(Neptune.PREFIX)) {
			Neptune.COMMAND.command(message.split(" "));
			cir.setReturnValue(true);
		}
	}
}
