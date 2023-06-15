package com.greenjon902.texthotkeys.client;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import me.shedaniel.clothconfig2.gui.entries.TextFieldListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class HotKeyEntry extends AbstractConfigListEntry<HotKeyEntry> {
	final Config.HotkeyInfo hotkeyInfo;
	private final TextFieldWidget messageInputWidget;
	private final ButtonWidget keyButtonWidget;
	private final ButtonWidget removeButton;
	private List<? extends Element> widgets;

	public HotKeyEntry(Config.HotkeyInfo hotkeyInfo) {
		super(Text.empty(), false);
		this.hotkeyInfo = hotkeyInfo;
		this.messageInputWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 148, 18, Text.empty()) {
			public void render(DrawContext graphics, int int_1, int int_2, float float_1) {
				HotKeyEntry.this.textFieldPreRender(this);
				super.render(graphics, int_1, int_2, float_1);
			}

			public void write(String string_1) {
				super.write(HotKeyEntry.this.stripAddText(string_1));
			}
		};

		this.keyButtonWidget = ButtonWidget.builder(Text.empty(), (widget) -> {}
		).dimensions(0, 0, 150, 20).build();

		this.removeButton = ButtonWidget.builder(
				Text.translatable("option.textHotkeys.hotkey.remove"),
				(widget) -> {})
				.dimensions(0, 0,
						MinecraftClient.getInstance().textRenderer.getWidth("option.textHotkeys.hotkey.remove") + 6, 20)
				.build();

		this.widgets = Lists.newArrayList(new ClickableWidget[]{this.keyButtonWidget, this.removeButton});
	}

	@Override
	public HotKeyEntry getValue() {
		return null;
	}

	@Override
	public Optional<HotKeyEntry> getDefaultValue() {
		return Optional.empty();
	}

	@Override
	public List<? extends Selectable> narratables() {
		return null;
	}


	public List<? extends Element> children() {
		return this.widgets;
	}

	protected void textFieldPreRender(TextFieldWidget widget) {
		widget.setEditableColor(this.getConfigError().isPresent() ? 16733525 : 14737632);
	}

	public void render(DrawContext graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
		super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);


		Window window = MinecraftClient.getInstance().getWindow();
		this.resetButton.active = this.isEditable() && this.getDefaultValue().isPresent() && !((ModifierKeyCode)this.getDefaultValue().get()).equals(this.getValue());
		this.resetButton.setY(y);
		this.buttonWidget.active = this.isEditable();
		this.buttonWidget.setY(y);
		this.buttonWidget.setMessage(this.getLocalizedName());
		if (this.getConfigScreen().getFocusedBinding() == this) {
			this.buttonWidget.setMessage(Text.literal("> ").formatted(Formatting.WHITE).append(this.buttonWidget.getMessage().copyContentOnly().formatted(Formatting.YELLOW)).append(Text.literal(" <").formatted(Formatting.WHITE)));
		}

		Text displayedFieldName = this.getDisplayedFieldName();
		if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
			graphics.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, displayedFieldName.asOrderedText(), window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName), y + 6, 16777215);
			this.resetButton.setX(x);
			this.buttonWidget.setX(x + this.resetButton.getWidth() + 2);
		} else {
			graphics.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, displayedFieldName.asOrderedText(), x, y + 6, this.getPreferredTextColor());
			this.resetButton.setX(x + entryWidth - this.resetButton.getWidth());
			this.buttonWidget.setX(x + entryWidth - 150);
		}

		this.buttonWidget.setWidth(150 - this.resetButton.getWidth() - 2);
		this.resetButton.render(graphics, mouseX, mouseY, delta);
		this.buttonWidget.render(graphics, mouseX, mouseY, delta);


		Window window = MinecraftClient.getInstance().getWindow();
		this.resetButton.active = this.isEditable() && this.getDefaultValue().isPresent() && !this.isMatchDefault(this.textFieldWidget.getText());
		this.resetButton.setY(y);
		this.textFieldWidget.setEditable(this.isEditable());
		this.textFieldWidget.setY(y + 1);
		Text displayedFieldName = this.getDisplayedFieldName();
		if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
			graphics.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, displayedFieldName.asOrderedText(), window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName), y + 6, this.getPreferredTextColor());
			this.resetButton.setX(x);
			this.textFieldWidget.setX(x + this.resetButton.getWidth());
		} else {
			graphics.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, displayedFieldName.asOrderedText(), x, y + 6, this.getPreferredTextColor());
			this.resetButton.setX(x + entryWidth - this.resetButton.getWidth());
			this.textFieldWidget.setX(x + entryWidth - 148);
		}

		setTextFieldWidth(this.textFieldWidget, 148 - this.resetButton.getWidth() - 4);
		this.resetButton.render(graphics, mouseX, mouseY, delta);
		this.textFieldWidget.render(graphics, mouseX, mouseY, delta);
	}
}
