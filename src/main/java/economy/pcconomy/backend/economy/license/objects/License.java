package economy.pcconomy.backend.economy.license.objects;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.time.LocalDateTime;


@Getter
public class License {
    public License(Player owner, LocalDateTime term, LicenseType licenseType) {
        this.term = term.toString();
        this.owner = owner.getUniqueId();
        this.licenseType = licenseType;
    }

    private final UUID owner;
    private final String term;
    private final LicenseType licenseType;

    /**
     * Checks status of license
     * @return Status of license
     */
    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(LocalDateTime.parse(term));
    }
}
