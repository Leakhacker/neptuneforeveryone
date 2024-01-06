package me.neptune.mixin;

import me.neptune.cmd.CommandManager;
import me.neptune.module.modules.world.AutoSign;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin extends Screen  {
	@Shadow
	@Final
	private String[] messages;
	
	protected AbstractSignEditScreenMixin(Text title) {
		super(title);
	}
	
	@Inject(at = {@At("HEAD")}, method = {"init()V"})
	private void onInit(CallbackInfo ci)
	{
		AutoSign mod = AutoSign.INSTANCE;
		String[] newText = mod.getText();
		if(newText != null) {
			for(int i = 0; i < 4; i++)
				messages[i] = newText[i];
			finishEditing();
		}
	}
	
	@Inject(at = { @At("HEAD") }, method = "finishEditing()V")
	private void onEditorClose(CallbackInfo ci) {
		AutoSign mod = AutoSign.INSTANCE;
		if(mod.isOn()) {
			if(mod.getText() == null) {
				mod.setText(messages);
				CommandManager.sendChatMessage("Sign text set!");
			}
		}
	}
	
	@Shadow
	private void finishEditing()
	{
		
	}
}
