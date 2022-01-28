package dev.slohth.minigames.utils;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A utility class designed to serialize and deserialize ItemStacks to/from readable and easily modifiable Strings
 * Designed for config files for full control of items (as bukkit's built-in serializer is a bit garbo)
 * Uses json format to store the data, so it's useful if you're familiar with it
 *
 * Supports ALL ItemStack modification through the spigot api (not nms, so no nbt tags yet sorry)
 *
 * @author slohth
 */
public final class ItemStackSerializer {

    /**
     * Deserializes an ItemStack from a valid serialized String - makes use of Google's gson library
     * @param input The String to deserialize from
     * @return The deserialized ItemStack
     * @throws JsonSyntaxException If the serialization was formatted incorrectly
     */
    @SuppressWarnings("deprecation")
    public static @NotNull ItemStack deserialize(final @NotNull String input) throws JsonSyntaxException {
        final JsonObject json = new JsonParser().parse(input).getAsJsonObject();

        final ItemStack item = new ItemStack(Material.STONE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            switch (entry.getKey()) {
                case "type": {
                    final Material material = Material.getMaterial(entry.getValue().getAsString());
                    if (material != null) {
                        item.setItemMeta(meta);
                        item.setType(material);
                        meta = item.getItemMeta();
                    }
                    break;
                }
                case "name": {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', entry.getValue().getAsString()));
                    break;
                }
                case "amount": {
                    item.setItemMeta(meta);
                    item.setAmount(entry.getValue().getAsInt());
                    meta = item.getItemMeta();
                    break;
                }
                case "lore": {
                    final List<String> lore = new ArrayList<>();
                    for (JsonElement element : entry.getValue().getAsJsonArray()) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', element.getAsString()));
                    }
                    meta.setLore(lore);
                    break;
                }
                case "owner": {
                    if (item.getType() == Material.PLAYER_HEAD) {
                        item.setItemMeta(meta);
                        meta = item.getItemMeta();
                        ((SkullMeta) meta).setOwningPlayer(Bukkit.getOfflinePlayer(entry.getValue().getAsString()));
                    }
                    break;
                }
                case "durability": {
                    item.setItemMeta(meta);
                    item.setDurability((short) entry.getValue().getAsInt());
                    meta = item.getItemMeta();
                    break;
                }
                case "unbreakable": {
                    meta.setUnbreakable(entry.getValue().getAsBoolean());
                    break;
                }
                case "local-name": {
                    meta.setLocalizedName(entry.getValue().getAsString());
                    break;
                }
                case "flags": {
                    for (JsonElement element : entry.getValue().getAsJsonArray()) {
                        try {
                            meta.addItemFlags(ItemFlag.valueOf(element.getAsString()));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    break;
                }
                case "enchants": {
                    for (JsonElement element : entry.getValue().getAsJsonArray()) {
                        final JsonObject enchant = element.getAsJsonObject();
                        final Enchantment type = Enchantment.getByName(enchant.get("type").getAsString());
                        if (type != null) {
                            item.setItemMeta(meta);
                            item.addUnsafeEnchantment(type, enchant.get("level").getAsInt());
                            meta = item.getItemMeta();
                        }
                    }
                    break;
                }
                case "axolotl": {
                    if (meta instanceof AxolotlBucketMeta) {
                        ((AxolotlBucketMeta) meta).setVariant(Axolotl.Variant.valueOf(entry.getValue().getAsString()));
                    }
                    break;
                }
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Serializes a String from an ItemStack using json
     * @param item The ItemStack to serialize
     * @return The serialized String
     */
    public static @NotNull String serialize(final @NotNull ItemStack item) {
        final JsonObject json = new JsonObject();

        final ItemMeta meta = item.getItemMeta();
        assert meta != null;

        json.add("type", new JsonPrimitive(item.getType().toString()));

        if (meta.hasDisplayName()) json.add("name", new JsonPrimitive(meta.getDisplayName()));

        if (item.getAmount() != 1) json.add("amount", new JsonPrimitive(item.getAmount()));

        if (meta.hasLore()) {
            final JsonArray array = new JsonArray();
            for (String line : meta.getLore()) array.add(line);
            json.add("lore", array);
        }

        if (meta instanceof SkullMeta && ((SkullMeta) meta).getOwningPlayer() != null) {
            json.add("owner", new JsonPrimitive(Objects.requireNonNull(((SkullMeta) meta).getOwningPlayer().getName())));
        }

        if (item.getDurability() != 0) json.add("durability", new JsonPrimitive((int) item.getDurability()));

        if (meta.isUnbreakable()) json.add("unbreakable", new JsonPrimitive(meta.isUnbreakable()));

        if (meta.hasLocalizedName()) json.add("local-name", new JsonPrimitive(meta.getLocalizedName()));

        if (!meta.getItemFlags().isEmpty()) {
            final JsonArray array = new JsonArray();
            for (ItemFlag flag : meta.getItemFlags()) array.add(flag.toString());
            json.add("flags", array);
        }

        if (!meta.getEnchants().isEmpty()) {
            final JsonArray array = new JsonArray();
            for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                final JsonObject obj = new JsonObject();
                obj.add("type", new JsonPrimitive(entry.getKey().getName().toString()));
                obj.add("level", new JsonPrimitive(entry.getValue().intValue()));
                array.add(obj);
            }
            json.add("enchants", array);
        }

        if (meta instanceof AxolotlBucketMeta) {
            if (((AxolotlBucketMeta) meta).hasVariant()) json.add("axolotl",
                    new JsonPrimitive(((AxolotlBucketMeta) meta).getVariant().toString()));
        }

        return json.toString();
    }

}
