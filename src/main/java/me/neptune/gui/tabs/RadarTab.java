package me.neptune.gui.tabs;

import me.neptune.Neptune;
import me.neptune.gui.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;

public class RadarTab extends ClickGuiTab {
	
	float distance = 50;
	public RadarTab(String title, int x, int y) {
		super(title, x, y);
		this.setWidth(190);
		this.setHeight(190);
		this.inheritHeightFromChildren = false;
	}

	
	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		MatrixStack matrixStack = drawContext.getMatrices();
		if(drawBorder) {
			// Draws background depending on components width and height
		    renderUtils.drawOutlinedBox(matrixStack, x, y, width, 29, new Color(30,30,30), 0.4f);
			renderUtils.drawString(drawContext, this.title, x + 8, y + 8, Neptune.HUD.getColor());
			renderUtils.drawOutlinedBox(matrixStack, x, y + 29, width, height, new Color(30,30,30), 0.4f);
		
			
			// Draw the 'Radar'
			renderUtils.drawBox(matrixStack, x , y + 30 + (height / 2), width - 1, 1, new Color(128,128,128), 1.0f);
			renderUtils.drawBox(matrixStack, x + (width / 2), y + 30, 1, height, new Color(128,128,128), 1.0f);
			renderUtils.drawBox(matrixStack, x + (width / 2) - 2, y + 30 + (height / 2) - 2, 5, 5, Neptune.HUD.getColor(), 1.0f);
			
			float sin_theta = (float) Math.sin(Math.toRadians(-mc.player.getRotationClient().y));
			float cos_theta = (float) Math.cos(Math.toRadians(-mc.player.getRotationClient().y));
			
			int center_x = x + (width / 2);
			int center_y = y + 28 + (height / 2);
			
			// Render Entities
			for (Entity entity : mc.world.getEntities()) {
				Color c ;
				if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
					if (entity instanceof AnimalEntity) {
						c = new Color(0, 255, 0);
					} else if (entity instanceof Monster) {
						c = new Color(255, 0, 0);
					} else {
						c = new Color(0, 0, 255);
					}
				}else {
					continue;
				}
				
				float ratio_x = (float)((entity.getX() - mc.player.getX())) / (distance);
				float ratio_y = (float)((entity.getZ() - mc.player.getZ())) / (distance);
				
				float fake_x = (x + ((float) width / 2) - (width * ratio_x / 2));
				float fake_y = (y + 28 + ((float) height / 2) - (width * ratio_y / 2));
				
				float radius_x = ((cos_theta * (fake_x - center_x)) - (sin_theta * (fake_y - center_y))) + center_x;
				float radius_y = (sin_theta * (fake_x - center_x)) + (cos_theta * (fake_y - center_y)) + center_y;
				
				renderUtils.drawBox(matrixStack, (int)(Math.min(x + width - 5, Math.max(x, radius_x))) , (int)(Math.min(y + 25 + height, Math.max(y + 30, radius_y))), 3, 3, c, 1.0f);
			}
			
			// Render Players
			for (AbstractClientPlayerEntity entity : mc.world.getPlayers()) {
				if(entity != mc.player) {
					float ratio_x = (float)((entity.getX() - mc.player.getX())) / (distance);
					float ratio_y = (float)((entity.getZ() - mc.player.getZ())) / (distance);
					
					float fake_x = (x + ((float) width / 2) - (width * ratio_x / 2));
					float fake_y = (y + 28 + ((float) height / 2) - (width * ratio_y / 2));
					
					float radius_x = ((cos_theta * (fake_x - center_x)) - (sin_theta * (fake_y - center_y))) + center_x;
					float radius_y = (sin_theta * (fake_x - center_x)) + (cos_theta * (fake_y - center_y)) + center_y;
					
					renderUtils.drawBox(matrixStack, (int)(Math.min(x + width - 5, Math.max(x, radius_x))), (int)(Math.min(y + 25 + height, Math.max(y + 30, radius_y))), 3, 3, new Color(255, 255, 255), 1.0f);
					renderUtils.drawStringWithScale(drawContext, entity.getName().getString(), (int)(Math.min(x + width - 5, Math.max(x, radius_x))) - (mc.textRenderer.getWidth(entity.getName()) * 0.5f), (int)(Math.min(y + 25 + height, Math.max(y + 30, radius_y))) - 10, color, 1.0f);
				}
			}
			
			if (this.isPinned) {
				renderUtils.drawOutlinedBox(matrixStack, x + width - 24, y + 4, 20, 20, color, 0.8f);
			} else {
				renderUtils.drawOutlinedBox(matrixStack, x + width - 24, y + 4, 20, 20, new Color(128,128,128), 0.2f);
			}
		}
	}
}