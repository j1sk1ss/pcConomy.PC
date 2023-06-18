package economy.pcconomy.backend.placeholderapi;

import economy.pcconomy.backend.cash.scripts.CashManager;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class PcConomyPAPI extends PlaceholderExpansion {
	
	@Override
	public @NotNull String getAuthor() { // Реализация API
		return "PotolotCraft Team";
	}
	
	@Override
	public @NotNull String getIdentifier() {
		return "pcconomy";
	}
	
	@Override
	public @NotNull String getVersion() {
		return "1.0.4";
	}
	
	@Override 
	public boolean persist() { // papi moment
		return true;
	}
	
	@Override
	public String onRequest(OfflinePlayer ply, String params) {
		if (params.substring(0, 5).equalsIgnoreCase("aleph")) {
			if (params.length() == 5) return CashManager.currencyName;
			
			/* Склонение по падежам происхоидит следующим образом
			 * %pcconomy_alephПЧ%
			 * где  П: первая буква названия падежа на латинице, 
			 * 		Ч: 's' - единственное число, 'p' - множественное.  
			 */
			return CashManager.getCurrencyNameCase(params.substring(5,7));
		}
		
		return null;
	}
	
}
