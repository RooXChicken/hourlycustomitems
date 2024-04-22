package com.rooxchicken.hourlycustomitems;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class GiveTotem implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!sender.isOp())
        {
            sender.sendMessage("You need to be OP silly!");
            return false;
        }
        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);
        totem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        Bukkit.getServer().getPlayer(sender.getName()).getInventory().addItem(totem);
        return true;
    }

}
