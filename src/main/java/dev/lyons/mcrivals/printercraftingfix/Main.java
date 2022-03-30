package dev.lyons.mcrivals.printercraftingfix;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.golfing8.kore.event.PlayerPrinterEnterEvent;
import com.golfing8.kore.event.PlayerPrinterExitEvent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Main extends JavaPlugin implements Listener {

	public void onEnable(){
		getServer().getPluginManager().registerEvents(this,this);
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Main.this, ListenerPriority.HIGHEST, PacketType.Play.Client.SET_CREATIVE_SLOT) {
			public void onPacketReceiving(final PacketEvent e) {
				if (e.getPacketType() != PacketType.Play.Client.SET_CREATIVE_SLOT) return;
				Bukkit.getScheduler().callSyncMethod(Main.this, () -> {
					EntityPlayer ep = ((CraftPlayer) e.getPlayer()).getHandle();
					PacketPlayInSetCreativeSlot packet = (PacketPlayInSetCreativeSlot) e.getPacket().getHandle();
					PlayerConnectionUtils.ensureMainThread(packet, ep.playerConnection, ep.u());
					int slot = packet.a();
					if (slot < 1 || slot > 4)return null;
					e.setCancelled(true);
					return null;
				});
			}
		});
	}
	Set<UUID> printerPlayers = new HashSet<>();
	@EventHandler
	public void printerEvent(PlayerPrinterEnterEvent e){
		printerPlayers.add(e.getPlayer().getUniqueId());
	}
	@EventHandler
	public void printerEvent(PlayerPrinterExitEvent e){
		printerPlayers.remove(e.getPlayer().getUniqueId());
	}
	@EventHandler
	public void playerQuit(PlayerQuitEvent e){
		printerPlayers.remove(e.getPlayer().getUniqueId());
	}
}
