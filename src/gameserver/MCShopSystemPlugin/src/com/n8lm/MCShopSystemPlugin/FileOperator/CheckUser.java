/*
 * CheckUser Class
 * MUST give USERNAME When created.
 * -> new CheckUser("UserName");
 * 
 * 	hasPassword
 * 	checkPassword
 */
package com.n8lm.MCShopSystemPlugin.FileOperator;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.n8lm.MCShopSystemPlugin.MainPlugin;

public class CheckUser{
	
	private String userName;
	private Player player;
	private static PasswordOperator Passwd = MainPlugin.getPasswordOperator();
	
	public CheckUser(String userName){
		this.userName = userName;
		this.player = MainPlugin.getBukkitServer().getPlayer(userName);
	}
	
	// Password
	
	public boolean hasPassword(){
		return Passwd.hasPasswd(userName);
	}

	public boolean checkPassword(String pass){
		return Passwd.checkPasswd(userName,pass);
	}
	
	// From Player Interface
	
	public boolean canSee(String targetPlayer){
		return player.canSee(MainPlugin.getBukkitServer().getPlayer(targetPlayer));
	}

	public boolean getAllowFlight(){
		return player.getAllowFlight();
	}
	
	public String getDisplayName(){
		return player.getDisplayName();
	}
	
	
	public float getExhaustion(){
		return player.getExhaustion();
	}
	
	
	public float getExp(){
		return player.getExp();
	}
	
	
	public int getFoodLevel(){
		return player.getFoodLevel();
	}
	
	
	public int getLevel(){
		return player.getLevel();
	}
	
	
	public InetSocketAddress getAddress(){
		return player.getAddress();
	}

	
	public void closeInventory(){
		player.closeInventory();
	}

	
	public Inventory getEnderChest() {
		return player.getEnderChest();
	}

	
	public int getExpToLevel() {
		return player.getExpToLevel();
	}

	
	public GameMode getGameMode() {
		return player.getGameMode();
	}

	
	public PlayerInventory getInventory() {
		return player.getInventory();
		
	}

	
	public ItemStack getItemInHand() {
		return player.getItemInHand();
		
	}

	
	public ItemStack getItemOnCursor() {
		return player.getItemOnCursor();
	}

	
	public String getName(){
		return userName;
	}

	
	public InventoryView getOpenInventory() {
		return player.getOpenInventory();
	}

	
	public int getSleepTicks() {
		return player.getSleepTicks();
	}

	
	public boolean isBlocking() {
		return player.isBlocking();
	}

	
	public boolean isSleeping() {
		return player.isSleeping();
	}

	
	public InventoryView openEnchanting(Location arg0, boolean arg1) {
		return player.openEnchanting(arg0,arg1);
	}

	
	public InventoryView openInventory(Inventory arg0) {
		return player.openInventory(arg0);
		
	}
/*
	
	public void openInventory(InventoryView arg0) {
		return player.
		
	}

	
	public InventoryView openWorkbench(Location arg0, boolean arg1) {
		return player.
		
	}

	
	public void setGameMode(GameMode arg0) {
		return player.
		
	}

	
	public void setItemInHand(ItemStack arg0) {
		return player.
		
	}

	
	public void setItemOnCursor(ItemStack arg0) {
		return player.
		
	}

	
	public boolean setWindowProperty(Property arg0, int arg1) {
		return player.
		return false;
	}

	
	public boolean addPotionEffect(PotionEffect arg0) {
		return player.
		return false;
	}

	
	public boolean addPotionEffect(PotionEffect arg0, boolean arg1) {
		return player.
		return false;
	}

	
	public boolean addPotionEffects(Collection<PotionEffect> arg0) {
		return player.
		return false;
	}

	
	public Collection<PotionEffect> getActivePotionEffects() {
		return player.
		
	}

	
	public boolean getCanPickupItems() {
		return player.
		return false;
	}

	
	public String getCustomName() {
		return player.
		
	}

	
	public EntityEquipment getEquipment() {
		return player.
		
	}

	
	public double getEyeHeight() {
		return player.
		return 0;
	}

	
	public double getEyeHeight(boolean arg0) {
		return player.
		return 0;
	}

	
	public Location getEyeLocation() {
		return player.
		
	}

	
	public Player getKiller() {
		return player.
		
	}

	
	public int getLastDamage() {
		return player.
		return 0;
	}

	
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> arg0, int arg1) {
		return player.
		
	}

	
	public List<Block> getLineOfSight(HashSet<Byte> arg0, int arg1) {
		return player.
		
	}

	
	public int getMaximumAir() {
		return player.
		return 0;
	}

	
	public int getMaximumNoDamageTicks() {
		return player.
		return 0;
	}

	
	public int getNoDamageTicks() {
		return player.
		return 0;
	}

	
	public int getRemainingAir() {
		return player.
		return 0;
	}

	
	public boolean getRemoveWhenFarAway() {
		return player.
		return false;
	}

	
	public Block getTargetBlock(HashSet<Byte> arg0, int arg1) {
		return player.
		
	}

	
	public boolean hasLineOfSight(Entity arg0) {
		return player.
		return false;
	}

	
	public boolean hasPotionEffect(PotionEffectType arg0) {
		return player.
		return false;
	}

	
	public boolean isCustomNameVisible() {
		return player.
		return false;
	}

	
	public <T extends Projectile> T launchProjectile(Class<? extends T> arg0) {
		return player.
		
	}

	
	public void removePotionEffect(PotionEffectType arg0) {
		return player.
		
	}

	
	public void setCanPickupItems(boolean arg0) {
		return player.
		
	}

	
	public void setCustomName(String arg0) {
		return player.
		
	}

	
	public void setCustomNameVisible(boolean arg0) {
		return player.
		
	}

	
	public void setLastDamage(int arg0) {
		return player.
		
	}

	
	public void setMaximumAir(int arg0) {
		return player.
		
	}

	
	public void setMaximumNoDamageTicks(int arg0) {
		return player.
		
	}

	
	public void setNoDamageTicks(int arg0) {
		return player.
		
	}

	
	public void setRemainingAir(int arg0) {
		return player.
		
	}

	
	public void setRemoveWhenFarAway(boolean arg0) {
		return player.
		
	}

	
	@Deprecated
	public Arrow shootArrow() {
		return player.
		
	}

	
	@Deprecated
	public Egg throwEgg() {
		return player.
		
	}

	
	public boolean eject() {
		return player.
		return false;
	}

	
	public int getEntityId() {
		return player.
		return 0;
	}

	
	public float getFallDistance() {
		return player.
		return 0;
	}

	
	public int getFireTicks() {
		return player.
		return 0;
	}

	
	public EntityDamageEvent getLastDamageCause() {
		return player.
		
	}

	
	public Location getLocation() {
		return player.
		
	}

	
	public Location getLocation(Location arg0) {
		return player.
		
	}

	
	public int getMaxFireTicks() {
		return player.
		return 0;
	}

	
	public List<Entity> getNearbyEntities(double arg0, double arg1, double arg2) {
		return player.
		
	}

	
	public Entity getPassenger() {
		return player.
		
	}

	
	public Server getServer() {
		return player.
		
	}

	
	public int getTicksLived() {
		return player.
		return 0;
	}

	
	public EntityType getType() {
		return player.
		
	}

	
	public UUID getUniqueId() {
		return player.
		
	}

	
	public Entity getVehicle() {
		return player.
		
	}

	
	public Vector getVelocity() {
		return player.
		
	}

	
	public World getWorld() {
		return player.
		
	}

	
	public boolean isDead() {
		return player.
		return false;
	}

	
	public boolean isEmpty() {
		return player.
		return false;
	}

	
	public boolean isInsideVehicle() {
		return player.
		return false;
	}

	
	public boolean isValid() {
		return player.
		return false;
	}

	
	public boolean leaveVehicle() {
		return player.
		return false;
	}

	
	public void playEffect(EntityEffect arg0) {
		return player.
		
	}

	
	public void remove() {
		return player.
		
	}

	
	public void setFallDistance(float arg0) {
		return player.
		
	}

	
	public void setFireTicks(int arg0) {
		return player.
		
	}

	
	public void setLastDamageCause(EntityDamageEvent arg0) {
		return player.
		
	}

	
	public boolean setPassenger(Entity arg0) {
		return player.
		return false;
	}

	
	public void setTicksLived(int arg0) {
		return player.
		
	}

	
	public void setVelocity(Vector arg0) {
		return player.
		
	}

	
	public boolean teleport(Location arg0) {
		return player.
		return false;
	}

	
	public boolean teleport(Entity arg0) {
		return player.
		return false;
	}

	
	public boolean teleport(Location arg0, TeleportCause arg1) {
		return player.
		return false;
	}

	
	public boolean teleport(Entity arg0, TeleportCause arg1) {
		return player.
		return false;
	}

	
	public List<MetadataValue> getMetadata(String arg0) {
		return player.
		
	}

	
	public boolean hasMetadata(String arg0) {
		return player.
		return false;
	}

	
	public void removeMetadata(String arg0, Plugin arg1) {
		return player.
		
	}

	
	public void setMetadata(String arg0, MetadataValue arg1) {
		return player.
		
	}

	
	public void damage(int arg0) {
		return player.
		
	}

	
	public void damage(int arg0, Entity arg1) {
		return player.
		
	}

	
	public int getHealth() {
		return player.
		return 0;
	}

	
	public int getMaxHealth() {
		return player.
		return 0;
	}

	
	public void resetMaxHealth() {
		return player.
		
	}

	
	public void setHealth(int arg0) {
		return player.
		
	}

	
	public void setMaxHealth(int arg0) {
		return player.
		
	}

	
	public PermissionAttachment addAttachment(Plugin arg0) {
		return player.
		
	}

	
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return player.
		
	}

	
	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2) {
		return player.
		
	}

	
	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2, int arg3) {
		return player.
		
	}

	
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return player.
		
	}

	
	public boolean hasPermission(String arg0) {
		return player.
		return false;
	}

	
	public boolean hasPermission(Permission arg0) {
		return player.
		return false;
	}

	
	public boolean isPermissionSet(String arg0) {
		return player.
		return false;
	}

	
	public boolean isPermissionSet(Permission arg0) {
		return player.
		return false;
	}

	
	public void recalculatePermissions() {
		return player.
		
	}

	
	public void removeAttachment(PermissionAttachment arg0) {
		return player.
		
	}

	
	public boolean isOp() {
		return player.
		return false;
	}

	
	public void setOp(boolean arg0) {
		return player.
		
	}

	
	public void abandonConversation(Conversation arg0) {
		return player.
		
	}

	
	public void abandonConversation(Conversation arg0,
			ConversationAbandonedEvent arg1) {
		return player.
		
	}

	
	public void acceptConversationInput(String arg0) {
		return player.
		
	}

	
	public boolean beginConversation(Conversation arg0) {
		return player.
		return false;
	}

	
	public boolean isConversing() {
		return player.
		return false;
	}

	
	public void sendMessage(String arg0) {
		return player.
		
	}

	
	public void sendMessage(String[] arg0) {
		return player.
		
	}

	
	public long getFirstPlayed() {
		return player.
		return 0;
	}

	
	public long getLastPlayed() {
		return player.
		return 0;
	}

	
	public Player getPlayer() {
		return player.
		
	}

	
	public boolean hasPlayedBefore() {
		return player.
		return false;
	}

	
	public boolean isBanned() {
		return player.
		return false;
	}

	
	public boolean isOnline() {
		return player.
		return false;
	}

	
	public boolean isWhitelisted() {
		return player.
		return false;
	}

	
	public void setBanned(boolean arg0) {
		return player.
		
	}

	
	public void setWhitelisted(boolean arg0) {
		return player.
		
	}

	
	public Map<String, Object> serialize() {
		return player.
		
	}

	
	public Set<String> getListeningPluginChannels() {
		return player.
		
	}

	
	public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {
		return player.
		
	}

	
	public void awardAchievement(Achievement arg0) {
		return player.
		
	}

	
	public boolean canSee(Player arg0) {
		return player.
		return false;
	}

	
	public void chat(String arg0) {
		return player.
		
	}

	
	public Location getBedSpawnLocation() {
		return player.
		
	}

	
	public Location getCompassTarget() {
		return player.
		
	}

	
	public float getFlySpeed() {
		return player.
		return 0;
	}

	
	public String getPlayerListName() {
		return player.
		
	}

	
	public long getPlayerTime() {
		return player.
		return 0;
	}

	
	public long getPlayerTimeOffset() {
		return player.
		return 0;
	}

	
	public WeatherType getPlayerWeather() {
		return player.
		
	}

	
	public float getSaturation() {
		return player.
		return 0;
	}

	
	public Scoreboard getScoreboard() {
		return player.
	}

	
	public int getTotalExperience() {
		return player.
		return 0;
	}

	
	public float getWalkSpeed() {
		return player.
		return 0;
	}

	
	public void giveExp(int arg0) {
		return player.
		
	}

	
	public void giveExpLevels(int arg0) {
		return player.
		
	}

	
	public void hidePlayer(Player arg0) {
		return player.
		
	}

	
	public void incrementStatistic(Statistic arg0) {
		return player.
		
	}

	
	public void incrementStatistic(Statistic arg0, int arg1) {
		return player.
		
	}

	
	public void incrementStatistic(Statistic arg0, Material arg1) {
		return player.
		
	}

	
	public void incrementStatistic(Statistic arg0, Material arg1, int arg2) {
		return player.
		
	}

	
	public boolean isFlying() {
		return player.
		return false;
	}

	
	public boolean isPlayerTimeRelative() {
		return player.
		return false;
	}

	
	public boolean isSleepingIgnored() {
		return player.
		return false;
	}

	
	public boolean isSneaking() {
		return player.
		return false;
	}

	
	public boolean isSprinting() {
		return player.
		return false;
	}

	
	public void kickPlayer(String arg0) {
		return player.
		
	}

	
	public void loadData() {
		return player.
		
	}

	
	public boolean performCommand(String arg0) {
		return player.
		return false;
	}

	
	public void playEffect(Location arg0, Effect arg1, int arg2) {
		return player.
		
	}

	
	public <T> void playEffect(Location arg0, Effect arg1, T arg2) {
		return player.
		
	}

	
	public void playNote(Location arg0, byte arg1, byte arg2) {
		return player.
		
	}

	
	public void playNote(Location arg0, Instrument arg1, Note arg2) {
		return player.
		
	}

	
	public void playSound(Location arg0, Sound arg1, float arg2, float arg3) {
		return player.
		
	}
	
	public void resetPlayerTime() {
		return player.
		
	}
	
	public void resetPlayerWeather() {
		return player.
		
	}
	
	public void saveData() {
		return player.
		
	}
	
	public void sendBlockChange(Location arg0, Material arg1, byte arg2) {
		return player.
		
	}
	
	public void sendBlockChange(Location arg0, int arg1, byte arg2) {
		return player.
		
	}
	
	public boolean sendChunkChange(Location arg0, int arg1, int arg2, int arg3,
			byte[] arg4) {
		return player.
		return false;
	}
	
	public void sendMap(MapView arg0) {
		return player.
		
	}
	
	public void sendRawMessage(String arg0) {
		return player.
		
	}
	
	public void setAllowFlight(boolean arg0) {
		return player.
		
	}
	
	public void setBedSpawnLocation(Location arg0) {
		return player.
		
	}
	
	public void setBedSpawnLocation(Location arg0, boolean arg1) {
		return player.
		
	}

	public void setCompassTarget(Location arg0) {
		return player.
		
	}

	public void setDisplayName(String arg0) {
		return player.
		
	}

	public void setExhaustion(float arg0) {
		return player.
		
	}

	public void setExp(float arg0) {
		return player.
		
	}

	public void setFlySpeed(float arg0) throws IllegalArgumentException {
		return player.
		
	}

	public void setFlying(boolean arg0) {
		return player.
		
	}
	
	public void setFoodLevel(int arg0) {
		return player.
		
	}
	
	public void setLevel(int arg0) {
		return player.
		
	}

	
	public void setPlayerListName(String arg0) {
		return player.
		
	}
	
	public void setPlayerTime(long arg0, boolean arg1) {
		return player.
		
	}

	
	public void setPlayerWeather(WeatherType arg0) {
		return player.
		
	}
	
	public void setSaturation(float arg0) {
		return player.
		
	}
	
	public void setScoreboard(Scoreboard arg0) throws IllegalArgumentException,
			IllegalStateException {
		return player.
		
	}

	public void setSleepingIgnored(boolean arg0) {
		return player.
		
	}

	
	public void setSneaking(boolean arg0) {
		return player.
		
	}

	public void setSprinting(boolean arg0) {
		return player.
		
	}

	public void setTexturePack(String arg0) {
		return player.
		
	}

	public void setTotalExperience(int arg0) {
		return player.
		
	}

	public void setWalkSpeed(float arg0) throws IllegalArgumentException {
		return player.
		
	}

	public void showPlayer(Player arg0) {
		return player.
		
	}
	*/
}