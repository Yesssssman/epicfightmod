package yesman.epicfight.client.gui.datapack.screen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;

import io.netty.util.internal.StringUtil;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import yesman.epicfight.api.animation.AnimationProvider;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.AnimatedModelPlayer;
import yesman.epicfight.client.gui.datapack.widgets.ColorPreviewWidget;
import yesman.epicfight.client.gui.datapack.widgets.ComboBox;
import yesman.epicfight.client.gui.datapack.widgets.Grid;
import yesman.epicfight.client.gui.datapack.widgets.InputComponentList;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.datapack.widgets.ResizableEditBox;
import yesman.epicfight.client.gui.datapack.widgets.Static;
import yesman.epicfight.client.gui.datapack.widgets.SubScreenOpenButton;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;

@OnlyIn(Dist.CLIENT)
public class DatapackEditScreen extends Screen {
	private static DatapackEditScreen unsavedLastScreen;
	public static final Component GUI_EXPORT = Component.translatable("gui.epicfight.export");
	
	private GridLayout bottomButtons;
	private TabNavigationBar tabNavigationBar;
	private final Screen parentScreen;
	private final DatapackEditScreen.WeaponTypeTab weaponTab;
	private final DatapackEditScreen.ItemCapabilityTab itemCapabilityTab;
	private final DatapackEditScreen.MobPatchTab mobPatchTab;
	
	private final TabManager tabManager = new TabManager(this::addRenderableWidget, (p_267853_) -> {
		this.removeWidget(p_267853_);
	}) {
		@Override
		public void setCurrentTab(Tab tab, boolean playSound) {
			if (this.getCurrentTab() instanceof DatapackTab<?> datapackTab) {
				DatapackEditScreen.this.removeWidget(datapackTab.packListGrid);
				DatapackEditScreen.this.removeWidget(datapackTab.inputComponentsList);
			}
			
			super.setCurrentTab(tab, playSound);
			
			if (tab instanceof DatapackTab<?> datapackTab) {
				DatapackEditScreen.this.addRenderableWidget(datapackTab.packListGrid);
				DatapackEditScreen.this.addRenderableWidget(datapackTab.inputComponentsList);
			}
		}
	};
	
	public DatapackEditScreen(Screen parentScreen) {
		super(Component.translatable("gui." + EpicFightMod.MODID + ".datapack_edit"));
		
		this.parentScreen = parentScreen;
		this.minecraft = parentScreen.getMinecraft();
		this.font = parentScreen.getMinecraft().font;
		
		this.weaponTab = new DatapackEditScreen.WeaponTypeTab();
		this.itemCapabilityTab = new DatapackEditScreen.ItemCapabilityTab();
		this.mobPatchTab = new DatapackEditScreen.MobPatchTab();
	}
	
	public boolean importDataPack(Path path) {
		Pack.ResourcesSupplier pack$resourcessupplier = FolderRepositorySource.detectPackResources(path, false);
		
		if (pack$resourcessupplier != null) {
			String s = path.getFileName().toString();
			Pack pack = Pack.readMetaAndCreate("file/" + s, Component.literal(s), false, pack$resourcessupplier, PackType.SERVER_DATA, Pack.Position.TOP, PackSource.WORLD);
			PackResources packResources = pack.open();
			
			this.weaponTab.importEntries(packResources);
			this.itemCapabilityTab.importEntries(packResources);
			this.mobPatchTab.importEntries(packResources);
			ImportAnimationsScreen.importAnimations(packResources);
			
			return true;
		} else {
			this.minecraft.setScreen(new MessageScreen<>("Invalid datapack", "", this, (button2) -> this.minecraft.setScreen(this), 160, 60));
			return false;
		}
	}
	
	public boolean exportDataPack(String packName) {
		try {
			File resourcePackDirectory = Minecraft.getInstance().getResourcePackDirectory().toFile();
			File zipFile = new File(resourcePackDirectory, packName + ".zip");
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
			
			this.weaponTab.exportEntries(out);
			this.itemCapabilityTab.exportEntries(out);
			this.mobPatchTab.exportEntries(out);
			ImportAnimationsScreen.export(out);
			
			ZipEntry zipEntry = new ZipEntry("pack.mcmeta");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonObject root = new JsonObject();
			JsonObject pack = new JsonObject();
			
			int datapackVersion = SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA);
			int resourcepackVersion = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);
			
			if (datapackVersion != resourcepackVersion) {
				EpicFightMod.LOGGER.warn(new StringBuilder("Pack version is not matching in ").append(SharedConstants.getCurrentVersion().getId()).toString());
			}
			
			pack.addProperty("description", packName);
			pack.addProperty("pack_format", datapackVersion);
			root.add("pack", pack);
			out.putNextEntry(zipEntry);
			out.write(gson.toJson(root).getBytes());
			out.closeEntry();
			out.close();
			
			Util.getPlatform().openFile(resourcePackDirectory);
			
