package org.oddlama.vane.enchantments.enchantments;

import static org.oddlama.vane.util.ItemUtil.damage_item;
import static org.oddlama.vane.util.PlayerUtil.apply_elytra_boost;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.util.Vector;

import org.bukkit.Particle;

import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.oddlama.vane.core.item.CustomItem;
import org.oddlama.vane.enchantments.items.AncientTomeOfKnowledge;
import org.oddlama.vane.enchantments.items.AncientTomeOfTheGods;
import org.oddlama.vane.enchantments.items.BookVariant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

import org.oddlama.vane.annotation.config.ConfigDoubleList;
import org.oddlama.vane.annotation.enchantment.Rarity;
import org.oddlama.vane.annotation.enchantment.VaneEnchantment;
import org.oddlama.vane.core.module.Context;
import org.oddlama.vane.enchantments.CustomEnchantment;
import org.oddlama.vane.enchantments.Enchantments;

@VaneEnchantment(name = "take_off", max_level = 3, rarity = Rarity.UNCOMMON, treasure = true)
public class TakeOff extends CustomEnchantment<Enchantments> {
	@ConfigDoubleList(def = {0.2, 0.4, 0.6}, min = 0.0, desc = "Boost strength for each enchantment level.")
	private List<Double> config_boost_strengths;

	public TakeOff(Context<Enchantments> context) {
		super(context);
	}

	@Override
	public void register_recipes() {
		final var ancient_tome_of_the_gods = CustomItem.<AncientTomeOfTheGods.AncientTomeOfTheGodsVariant>variant_of(AncientTomeOfTheGods.class, BookVariant.ENCHANTED_BOOK).item();

		final var item = ancient_tome_of_the_gods.clone();
		final var meta = (EnchantmentStorageMeta)item.getItemMeta();
		meta.addStoredEnchant(bukkit(), 1, false);
		item.setItemMeta(meta);

		final var recipe = new ShapedRecipe(recipe_key(), item)
			.shape("mbm",
				   "psp")
			.setIngredient('b', ancient_tome_of_the_gods)
			.setIngredient('m', Material.PHANTOM_MEMBRANE)
			.setIngredient('p', Material.STICKY_PISTON)
			.setIngredient('s', Material.SLIME_BLOCK);

		add_recipe(recipe);
	}

	@Override
	public boolean can_enchant(@NotNull ItemStack item_stack) {
		return item_stack.getType() == Material.ELYTRA;
	}

	private double get_boost_strength(int level) {
		if (level > 0 && level <= config_boost_strengths.size()) {
			return config_boost_strengths.get(level - 1);
		}
		return config_boost_strengths.get(0);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void on_player_toggle_glide(EntityToggleGlideEvent event) {
		if (!(event.getEntity() instanceof Player) || !event.isGliding()) {
			return;
		}

		// Don't apply for sneaking players
		final var player = (Player)event.getEntity();
		if (player.isSneaking()) {
			return;
		}

		// Check enchantment level
		final var chest = player.getEquipment().getChestplate();
		final var level = chest.getEnchantmentLevel(this.bukkit());
		if (level == 0) {
			return;
		}

		// Apply boost
		apply_elytra_boost(player, get_boost_strength(level));
		damage_item(player, chest, (int)(1.0 + 2.0 * Math.random()));

		// Spawn particles
		final var loc = player.getLocation();
		final var vel = player.getVelocity().length();
		for (int i = 0; i < 16; ++i) {
			final var rnd = Vector.getRandom()
				.subtract(new Vector(.5, .5, .5))
				.normalize()
				.multiply(.25);
			final var dir = rnd.clone()
				.multiply(.5)
				.subtract(player.getVelocity());
			loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc.add(rnd), 0, dir.getX(), dir.getY(), dir.getZ(), vel * ThreadLocalRandom.current().nextDouble(0.4, 0.6));
		}
	}
}
