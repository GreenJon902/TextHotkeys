package com.greenjon902.texthotkeys.mixin;

import com.greenjon902.texthotkeys.Unregisterable;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements Unregisterable {
	@Accessor("KEYS_BY_ID")
	protected static Map<String, KeyBinding> getKeysById() {
		throw new AssertionError();
	}

	@Accessor("KEY_TO_BINDINGS")
	protected static Map<InputUtil.Key, KeyBinding> getKeyToBinding() {
		throw new AssertionError();
	}

	@Accessor("boundKey")
	protected abstract InputUtil.Key getBoundKey();

	@Override
	public void unregisterSelf() {
		getKeysById().remove(((KeyBinding)(Object)this).getTranslationKey());
		getKeyToBinding().remove(getBoundKey());
	}
}