			return true;
		} catch (Exception e) {
			this.minecraft.setScreen(new MessageScreen<>("Failed to export datapack", e.getMessage(), this, (button2) -> this.minecraft.setScreen(this), 200, 110));
			e.printStackTrace();
			
			return false;
		}
	}
	
	@Override
	public void onFilesDrop(List<Path> filePath) {
		if (filePath.size() > 1) {
			this.minecraft.setScreen(new MessageScreen<>("", "Please select only one file", this, (button) -> this.minecraft.setScreen(this), 160, 50));
		}
		
		if (this.weaponTab.packList.size() > 0 || this.itemCapabilityTab.packList.size() > 0 || this.mobPatchTab.packList.size() > 0) {
			this.minecraft.setScreen(new MessageScreen<>("", "The current entries will be removed if you import the new data pack. Do you want to proceed?", this,
														(button) -> {
															if (this.importDataPack(filePath.get(0))) {
																this.minecraft.setScreen(this);
															}
														}, (button) -> this.minecraft.setScreen(this), 200, 90));
		} else {
			this.importDataPack(filePath.get(0));
		}
	}
	
	@Override
	protected void init() {
		// Enable stencil buffer to render a grid inside the area
		Minecraft.getInstance().getMainRenderTarget().enableStencil();
		
		this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width).addTabs(this.weaponTab, this.itemCapabilityTab, this.mobPatchTab).build();
		this.tabNavigationBar.selectTab(0, false);
		
	    this.addRenderableWidget(this.tabNavigationBar);
	    this.bottomButtons = (new GridLayout()).columnSpacing(10);
	    
		GridLayout.RowHelper gridlayout$rowhelper = this.bottomButtons.createRowHelper(2);
		gridlayout$rowhelper.addChild(Button.builder(GUI_EXPORT, (button) -> {
			this.minecraft.setScreen(new MessageScreen<>("", "Enter the pack title", this, null, 180, 70) {
				@Override
				protected void init() {
					this.parentScreen.init(this.minecraft, this.width, this.height);
					int height = this.messageBoxHeight / 2;
					
					final EditBox titleEditBox = new EditBox(this.font, this.width / 2 - 72, this.height / 2 - 6, 144, 16, Component.literal("pack_title_input_box"));
					
					this.addRenderableWidget(titleEditBox);
					this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (button$2) -> {
						if (DatapackEditScreen.this.exportDataPack(titleEditBox.getValue())) {
							DatapackEditScreen.this.minecraft.setScreen(DatapackEditScreen.this);
						}
					}).bounds(this.width / 2 - 56, this.height / 2 + height - 20, 55, 16).build());
					this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button$2) -> this.minecraft.setScreen(DatapackEditScreen.this)).bounds(this.width / 2 + 1, this.height / 2 + height - 20, 55, 16).build());
				}
			});
		}).build());
		gridlayout$rowhelper.addChild(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.onClose();
		}).build());
		
		this.bottomButtons.visitWidgets((button) -> {
			button.setTabOrderGroup(1);
			this.addRenderableWidget(button);
		});
		
		this.repositionElements();
		
		if (!this.initialized && unsavedLastScreen != null && unsavedLastScreen != this) {
			this.initialized = true;
			
			this.minecraft.setScreen(new MessageScreen<>("", "Would you like to load the previous pack?", this, (button) -> {
				this.minecraft.setScreen(unsavedLastScreen);
				ImportAnimationsScreen.clearUserAnimation();
				unsavedLastScreen = null;
			}, (button) -> {
				this.minecraft.setScreen(this);
				ImportAnimationsScreen.clearUserAnimation();
				unsavedLastScreen = null;
			}, 180, 70));
		}
	}
	
	@Override
	public void repositionElements() {
		if (this.tabNavigationBar != null && this.bottomButtons != null) {
			this.tabNavigationBar.setWidth(this.width);
			this.tabNavigationBar.arrangeElements();
			this.bottomButtons.arrangeElements();
			FrameLayout.centerInRectangle(this.bottomButtons, 0, this.height - 36, this.width, 36);
			int i = this.tabNavigationBar.getRectangle().bottom();
			ScreenRectangle screenrectangle = new ScreenRectangle(0, i, this.width, this.bottomButtons.getY() - i);
			this.tabManager.setTabArea(screenrectangle);
		}
	}
	
	@Override
	public void tick() {
		this.tabManager.getCurrentTab().tick();
	}
	
	@Override
	public void onClose() {
		if (this.weaponTab.packList.size() > 0 || this.itemCapabilityTab.packList.size() > 0 || this.mobPatchTab.packList.size() > 0) {
			unsavedLastScreen = this;
		}
		
		this.minecraft.setScreen(this.parentScreen);
		this.itemCapabilityTab.modelPlayer.onDestroy();
	}
	
	@Override
	public boolean keyPressed(int keycode, int p_100876_, int p_100877_) {
		if (this.tabNavigationBar.keyPressed(keycode)) {
			return true;
		} else if (super.keyPressed(keycode, p_100876_, p_100877_)) {
			return true;
		} else if (keycode != 257 && keycode != 335) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
		return this.getChildAt(mouseX, mouseY).filter((listener) -> {
			return listener.mouseDragged(mouseX, mouseY, button, dx, dy);
		}).isPresent();
	}
	
	@Override
	public void setFocused(@Nullable GuiEventListener target) {
		if (this.getFocused() == target) {
			return;
		}
		
		super.setFocused(target);
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics);
		guiGraphics.blit(CreateWorldScreen.FOOTER_SEPERATOR, 0, Mth.roundToward(this.height - 36 - 2, 2), 0.0F, 0.0F, this.width, 2, 32, 2);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}
	
	@OnlyIn(Dist.CLIENT)
	abstract class DatapackTab<T> extends GridLayoutTab {
		protected Grid packListGrid;
		protected InputComponentList<CompoundTag> inputComponentsList;
		protected final IForgeRegistry<T> registry;
		protected final Set<ResourceLocation> namesSet = Sets.newHashSet();
		protected final List<PackEntry<ResourceLocation, CompoundTag>> packList = Lists.newLinkedList();
		protected final String directory;
		
		public DatapackTab(Component title, String directory, @Nullable IForgeRegistry<T> registry) {
			super(title);
			
			this.directory = directory;
			
			ScreenRectangle screenRect = DatapackEditScreen.this.getRectangle();
			
			this.packListGrid = Grid.builder(DatapackEditScreen.this)
									.xy1(8, screenRect.top() + 14)
									.xy2(150, screenRect.height() - screenRect.top() - 7)
									.rowHeight(26)
									.rowEditable(true)
									.transparentBackground(true)
									.rowpositionChanged(this::packGridRowpositionChanged)
									.addColumn(Grid.editbox("pack_item")
													.editWidgetCreated((editbox) -> editbox.setFilter((str) -> ResourceLocation.isValidResourceLocation(str)))
													.valueChanged((event) -> this.packList.get(event.rowposition).setPackKey(new ResourceLocation(event.postValue)))
									.defaultVal(EpicFightMod.MODID + ":").editable(registry == null ? true : false).width(180))
									.pressAdd((grid, button) -> {
										if (registry != null) {
											DatapackEditScreen.this.minecraft.setScreen(new SelectFromRegistryScreen<>(DatapackEditScreen.this, registry, (selItem) -> {
												grid.setValueChangeEnabled(false);
												int rowposition = grid.addRowWithDefaultValues("pack_item", ParseUtil.getRegistryName(selItem, registry));
												this.packList.add(rowposition, PackEntry.of(new ResourceLocation(ParseUtil.getRegistryName(selItem, registry)), CompoundTag::new));
												grid.setGridFocus(rowposition, "pack_item");
												grid.setValueChangeEnabled(true);
											}));
										} else {
											grid.setValueChangeEnabled(false);
											int rowposition = grid.addRowWithDefaultValues("pack_item", EpicFightMod.MODID + ":");
											this.packList.add(rowposition, PackEntry.of(new ResourceLocation(EpicFightMod.MODID + ":"), CompoundTag::new));
											grid.setGridFocus(rowposition, "pack_item");
											grid.setValueChangeEnabled(true);
										}
										
										DatapackEditScreen.this.setFocused(grid);
									})
									.pressRemove((grid, button) -> {
										grid.removeRow((removedRow) -> this.packList.remove(removedRow));
										
										if (this.packList.size() == 0) {
											this.inputComponentsList.setComponentsActive(false);
										}
									})
									.build();
			
			this.registry = registry;
		}
		
		@Override
		public void doLayout(ScreenRectangle screenRectangle) {
			this.layout.arrangeElements();
			this.layout.setY(screenRectangle.top());
			
			this.packListGrid.updateSize(150, screenRectangle.height(), screenRectangle.top() + 14, screenRectangle.height() + 7);
			this.inputComponentsList.updateSize(screenRectangle.width() - 172, screenRectangle.height(), screenRectangle.top() + 14, screenRectangle.height() + 7);
			
			this.packListGrid.setLeftPos(8);
			this.inputComponentsList.setLeftPos(164);
		}
		
		@Override
		public void tick() {
			this.packListGrid._tick();
			this.inputComponentsList.tick();
		}
		
		public void clear() {
			this.packList.clear();
			this.packListGrid.reset();
			this.inputComponentsList.resetComponents();
			this.inputComponentsList.setComponentsActive(false);
		}
		
		public void packGridRowpositionChanged(int rowposition, Map<String, Object> values) {
			this.inputComponentsList.importTag(this.packList.get(rowposition).getPackValue());
		}
		
		public void importEntries(PackResources packResources) {
			packResources.getNamespaces(PackType.SERVER_DATA).stream().distinct().forEach((namespace) -> {
				packResources.listResources(PackType.SERVER_DATA, namespace, this.directory, (resourceLocation, stream) -> {
					try {
						JsonReader jsonReader = new JsonReader(new InputStreamReader(stream.get(), StandardCharsets.UTF_8));
						jsonReader.setLenient(true);
						JsonObject jsonObject = Streams.parse(jsonReader).getAsJsonObject();
						CompoundTag compTag = TagParser.parseTag(jsonObject.toString());
						ResourceLocation rl = new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath().replaceAll(this.directory, "").replaceAll(".json", ""));
						
						this.packList.add(PackEntry.of(rl, () -> compTag));
						this.packListGrid.addRowWithDefaultValues("pack_item", rl.toString());
					} catch (IOException | CommandSyntaxException e) {
						e.printStackTrace();
					}
				});
			});
		}
		
		public void exportEntries(ZipOutputStream out) throws IOException {
			for (PackEntry<ResourceLocation, CompoundTag> packEntry : this.packList) {
				ZipEntry zipEntry = new ZipEntry(String.format("data/%s/" + this.directory + "/%s.json", packEntry.getPackKey().getNamespace(), packEntry.getPackKey().getPath()));
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				out.putNextEntry(zipEntry);
				out.write(gson.toJson(CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, packEntry.getPackValue()).get().left().get()).getBytes());
				out.closeEntry();
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	class WeaponTypeTab extends DatapackTab<ResourceLocation> {
		public WeaponTypeTab() {
			super(Component.translatable("gui." + EpicFightMod.MODID + ".tab.datapack.weapon_type"), WeaponTypeReloadListener.DIRECTORY, null);
			
			Screen parentScreen = DatapackEditScreen.this;
			Font font = DatapackEditScreen.this.font;
			ScreenRectangle rect = DatapackEditScreen.this.getRectangle();
			
			this.inputComponentsList = new InputComponentList<>(DatapackEditScreen.this, 0, 0, 0, 0, 30) {
				@Override
				public void importTag(CompoundTag tag) {
					CompoundTag colliderTag = ParseUtil.getOrSupply(tag, "collider", CompoundTag::new);
					boolean centerInit = colliderTag.contains("center");
					boolean sizeInit = colliderTag.contains("size");
					
					if (!centerInit) {
						ListTag list = new ListTag();
						list.add(DoubleTag.valueOf(0.0D));
						list.add(DoubleTag.valueOf(0.0D));
						list.add(DoubleTag.valueOf(0.0D));
						colliderTag.put("center", list);
					}
					
					if (!sizeInit) {
						ListTag list = new ListTag();
						list.add(DoubleTag.valueOf(0.0D));
						list.add(DoubleTag.valueOf(0.0D));
						list.add(DoubleTag.valueOf(0.0D));
						colliderTag.put("size", list);
					}
					
					this.setComponentsActive(true);
					
					Grid.PackImporter packImporter = new Grid.PackImporter();
					
					for (String key : tag.getCompound("innate_skills").getAllKeys()) {
						packImporter.newRow();
						packImporter.newValue("style", Style.ENUM_MANAGER.get(key));
						packImporter.newValue("skill", SkillManager.getSkill(tag.getCompound("innate_skills").getString(key)));
					}
					
					this.setDataBindingComponenets(new Object[] {
						WeaponCategory.ENUM_MANAGER.get(tag.getString("category")),
						ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(tag.getString("hit_particle"))),
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(tag.getString("hit_sound"))),
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(tag.getString("swing_sound"))),
						null,
						ParseUtil.nullParam(colliderTag.get("number")),
						centerInit ? ParseUtil.nullOrToString(colliderTag.getList("center", Tag.TAG_DOUBLE).get(0), Tag::getAsString) : "",
						centerInit ? ParseUtil.nullOrToString(colliderTag.getList("center", Tag.TAG_DOUBLE).get(1), Tag::getAsString) : "",
						centerInit ? ParseUtil.nullOrToString(colliderTag.getList("center", Tag.TAG_DOUBLE).get(2), Tag::getAsString) : "",
						sizeInit ? ParseUtil.nullOrToString(colliderTag.getList("size", Tag.TAG_DOUBLE).get(0), Tag::getAsString) : "",
						sizeInit ? ParseUtil.nullOrToString(colliderTag.getList("size", Tag.TAG_DOUBLE).get(1), Tag::getAsString) : "",
						sizeInit ? ParseUtil.nullOrToString(colliderTag.getList("size", Tag.TAG_DOUBLE).get(2), Tag::getAsString) : "",
						packImporter
					});
				}
			};
			
			this.inputComponentsList.setLeftPos(164);
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.category")));
			this.inputComponentsList.addComponentCurrentRow(new ComboBox<>(parentScreen, parentScreen.getMinecraft().font, this.inputComponentsList.nextStart(5), 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8,
																			Component.translatable("datapack_edit.weapon_type.category"), new ArrayList<>(WeaponCategory.ENUM_MANAGER.universalValues()), ParseUtil::snakeToSpacedCamel,
																			(weaponCategory) -> this.packList.get(this.packListGrid.getRowposition()).getPackValue().putString("category", ParseUtil.nullParam(weaponCategory).toLowerCase(Locale.ROOT))));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.hit_particle")));
			this.inputComponentsList.addComponentCurrentRow(new PopupBox.RegistryPopupBox<>(parentScreen, font, this.inputComponentsList.nextStart(5), 15, 130, 15, HorizontalSizing.LEFT_RIGHT, null,
																			Component.translatable("datapack_edit.weapon_type.hit_particle"), ForgeRegistries.PARTICLE_TYPES,
																			(item) -> this.packList.get(this.packListGrid.getRowposition()).getPackValue().putString("hit_particle", ParseUtil.getRegistryName(item, ForgeRegistries.PARTICLE_TYPES))));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.hit_sound")));
			this.inputComponentsList.addComponentCurrentRow(new PopupBox.SoundPopupBox(parentScreen, font, this.inputComponentsList.nextStart(5), 15, 130, 15, HorizontalSizing.LEFT_RIGHT, null,
																			Component.translatable("datapack_edit.weapon_type.hit_sound"),
																			(item) -> this.packList.get(this.packListGrid.getRowposition()).getPackValue().putString("hit_sound", ParseUtil.getRegistryName(item, ForgeRegistries.SOUND_EVENTS))));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.swing_sound")));
			this.inputComponentsList.addComponentCurrentRow(new PopupBox.SoundPopupBox(parentScreen, font, this.inputComponentsList.nextStart(5), 15, 130, 15, HorizontalSizing.LEFT_RIGHT, null,
																			Component.translatable("datapack_edit.weapon_type.swing_sound"),
																			(item) -> this.packList.get(this.packListGrid.getRowposition()).getPackValue().putString("swing_sound", ParseUtil.getRegistryName(item, ForgeRegistries.SOUND_EVENTS))));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.styles")));
			this.inputComponentsList.addComponentCurrentRow(SubScreenOpenButton.builder().subScreen(() -> {
				return new StylesScreen(DatapackEditScreen.this, ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "styles", new CompoundTag()));
			}).bounds(this.inputComponentsList.nextStart(4), 0, 15, 15).build());
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.offhand_validator")));
			this.inputComponentsList.addComponentCurrentRow(SubScreenOpenButton.builder().subScreen(() -> {
				return new OffhandValidatorScreen(DatapackEditScreen.this, ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "offhand_item_compatible_predicate", new CompoundTag()));
			}).bounds(this.inputComponentsList.nextStart(4), 0, 15, 15).build());
			
			final ResizableEditBox colliderCount = new ResizableEditBox(font, 0, 40, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.count"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCenterX = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.center.x"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCenterY = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.center.y"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCenterZ = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.center.z"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderSizeX = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.size.x"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderSizeY = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.size.y"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderSizeZ = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.size.z"), HorizontalSizing.LEFT_WIDTH, null);
			
			colliderCount.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				
				if (StringUtil.isNullOrEmpty(input)) {
					collider.remove("number");
				} else {
					collider.put("number", IntTag.valueOf(Integer.valueOf(input)));
				}
			});
			
			colliderCenterX.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				ListTag centerVec = collider.getList("center", Tag.TAG_DOUBLE);
				
				double i = StringUtil.isNullOrEmpty(input) ? 0 : ParseUtil.parseOrGet(input, Double::valueOf, 0.0D);
				centerVec.remove(0);
				centerVec.add(0, DoubleTag.valueOf(i));
			});
			
			colliderCenterY.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				ListTag centerVec = collider.getList("center", Tag.TAG_DOUBLE);
				
				double i = StringUtil.isNullOrEmpty(input) ? 0 : ParseUtil.parseOrGet(input, Double::valueOf, 0.0D);
				centerVec.remove(1);
				centerVec.add(1, DoubleTag.valueOf(i));
			});
			
			colliderCenterZ.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				ListTag centerVec = collider.getList("center", Tag.TAG_DOUBLE);
				
				double i = StringUtil.isNullOrEmpty(input) ? 0 : ParseUtil.parseOrGet(input, Double::valueOf, 0.0D);
				centerVec.remove(2);
				centerVec.add(2, DoubleTag.valueOf(i));
			});
			
			colliderSizeX.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				ListTag centerVec = collider.getList("size", Tag.TAG_DOUBLE);
				
				double i = StringUtil.isNullOrEmpty(input) ? 0 : Double.valueOf(input);
				centerVec.remove(0);
				centerVec.add(0, DoubleTag.valueOf(i));
			});
			
			colliderSizeY.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				ListTag centerVec = collider.getList("size", Tag.TAG_DOUBLE);
				
				double i = StringUtil.isNullOrEmpty(input) ? 0 : Double.valueOf(input);
				centerVec.remove(1);
				centerVec.add(1, DoubleTag.valueOf(i));
			});
			
			colliderSizeZ.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				ListTag centerVec = collider.getList("size", Tag.TAG_DOUBLE);
				
				double i = StringUtil.isNullOrEmpty(input) ? 0 : Double.valueOf(input);
				centerVec.remove(2);
				centerVec.add(2, DoubleTag.valueOf(i));
			});
			
			colliderCount.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Integer::parseInt));
			colliderCenterX.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			colliderCenterY.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			colliderCenterZ.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			colliderSizeX.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
			colliderSizeY.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
			colliderSizeZ.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.collider")));
			this.inputComponentsList.addComponentCurrentRow(new PopupBox.ColliderPopupBox(parentScreen, font, this.inputComponentsList.nextStart(5), 15, 130, 15, HorizontalSizing.LEFT_RIGHT, null, Component.translatable("datapack_edit.weapon_type.collider"),
																							(collider) -> {
																								if (collider != null) {
																									CompoundTag colliderTag = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
																									collider.serialize(colliderTag);
																									
																									colliderCount.setValue(String.valueOf(colliderTag.getInt("number")));
																									
																									ListTag centerVec = colliderTag.getList("center", Tag.TAG_DOUBLE);
																									colliderCenterX.setValue(String.valueOf(centerVec.getDouble(0)));
																									colliderCenterY.setValue(String.valueOf(centerVec.getDouble(1)));
																									colliderCenterZ.setValue(String.valueOf(centerVec.getDouble(2)));
																									
																									ListTag sizeVec = colliderTag.getList("size", Tag.TAG_DOUBLE);
																									colliderSizeX.setValue(String.valueOf(sizeVec.getDouble(0)));
																									colliderSizeY.setValue(String.valueOf(sizeVec.getDouble(1)));
																									colliderSizeZ.setValue(String.valueOf(sizeVec.getDouble(2)));
																								}
																							}).applyFilter((collider) -> collider instanceof OBBCollider || collider instanceof MultiOBBCollider));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 40, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.collider.count")));
			this.inputComponentsList.addComponentCurrentRow(colliderCount.relocateX(rect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 40, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.collider.center")));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(5), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("X: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderCenterX.relocateX(rect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Y: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderCenterY.relocateX(rect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Z: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderCenterZ.relocateX(rect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 40, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.collider.size")));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(5), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("X: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderSizeX.relocateX(rect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Y: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderSizeY.relocateX(rect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Z: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderSizeZ.relocateX(rect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.combos")));
			this.inputComponentsList.addComponentCurrentRow(SubScreenOpenButton.builder().subScreen(() -> {
				return new ComboScreen(DatapackEditScreen.this, this.packList.get(this.packListGrid.getRowposition()).getPackValue());
			}).bounds(this.inputComponentsList.nextStart(4), 0, 15, 15).build());
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.innate_skill")));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(Grid.builder(DatapackEditScreen.this)
																.xy1(this.inputComponentsList.nextStart(5), 0)
																.xy2(15, 90)
																.horizontalSizing(HorizontalSizing.LEFT_RIGHT)
																.rowHeight(26)
																.rowEditable(true)
																.transparentBackground(false)
																.addColumn(Grid.combo("style", Style.ENUM_MANAGER.universalValues()).valueChanged((event) -> {
																				CompoundTag innateSkillsTag = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "innate_skills", new CompoundTag());
																				innateSkillsTag.remove(ParseUtil.nullParam(event.prevValue).toLowerCase(Locale.ROOT));
																				innateSkillsTag.putString(ParseUtil.nullParam(event.postValue).toLowerCase(Locale.ROOT), ParseUtil.nullParam(event.grid.getValue(event.rowposition, "skill")));
																			}).editable(true).width(100))
																.addColumn(Grid.registryPopup("skill", SkillManager.getSkillRegistry()).filter((skill) -> skill.getCategory() == SkillCategories.WEAPON_INNATE).valueChanged((event) -> {
																				CompoundTag innateSkillsTag = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "innate_skills", new CompoundTag());
																				innateSkillsTag.putString(ParseUtil.nullParam(event.grid.getValue(event.rowposition, "style")).toLowerCase(Locale.ROOT), ParseUtil.nullParam(event.postValue));
																			}).toDisplayText((item) -> item == null ? "" : item.getRegistryName().toString()).width(150))
																.pressAdd((grid, button) -> {
																	grid.setValueChangeEnabled(false);
																	int rowposition = grid.addRow();
																	grid.setGridFocus(rowposition, "style");
																	grid.setValueChangeEnabled(true);
																})
																.pressRemove((grid, button) -> {
																	int rowposition = grid.getRowposition();
																	
																	if (rowposition > -1) {
																		CompoundTag innateSkillsTag = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "innate_skills", new CompoundTag());
																		innateSkillsTag.remove(ParseUtil.nullParam(grid.getValue(rowposition, "style")).toLowerCase(Locale.ROOT));
																		grid.removeRow(rowposition);
																	}
																})
																.build());
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 80, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.living_animations")));
			
			this.inputComponentsList.addComponentCurrentRow(SubScreenOpenButton.builder().subScreen(() -> {
				return new LivingAnimationsScreen(DatapackEditScreen.this, ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "livingmotion_modifier", new CompoundTag()));
			}).bounds(this.inputComponentsList.nextStart(4), 0, 15, 15).build());
			
			this.inputComponentsList.setComponentsActive(false);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	class ItemCapabilityTab extends DatapackTab<Item> {
		private final AnimatedModelPlayer modelPlayer;
		
		public ItemCapabilityTab() {
			super(Component.translatable("gui." + EpicFightMod.MODID + ".tab.datapack.item_capability"), ItemCapabilityReloadListener.DIRECTORY, ForgeRegistries.ITEMS);
			
			Screen parentScreen = DatapackEditScreen.this;
			Font font = DatapackEditScreen.this.font;
			ScreenRectangle rect = DatapackEditScreen.this.getRectangle();
			
			this.modelPlayer = new AnimatedModelPlayer(20, 15, 0, 150, HorizontalSizing.LEFT_RIGHT, null, Armatures.BIPED, Meshes.BIPED);
			
			this.inputComponentsList = new InputComponentList<>(DatapackEditScreen.this, 0, 0, 0, 0, 30) {
				@Override
				public void importTag(CompoundTag tag) {
					this.setComponentsActive(true);
					
					CompoundTag trailTag = ParseUtil.getOrSupply(tag, "trail", CompoundTag::new);
					CompoundTag colliderTag = ParseUtil.getOrSupply(tag, "collider", CompoundTag::new);
					boolean centerInit = colliderTag.contains("center");
					boolean sizeInit = colliderTag.contains("size");
					boolean colorInit = trailTag.contains("color");
					boolean beginInit = trailTag.contains("begin_pos");
					boolean endInit = trailTag.contains("end_pos");
					
					if (!centerInit) {
						ListTag list = new ListTag();
						list.add(DoubleTag.valueOf(0.0D));
						list.add(DoubleTag.valueOf(0.0D));
						list.add(DoubleTag.valueOf(0.0D));
						colliderTag.put("center", list);
					}
					
					if (!sizeInit) {
						ListTag list = new ListTag();
						list.add(DoubleTag.valueOf(0.0D));
						list.add(DoubleTag.valueOf(0.0D));
						list.add(DoubleTag.valueOf(0.0D));
						colliderTag.put("size", list);
					}
					
					if (!colorInit) {
						ListTag list = new ListTag();
						list.add(IntTag.valueOf(0));
						list.add(IntTag.valueOf(0));
						list.add(IntTag.valueOf(0));
						trailTag.put("color", list);
					}
					
					if (!beginInit) {
						ListTag list = new ListTag();
						list.add(DoubleTag.valueOf(0.0D));
						list.add(DoubleTag.valueOf(0.0D));
						list.add(DoubleTag.valueOf(0.0D));
						trailTag.put("begin_pos", list);
					}
					
					if (!endInit) {
						ListTag list = new ListTag();
						list.add(DoubleTag.valueOf(0.0D));
						list.add(DoubleTag.valueOf(0.0D));
						list.add(DoubleTag.valueOf(0.0D));
						trailTag.put("end_pos", list);
					}
					
					this.setDataBindingComponenets(new Object[] {
						WeaponTypeReloadListener.get(tag.getString("type")),
						null,
						ParseUtil.nullParam(colliderTag.get("number")),
						centerInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(colliderTag.getList("center", Tag.TAG_DOUBLE).get(0), Tag::getAsString)) : "",
						centerInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(colliderTag.getList("center", Tag.TAG_DOUBLE).get(1), Tag::getAsString)) : "",
						centerInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(colliderTag.getList("center", Tag.TAG_DOUBLE).get(2), Tag::getAsString)) : "",
						sizeInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(colliderTag.getList("size", Tag.TAG_DOUBLE).get(0), Tag::getAsString)) : "",
						sizeInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(colliderTag.getList("size", Tag.TAG_DOUBLE).get(1), Tag::getAsString)) : "",
						sizeInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(colliderTag.getList("size", Tag.TAG_DOUBLE).get(2), Tag::getAsString)) : "",
						colorInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(trailTag.getList("color", Tag.TAG_INT).get(0), Tag::getAsString)) : "",
						colorInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(trailTag.getList("color", Tag.TAG_INT).get(1), Tag::getAsString)) : "",
						colorInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(trailTag.getList("color", Tag.TAG_INT).get(2), Tag::getAsString)) : "",
						beginInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(trailTag.getList("begin_pos", Tag.TAG_DOUBLE).get(0), Tag::getAsString)) : "",
						beginInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(trailTag.getList("begin_pos", Tag.TAG_DOUBLE).get(1), Tag::getAsString)) : "",
						beginInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(trailTag.getList("begin_pos", Tag.TAG_DOUBLE).get(2), Tag::getAsString)) : "",
						endInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(trailTag.getList("end_pos", Tag.TAG_DOUBLE).get(0), Tag::getAsString)) : "",
						endInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(trailTag.getList("end_pos", Tag.TAG_DOUBLE).get(1), Tag::getAsString)) : "",
						endInit ? ParseUtil.nullParam(ParseUtil.nullOrToString(trailTag.getList("end_pos", Tag.TAG_DOUBLE).get(2), Tag::getAsString)) : "",
						ParseUtil.nullParam(trailTag.get("lifetime")),
						ParseUtil.nullParam(trailTag.get("interpolation")),
						ParseUtil.nullParam(trailTag.getString("texture_path")),
						ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(trailTag.getString("particle_type")))
					});
				}
			};
			
			this.inputComponentsList.setLeftPos(164);
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.item_capability.attributes")));
			this.inputComponentsList.addComponentCurrentRow(SubScreenOpenButton.builder().subScreen(() -> {
				return new AttributeScreen(DatapackEditScreen.this, ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "attributes", new CompoundTag()));
			}).bounds(this.inputComponentsList.nextStart(4), 0, 15, 15).build());
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.item_capability.type")));
			this.inputComponentsList.addComponentCurrentRow(new PopupBox.WeaponTypePopupBox(parentScreen, font, this.inputComponentsList.nextStart(5), 15, 15, 15, HorizontalSizing.LEFT_RIGHT, null,
																			Component.translatable("datapack_edit.item_capability.type"),
																			(item) -> {
																				this.packList.get(this.packListGrid.getRowposition()).getPackValue().putString("type", ParseUtil.nullParam(WeaponTypeReloadListener.getKey(item)));
																				
																				if (item != null) {
																					CapabilityItem.Builder builder = item.apply(this.registry.getValue(this.packList.get(this.packListGrid.getRowposition()).getPackKey()));
																					
																					if (builder instanceof WeaponCapability.Builder weaponBuilder) {
																						this.modelPlayer.clearAnimations();
																						
																						List<AnimationProvider<?>> allAnimations = weaponBuilder.getComboAnimations().entrySet().stream().reduce(Lists.newArrayList(), (list, entry) -> {
																							list.addAll(entry.getValue());
																							return list;
																						}, (list1, list2) -> {
																							list1.addAll(list2);
																							return list1;
																						});
																						
																						allAnimations.stream().map((provider) -> provider.get()).forEach(this.modelPlayer::addAnimationToPlay);
																						
																						this.modelPlayer.setCollider(weaponBuilder.getCollider());
																					}
																				}
																			}));
			
			this.inputComponentsList.newRow();
			
			final ResizableEditBox colliderCount = new ResizableEditBox(font, 0, 40, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.count"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCenterX = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.center.x"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCenterY = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.center.y"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderCenterZ = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.center.z"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderSizeX = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.size.x"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderSizeY = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.size.y"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colliderSizeZ = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.weapon_type.collider.size.z"), HorizontalSizing.LEFT_WIDTH, null);
			
			colliderCount.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				
				if (StringUtil.isNullOrEmpty(input)) {
					collider.remove("number");
				} else {
					collider.put("number", IntTag.valueOf(Integer.valueOf(input)));
				}
				
				try {
					this.modelPlayer.setCollider(ColliderPreset.deserializeSimpleCollider(collider));
				} catch (IllegalArgumentException e) {
					this.modelPlayer.setCollider(null);
				}
			});
			
			colliderCenterX.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				ListTag centerVec = collider.getList("center", Tag.TAG_DOUBLE);
				
				double i = StringUtil.isNullOrEmpty(input) ? 0 : ParseUtil.parseOrGet(input, Double::valueOf, 0.0D);
				centerVec.remove(0);
				centerVec.add(0, DoubleTag.valueOf(i));
				
				try {
					this.modelPlayer.setCollider(ColliderPreset.deserializeSimpleCollider(collider));
				} catch (IllegalArgumentException e) {
					this.modelPlayer.setCollider(null);
				}
			});
			
			colliderCenterY.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				ListTag centerVec = collider.getList("center", Tag.TAG_DOUBLE);
				
				double i = StringUtil.isNullOrEmpty(input) ? 0 : ParseUtil.parseOrGet(input, Double::valueOf, 0.0D);
				centerVec.remove(1);
				centerVec.add(1, DoubleTag.valueOf(i));
				
				try {
					this.modelPlayer.setCollider(ColliderPreset.deserializeSimpleCollider(collider));
				} catch (IllegalArgumentException e) {
					this.modelPlayer.setCollider(null);
				}
			});
			
			colliderCenterZ.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				ListTag centerVec = collider.getList("center", Tag.TAG_DOUBLE);
				
				double i = StringUtil.isNullOrEmpty(input) ? 0 : ParseUtil.parseOrGet(input, Double::valueOf, 0.0D);
				centerVec.remove(2);
				centerVec.add(2, DoubleTag.valueOf(i));
				
				try {
					this.modelPlayer.setCollider(ColliderPreset.deserializeSimpleCollider(collider));
				} catch (IllegalArgumentException e) {
				this.modelPlayer.setCollider(null);
				}
			});
			
			colliderSizeX.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				ListTag centerVec = collider.getList("size", Tag.TAG_DOUBLE);
				
				double i = StringUtil.isNullOrEmpty(input) ? 0 : Double.valueOf(input);
				centerVec.remove(0);
				centerVec.add(0, DoubleTag.valueOf(i));
				
				try {
					this.modelPlayer.setCollider(ColliderPreset.deserializeSimpleCollider(collider));
				} catch (IllegalArgumentException e) {
					this.modelPlayer.setCollider(null);
				}
			});
			
			colliderSizeY.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				ListTag centerVec = collider.getList("size", Tag.TAG_DOUBLE);
				
				double i = StringUtil.isNullOrEmpty(input) ? 0 : Double.valueOf(input);
				centerVec.remove(1);
				centerVec.add(1, DoubleTag.valueOf(i));
				
				try {
					this.modelPlayer.setCollider(ColliderPreset.deserializeSimpleCollider(collider));
				} catch (IllegalArgumentException e) {
					this.modelPlayer.setCollider(null);
				}
			});
			
			colliderSizeZ.setResponder((input) -> {
				CompoundTag collider = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
				ListTag centerVec = collider.getList("size", Tag.TAG_DOUBLE);
				
				double i = StringUtil.isNullOrEmpty(input) ? 0 : Double.valueOf(input);
				centerVec.remove(2);
				centerVec.add(2, DoubleTag.valueOf(i));
				
				try {
					this.modelPlayer.setCollider(ColliderPreset.deserializeSimpleCollider(collider));
				} catch (IllegalArgumentException e) {
					this.modelPlayer.setCollider(null);
				}
			});
			
			colliderCount.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Integer::parseInt));
			colliderCenterX.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			colliderCenterY.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			colliderCenterZ.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			colliderSizeX.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
			colliderSizeY.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
			colliderSizeZ.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Double::parseDouble));
			
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 100, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.collider")));
			this.inputComponentsList.addComponentCurrentRow(new PopupBox.ColliderPopupBox(parentScreen, font, this.inputComponentsList.nextStart(5), 15, 130, 15, HorizontalSizing.LEFT_RIGHT, null, Component.translatable("datapack_edit.weapon_type.collider"),
																							(collider) -> {
																								if (collider != null) {
																									CompoundTag colliderTag = ParseUtil.getOrDefaultTag(this.packList.get(this.packListGrid.getRowposition()).getPackValue(), "collider", new CompoundTag());
																									collider.serialize(colliderTag);
																									
																									colliderCount.setValue(String.valueOf(colliderTag.getInt("number")));
																									
																									ListTag centerVec = colliderTag.getList("center", Tag.TAG_DOUBLE);
																									colliderCenterX.setValue(String.valueOf(centerVec.getDouble(0)));
																									colliderCenterY.setValue(String.valueOf(centerVec.getDouble(1)));
																									colliderCenterZ.setValue(String.valueOf(centerVec.getDouble(2)));
																									
																									ListTag sizeVec = colliderTag.getList("size", Tag.TAG_DOUBLE);
																									colliderSizeX.setValue(String.valueOf(sizeVec.getDouble(0)));
																									colliderSizeY.setValue(String.valueOf(sizeVec.getDouble(1)));
																									colliderSizeZ.setValue(String.valueOf(sizeVec.getDouble(2)));
																									
																									this.modelPlayer.setCollider(collider);
																								}
																							}).applyFilter((collider) -> collider instanceof OBBCollider || collider instanceof MultiOBBCollider));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 40, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.collider.count")));
			this.inputComponentsList.addComponentCurrentRow(colliderCount.relocateX(rect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 40, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.collider.center")));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(5), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("X: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderCenterX.relocateX(rect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Y: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderCenterY.relocateX(rect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Z: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderCenterZ.relocateX(rect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 40, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.weapon_type.collider.size")));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(5), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("X: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderSizeX.relocateX(rect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Y: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderSizeY.relocateX(rect, this.inputComponentsList.nextStart(5)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Z: ")));
			this.inputComponentsList.addComponentCurrentRow(colliderSizeZ.relocateX(rect, this.inputComponentsList.nextStart(5)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 90, 60, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.item_capability.trail")));
			
			final ResizableEditBox colorR = new ResizableEditBox(font, 0, 30, 0, 15, Component.translatable("datapack_edit.item_capability.trail.color.r"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colorG = new ResizableEditBox(font, 0, 30, 0, 15, Component.translatable("datapack_edit.item_capability.trail.color.g"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox colorB = new ResizableEditBox(font, 0, 30, 0, 15, Component.translatable("datapack_edit.item_capability.trail.color.b"), HorizontalSizing.LEFT_WIDTH, null);
			final ColorPreviewWidget colorWidget = new ColorPreviewWidget(0, 12, 0, 12, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.item_capability.color"));
			
			colorR.setResponder((input) -> {
				CompoundTag trailTag = this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail");
				ListTag list = trailTag.getList("color", Tag.TAG_INT);
				int i = StringUtil.isNullOrEmpty(input) ? 0 : Integer.valueOf(input);
				list.remove(0);
				list.add(0, IntTag.valueOf(i));
				colorWidget.setColor(ParseUtil.parseOrGet(input, Integer::valueOf, 0), ParseUtil.parseOrGet(colorG.getValue(), Integer::valueOf, 0), ParseUtil.parseOrGet(colorB.getValue(), Integer::valueOf, 0));
				
				TrailInfo trailInfo = TrailInfo.deserialize(trailTag);
				this.modelPlayer.setTrailInfo(trailInfo);
			});
			colorG.setResponder((input) -> {
				CompoundTag trailTag = this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail");
				ListTag list = trailTag.getList("color", Tag.TAG_INT);
				int i = StringUtil.isNullOrEmpty(input) ? 0 : Integer.valueOf(input);
				list.remove(1);
				list.add(1, IntTag.valueOf(i));
				colorWidget.setColor(ParseUtil.parseOrGet(colorR.getValue(), Integer::valueOf, 0), ParseUtil.parseOrGet(input, Integer::valueOf, 0), ParseUtil.parseOrGet(colorB.getValue(), Integer::valueOf, 0));
				
				TrailInfo trailInfo = TrailInfo.deserialize(trailTag);
				this.modelPlayer.setTrailInfo(trailInfo);
			});
			colorB.setResponder((input) -> {
				CompoundTag trailTag = this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail");
				ListTag list = trailTag.getList("color", Tag.TAG_INT);
				int i = StringUtil.isNullOrEmpty(input) ? 0 : Integer.valueOf(input);
				list.remove(2);
				list.add(2, IntTag.valueOf(i));
				colorWidget.setColor(ParseUtil.parseOrGet(colorR.getValue(), Integer::valueOf, 0), ParseUtil.parseOrGet(colorG.getValue(), Integer::valueOf, 0), ParseUtil.parseOrGet(input, Integer::valueOf, 0));
				
				TrailInfo trailInfo = TrailInfo.deserialize(trailTag);
				this.modelPlayer.setTrailInfo(trailInfo);
			});
			
			colorR.setFilter((context) -> StringUtil.isNullOrEmpty(context) || (ParseUtil.isParsable(context, Integer::parseInt) && ParseUtil.parseOrGet(context, Integer::parseInt, 0) < 256));
			colorG.setFilter((context) -> StringUtil.isNullOrEmpty(context) || (ParseUtil.isParsable(context, Integer::parseInt) && ParseUtil.parseOrGet(context, Integer::parseInt, 0) < 256));
			colorB.setFilter((context) -> StringUtil.isNullOrEmpty(context) || (ParseUtil.isParsable(context, Integer::parseInt) && ParseUtil.parseOrGet(context, Integer::parseInt, 0) < 256));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 28, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.item_capability.color")));
			this.inputComponentsList.addComponentCurrentRow(colorWidget.relocateX(rect, this.inputComponentsList.nextStart(4)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(41), 8, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("R: ")));
			this.inputComponentsList.addComponentCurrentRow(colorR.relocateX(rect, this.inputComponentsList.nextStart(4)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("G: ")));
			this.inputComponentsList.addComponentCurrentRow(colorG.relocateX(rect, this.inputComponentsList.nextStart(4)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("B: ")));
			this.inputComponentsList.addComponentCurrentRow(colorB.relocateX(rect, this.inputComponentsList.nextStart(4)));
			
			final ResizableEditBox beginX = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.item_capability.trail.begin_pos.x"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox beginY = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.item_capability.trail.begin_pos.y"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox beginZ = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.item_capability.trail.begin_pos.z"), HorizontalSizing.LEFT_WIDTH, null);
			
			beginX.setResponder((input) -> {
				CompoundTag trailTag = this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail");
				ListTag list = trailTag.getList("begin_pos", Tag.TAG_DOUBLE);
				double d = StringUtil.isNullOrEmpty(input) ? 0 : ParseUtil.parseOrGet(input, Double::valueOf, 0.0D);
				list.remove(0);
				list.add(0, DoubleTag.valueOf(d));
				
				TrailInfo trailInfo = TrailInfo.deserialize(trailTag);
				this.modelPlayer.setTrailInfo(trailInfo);
			});
			beginY.setResponder((input) -> {
				CompoundTag trailTag = this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail");
				ListTag list = trailTag.getList("begin_pos", Tag.TAG_DOUBLE);
				double d = StringUtil.isNullOrEmpty(input) ? 0 : ParseUtil.parseOrGet(input, Double::valueOf, 0.0D);
				list.remove(1);
				list.add(1, DoubleTag.valueOf(d));
				
				TrailInfo trailInfo = TrailInfo.deserialize(trailTag);
				this.modelPlayer.setTrailInfo(trailInfo);
			});
			beginZ.setResponder((input) -> {
				CompoundTag trailTag = this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail");
				ListTag list = trailTag.getList("begin_pos", Tag.TAG_DOUBLE);
				double d = StringUtil.isNullOrEmpty(input) ? 0 : ParseUtil.parseOrGet(input, Double::valueOf, 0.0D);
				list.remove(2);
				list.add(2, DoubleTag.valueOf(d));
				
				TrailInfo trailInfo = TrailInfo.deserialize(trailTag);
				this.modelPlayer.setTrailInfo(trailInfo);
			});
			
			beginX.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			beginY.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			beginZ.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 80, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.item_capability.begin_pos")));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(5), 8, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("X: ")));
			this.inputComponentsList.addComponentCurrentRow(beginX.relocateX(rect, this.inputComponentsList.nextStart(4)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Y: ")));
			this.inputComponentsList.addComponentCurrentRow(beginY.relocateX(rect, this.inputComponentsList.nextStart(4)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Z: ")));
			this.inputComponentsList.addComponentCurrentRow(beginZ.relocateX(rect, this.inputComponentsList.nextStart(4)));
			
			final ResizableEditBox endX = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.item_capability.trail.end_pos.x"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox endY = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.item_capability.trail.end_pos.y"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox endZ = new ResizableEditBox(font, 0, 35, 0, 15, Component.translatable("datapack_edit.item_capability.trail.end_pos.z"), HorizontalSizing.LEFT_WIDTH, null);
			
			endX.setResponder((input) -> {
				CompoundTag trailTag = this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail");
				ListTag list =trailTag.getList("end_pos", Tag.TAG_DOUBLE);
				double d = StringUtil.isNullOrEmpty(input) ? 0 : ParseUtil.parseOrGet(input, Double::valueOf, 0.0D);
				list.remove(0);
				list.add(0, DoubleTag.valueOf(d));
				
				TrailInfo trailInfo = TrailInfo.deserialize(trailTag);
				this.modelPlayer.setTrailInfo(trailInfo);
			});
			endY.setResponder((input) -> {
				CompoundTag trailTag = this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail");
				ListTag list =trailTag.getList("end_pos", Tag.TAG_DOUBLE);
				double d = StringUtil.isNullOrEmpty(input) ? 0 : ParseUtil.parseOrGet(input, Double::valueOf, 0.0D);
				list.remove(1);
				list.add(1, DoubleTag.valueOf(d));
				
				TrailInfo trailInfo = TrailInfo.deserialize(trailTag);
				this.modelPlayer.setTrailInfo(trailInfo);
			});
			endZ.setResponder((input) -> {
				CompoundTag trailTag = this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail");
				ListTag list =trailTag.getList("end_pos", Tag.TAG_DOUBLE);
				double d = StringUtil.isNullOrEmpty(input) ? 0 : ParseUtil.parseOrGet(input, Double::valueOf, 0.0D);
				list.remove(2);
				list.add(2, DoubleTag.valueOf(d));
				
				TrailInfo trailInfo = TrailInfo.deserialize(trailTag);
				this.modelPlayer.setTrailInfo(trailInfo);
			});
			
			endX.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			endY.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			endZ.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsableAllowingMinus(context, Double::parseDouble));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 80, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.item_capability.end_pos")));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(5), 8, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("X: ")));
			this.inputComponentsList.addComponentCurrentRow(endX.relocateX(rect, this.inputComponentsList.nextStart(4)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Y: ")));
			this.inputComponentsList.addComponentCurrentRow(endY.relocateX(rect, this.inputComponentsList.nextStart(4)));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 8, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Z: ")));
			this.inputComponentsList.addComponentCurrentRow(endZ.relocateX(rect, this.inputComponentsList.nextStart(4)));
			
			final ResizableEditBox lifetime = new ResizableEditBox(font, 0, 30, 0, 15, Component.translatable("datapack_edit.item_capability.trail.lifetime"), HorizontalSizing.LEFT_WIDTH, null);
			final ResizableEditBox interpolation = new ResizableEditBox(font, 0, 30, 0, 15, Component.translatable("datapack_edit.item_capability.trail.interpolation"), HorizontalSizing.LEFT_WIDTH, null);
			
			lifetime.setResponder((input) -> {
				int i = StringUtil.isNullOrEmpty(input) ? 0 : Integer.valueOf(input);
				CompoundTag trailTag = this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail");
				trailTag.put("lifetime", IntTag.valueOf(i));
				
				TrailInfo trailInfo = TrailInfo.deserialize(trailTag);
				this.modelPlayer.setTrailInfo(trailInfo);
			});
			lifetime.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Integer::parseInt));
			
			interpolation.setResponder((input) -> {
				int i = StringUtil.isNullOrEmpty(input) ? 0 : Integer.valueOf(input);
				
				CompoundTag trailTag = this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail");
				trailTag.put("interpolations", IntTag.valueOf(i));
				
				TrailInfo trailInfo = TrailInfo.deserialize(trailTag);
				this.modelPlayer.setTrailInfo(trailInfo);
			});
			interpolation.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ParseUtil.isParsable(context, Integer::parseInt));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 80, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.item_capability.lifetime")));
			this.inputComponentsList.addComponentCurrentRow(lifetime.relocateX(rect, this.inputComponentsList.nextStart(8)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 80, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.item_capability.interpolations")));
			this.inputComponentsList.addComponentCurrentRow(interpolation.relocateX(rect, this.inputComponentsList.nextStart(8)));
			
			final ResizableEditBox texturePath = new ResizableEditBox(font, 0, 15, 0, 15, Component.translatable("datapack_edit.item_capability.trail.end_pos.z"), HorizontalSizing.LEFT_RIGHT, null);
			texturePath.setResponder((input) -> this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail").put("texture_path", StringTag.valueOf(new ResourceLocation(input).toString())));
			texturePath.setFilter((context) -> StringUtil.isNullOrEmpty(context) || ResourceLocation.isValidResourceLocation(context));
			texturePath.setMaxLength(100);
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 80, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.item_capability.texture_path")));
			this.inputComponentsList.addComponentCurrentRow(texturePath.relocateX(rect, this.inputComponentsList.nextStart(8)));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 80, 0, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.item_capability.particle_type")));
			this.inputComponentsList.addComponentCurrentRow(new PopupBox.RegistryPopupBox<>(parentScreen, font, this.inputComponentsList.nextStart(8), 15, 130, 15, HorizontalSizing.LEFT_RIGHT, null,
																		Component.translatable("datapack_edit.weapon_type.hit_particle"), ForgeRegistries.PARTICLE_TYPES,
																		(item) -> {
																			CompoundTag trailTag = this.packList.get(this.packListGrid.getRowposition()).getPackValue().getCompound("trail");
																			trailTag.putString("particle_type", ParseUtil.getRegistryName(item, ForgeRegistries.PARTICLE_TYPES));
																			
																			TrailInfo trailInfo = TrailInfo.deserialize(trailTag);
																			this.modelPlayer.setTrailInfo(trailInfo);
																		}));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.newRow();
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(this.modelPlayer.relocateX(rect, this.inputComponentsList.nextStart(8)));
			this.inputComponentsList.newRow();
			this.inputComponentsList.newRow();
			
			this.inputComponentsList.setComponentsActive(false);
		}
		
		@Override
		public void packGridRowpositionChanged(int rowposition, Map<String, Object> values) {
			this.inputComponentsList.importTag(this.packList.get(rowposition).getPackValue());
			
			ResourceLocation rl = new ResourceLocation(ParseUtil.nullParam(values.get("pack_item")));
			
			if (this.registry.containsKey(rl)) {
				this.modelPlayer.setItemToRender(this.registry.getValue(rl));
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	class MobPatchTab extends DatapackTab<EntityType<?>> {
		public MobPatchTab() {
			super(Component.translatable("gui." + EpicFightMod.MODID + ".tab.datapack.mob_patch"), MobPatchReloadListener.DIRECTORY, ForgeRegistries.ENTITY_TYPES);
			
			this.inputComponentsList = new InputComponentList<>(DatapackEditScreen.this, 0, 0, 0, 0, 30) {
				@Override
				public void importTag(CompoundTag tag) {
					
				}
			};
		}
	}
}