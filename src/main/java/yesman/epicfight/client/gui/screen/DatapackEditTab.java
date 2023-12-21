package yesman.epicfight.client.gui.screen;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarratableEntry;

public abstract class DatapackEditTab extends GridLayout {
	static class DatapackEntryList extends ContainerObjectSelectionList<DatapackEntry> {
		public DatapackEntryList(Minecraft minecraft, int x, int y, int width, int height, int p_94447_) {
			super(minecraft, x, y, width, height, p_94447_);
		}
	}
	
	static class DatapackEntry extends ContainerObjectSelectionList.Entry<DatapackEditTab.DatapackEntry> {
		@Override
		public void render(GuiGraphics guiGraphics, int p_93524_, int p_93525_, int p_93526_, int p_93527_, int p_93528_, int p_93529_, int p_93530_, boolean p_93531_, float p_93532_) {
			
		}

		@Override
		public List<? extends GuiEventListener> children() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}