package com.rooxchicken.hourlycustomitems;

import java.util.ArrayList;

import javax.swing.ToolTipManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.common.value.qual.EnumVal;


public class App extends JavaPlugin implements Listener
{
    
    public NamespacedKey totemCooldownkey = new NamespacedKey(this, "totemcooldown");

    @Override
    public void onEnable()
    {
        //getServer().getPluginManager().registerEvents(new OnTotemUse(), this); 
        this.getCommand("givetotem").setExecutor(new GiveTotem());
        getServer().getPluginManager().registerEvents(this, this); 
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() // for totem cooldown
        {
            public void run()
            {
                for(Player p : getServer().getOnlinePlayers())
                {
                    for(ItemStack item : p.getInventory())
                    {
                        handleItemLogic(item);
                    }
                }
            }
        }, 0, 20);
        getLogger().info("Being a simple test plugin since 1987");
    }

    public void handleItemLogic(ItemStack item)
    {
        if(item == null || item.getType() != Material.TOTEM_OF_UNDYING || !item.getEnchantments().containsKey(Enchantment.DURABILITY))
            return;

        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if(container.has(totemCooldownkey, PersistentDataType.INTEGER))
        {
            ArrayList<String> lore = new ArrayList<String>();
            String time = "Cooldown: ";

            int foundValue = container.get(totemCooldownkey, PersistentDataType.INTEGER);
            foundValue--;
            container.set(totemCooldownkey, PersistentDataType.INTEGER, foundValue);

            if(foundValue > 0)
            {
                int nt = foundValue;
                int hr = nt / 3600;
                if(hr > 0)
                    time += hr + "hr ";
                nt %= 3600;
                int m = nt / 60;
                if(m > 0)
                    time += m + "m ";
                nt %= 60;
                time += nt + "s";

                lore.add(time);
            }
            else
                container.remove(totemCooldownkey);

            meta.setLore(lore);
            item.setItemMeta(meta);
        }                        
    }

    @Override
    public void onDisable()
    {
        getLogger().info("bye bye test plugin");
    }

    @EventHandler
    public void playerItemConsumeEvent(EntityResurrectEvent event)
    {
        EquipmentSlot hand = EquipmentSlot.HAND;
        ItemStack totemItemStack = event.getEntity().getEquipment().getItem(hand);

        if (totemItemStack == null || totemItemStack.getType() != Material.TOTEM_OF_UNDYING)
        {
            totemItemStack = event.getEntity().getEquipment().getItem(hand = EquipmentSlot.OFF_HAND);

            if (totemItemStack == null || totemItemStack.getType() != Material.TOTEM_OF_UNDYING)
                return;
        }

        if(totemItemStack.getEnchantments().containsKey(Enchantment.DURABILITY))
        {
            ItemMeta meta = totemItemStack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            if(container.has(totemCooldownkey, PersistentDataType.INTEGER))
            {
                int foundValue = container.get(totemCooldownkey, PersistentDataType.INTEGER);
                if(foundValue > 0)
                {
                    event.setCancelled(true);
                    return;
                }
            }

            ArrayList<String> lore = new ArrayList<String>();
            lore.add("Cooldown: 6hr");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(totemCooldownkey, PersistentDataType.INTEGER, 360*60);
            totemItemStack.setItemMeta(meta);

            totemItemStack.setAmount(totemItemStack.getAmount() + 1);
        }
        else
            event.setCancelled(true);
    }
}